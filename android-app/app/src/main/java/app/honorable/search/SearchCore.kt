package app.honorable.search

import kotlin.math.sqrt

data class MediaRecord(
    val id: Long, val kind: MediaKind, val capturedAtEpochMs: Long,
    val location: String? = null, val ocr: String = "", val labels: Set<String> = emptySet(),
    val embedding: FloatArray? = null, val videoFrames: List<VideoFrame> = emptyList()
)
enum class MediaKind { IMAGE, VIDEO }
data class VideoFrame(val timestampMs: Long, val ocr: String, val labels: Set<String>, val embedding: FloatArray?)
data class SearchQuery(val terms: List<String>, val mediaKind: MediaKind? = null, val afterEpochMs: Long? = null, val beforeEpochMs: Long? = null, val location: String? = null)
data class SearchMatch(val media: MediaRecord, val score: Double, val explanations: List<String>, val bestTimestampMs: Long? = null)

interface EmbeddingService { val modelId: String; val dimension: Int; fun image(bytes: ByteArray): FloatArray?; fun text(query: String): FloatArray? }
interface OCRService { fun recognize(bytes: ByteArray): String }
interface VideoAnalysisService { fun representativeFrames(uri: String, cancellation: () -> Boolean): Sequence<VideoFrame> }
interface MediaIndexer { fun synchronize(cancellation: () -> Boolean, paused: () -> Boolean): IndexStats }
interface VectorIndex { fun upsert(id: Long, vector: FloatArray); fun nearest(vector: FloatArray, limit: Int): List<Pair<Long, Double>> }
data class IndexStats(val added: Int, val updated: Int, val deleted: Int)
data class IndexCompatibility(val schemaVersion: Int = 2, val modelId: String = TinyClipEmbeddingService.MODEL_ID, val embeddingDimension: Int = 512)

class QueryParser {
    fun parse(raw: String): SearchQuery {
        val lower = raw.lowercase()
        val kind = when { "video" in lower -> MediaKind.VIDEO; "photo" in lower || "picture" in lower -> MediaKind.IMAGE; else -> null }
        val stop = setOf("find", "the", "a", "an", "where", "my", "was", "in", "at", "of", "photo", "picture", "video")
        return SearchQuery(lower.replace(Regex("[^a-z0-9 ]"), " ").split(Regex("\\s+")).filter { it.isNotBlank() && it !in stop }, mediaKind = kind)
    }
}

class SearchRanker {
    fun rank(query: SearchQuery, records: List<MediaRecord>, queryVector: FloatArray? = null): List<SearchMatch> = records.asSequence()
        .filter { query.mediaKind == null || it.kind == query.mediaKind }
        .filter { query.afterEpochMs == null || it.capturedAtEpochMs >= query.afterEpochMs }
        .filter { query.beforeEpochMs == null || it.capturedAtEpochMs <= query.beforeEpochMs }
        .filter { query.location == null || it.location?.contains(query.location, true) == true }
        .map { score(query, it, queryVector) }.filter { it.score > 0 }.sortedByDescending { it.score }.toList()

    private fun score(q: SearchQuery, media: MediaRecord, vector: FloatArray?): SearchMatch {
        val explanations = mutableListOf<String>()
        val ocrHits = q.terms.count { media.ocr.contains(it, true) }; if (ocrHits > 0) explanations += "Text in media matches"
        val labelHits = q.terms.count { term -> media.labels.any { fuzzy(term, it) } }; if (labelHits > 0) explanations += "Visual labels match"
        val semantic = if (vector != null && media.embedding != null) cosine(vector, media.embedding) else 0.0
        if (semantic > .25) explanations += "Meaning is similar"
        val bestFrame = media.videoFrames.maxByOrNull { frame -> q.terms.count { it in frame.ocr.lowercase() || frame.labels.any { label -> fuzzy(it, label) } } }
        val frameHits = bestFrame?.let { f -> q.terms.count { it in f.ocr.lowercase() || f.labels.any { label -> fuzzy(it, label) } } } ?: 0
        if (frameHits > 0) explanations += "Match at ${bestFrame!!.timestampMs / 1000}s"
        return SearchMatch(media, ocrHits * 3.0 + labelHits * 2.0 + semantic.coerceAtLeast(0.0) * 2.5 + frameHits * 2.0, explanations, bestFrame?.timestampMs)
    }
    private fun fuzzy(a: String, b: String) = a == b.lowercase() || b.lowercase().contains(a) || (a.length > 3 && levenshtein(a, b.lowercase()) <= 1)
    private fun levenshtein(a: String, b: String): Int { var prev = IntArray(b.length + 1) { it }; for (i in a.indices) { val cur = IntArray(b.length + 1); cur[0] = i + 1; for (j in b.indices) cur[j + 1] = minOf(cur[j] + 1, prev[j + 1] + 1, prev[j] + if (a[i] == b[j]) 0 else 1); prev = cur }; return prev[b.length] }
    private fun cosine(a: FloatArray, b: FloatArray): Double { if (a.size != b.size) return 0.0; var dot=0.0; var aa=0.0; var bb=0.0; a.indices.forEach { dot += a[it]*b[it]; aa += a[it]*a[it]; bb += b[it]*b[it] }; return if (aa==0.0||bb==0.0) 0.0 else dot/sqrt(aa*bb) }
}

class TinyClipEmbeddingService : EmbeddingService {
    companion object { const val MODEL_ID = "TinyCLIP-ViT-8M-16-Text-3M-YFCC15M-int8" }
    override val modelId = MODEL_ID; override val dimension = 512
    override fun image(bytes: ByteArray): FloatArray? = null // ONNX asset must be supplied and validated before enabling.
    override fun text(query: String): FloatArray? = null
}

/** In-memory exact implementation behind the VectorIndex boundary; replaceable by persisted LSH buckets. */
class LocalVectorIndex : VectorIndex {
    private val vectors = mutableMapOf<Long, FloatArray>()
    override fun upsert(id: Long, vector: FloatArray) { vectors[id] = normalized(vector) }
    override fun nearest(vector: FloatArray, limit: Int): List<Pair<Long, Double>> {
        val q = normalized(vector)
        return vectors.map { (id, value) -> id to q.indices.sumOf { i -> (q.getOrElse(i){0f} * value.getOrElse(i){0f}).toDouble() } }.sortedByDescending { it.second }.take(limit)
    }
    private fun normalized(value: FloatArray): FloatArray { val norm=sqrt(value.sumOf { (it*it).toDouble() }).toFloat(); return if(norm==0f) value.copyOf() else FloatArray(value.size){value[it]/norm} }
}
