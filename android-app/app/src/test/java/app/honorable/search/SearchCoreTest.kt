package app.honorable.search

import org.junit.Assert.*
import org.junit.Test

class SearchCoreTest {
    @Test fun `query parser extracts media type and useful terms`() { val q=QueryParser().parse("find the video where my son was playing tennis outside"); assertEquals(MediaKind.VIDEO,q.mediaKind); assertTrue(q.terms.containsAll(listOf("son","playing","tennis","outside"))) }
    @Test fun `ocr is weighted above labels`() { val q=SearchQuery(listOf("flight")); val o=MediaRecord(1,MediaKind.IMAGE,0,ocr="FLIGHT 302"); val l=MediaRecord(2,MediaKind.IMAGE,0,labels=setOf("flight")); assertEquals(1L,SearchRanker().rank(q,listOf(l,o)).first().media.id) }
    @Test fun `hard media filter applies`() { val q=SearchQuery(listOf("beach"),mediaKind=MediaKind.VIDEO); val p=MediaRecord(1,MediaKind.IMAGE,0,labels=setOf("beach")); val v=MediaRecord(2,MediaKind.VIDEO,0,labels=setOf("beach")); assertEquals(listOf(2L),SearchRanker().rank(q,listOf(p,v)).map{it.media.id}) }
    @Test fun `semantic vector affects ranking`() { val q=SearchQuery(listOf("moment")); val a=MediaRecord(1,MediaKind.IMAGE,0,embedding=floatArrayOf(1f,0f)); val b=MediaRecord(2,MediaKind.IMAGE,0,embedding=floatArrayOf(0f,1f)); assertEquals(1L,SearchRanker().rank(q,listOf(b,a),floatArrayOf(1f,0f)).first().media.id) }
    @Test fun `video aggregation retains best timestamp`() { val f=VideoFrame(42000,"birthday cake",emptySet(),null); val v=MediaRecord(3,MediaKind.VIDEO,0,videoFrames=listOf(f)); assertEquals(42000L,SearchRanker().rank(SearchQuery(listOf("birthday","cake")),listOf(v)).first().bestTimestampMs) }
    @Test fun `index compatibility pins schema model and dimension`() { val c=IndexCompatibility(); assertEquals(2,c.schemaVersion); assertEquals(512,c.embeddingDimension); assertTrue(c.modelId.contains("TinyCLIP")) }
    @Test fun `vector index returns nearest normalized vector`() { val index=LocalVectorIndex(); index.upsert(1,floatArrayOf(1f,0f)); index.upsert(2,floatArrayOf(0f,1f)); assertEquals(1L,index.nearest(floatArrayOf(.9f,.1f),1).single().first) }
}
