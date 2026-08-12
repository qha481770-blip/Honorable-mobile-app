package app.honorable.search

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues
import java.nio.ByteBuffer
import java.nio.ByteOrder

/** Versioned, bounded local index. Raw media and frames are never copied into it. */
class LocalMediaDatabase(context: Context) : SQLiteOpenHelper(context, "honorable-media.db", null, SCHEMA_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""CREATE TABLE media_index(id INTEGER PRIMARY KEY, uri TEXT NOT NULL UNIQUE, kind TEXT NOT NULL, captured_at INTEGER NOT NULL, location TEXT, latitude REAL, longitude REAL, ocr TEXT NOT NULL DEFAULT '', labels TEXT NOT NULL DEFAULT '', dominant_colors TEXT NOT NULL DEFAULT '', embedding BLOB, model_id TEXT, embedding_dimension INTEGER, preprocessing_version TEXT, indexed_at INTEGER NOT NULL, content_modified_at INTEGER NOT NULL DEFAULT 0, display_name TEXT NOT NULL DEFAULT '', duration_ms INTEGER)""")
        db.execSQL("""CREATE TABLE video_frame(media_id INTEGER NOT NULL, timestamp_ms INTEGER NOT NULL, ocr TEXT NOT NULL DEFAULT '', labels TEXT NOT NULL DEFAULT '', dominant_colors TEXT NOT NULL DEFAULT '', embedding BLOB, scene_fingerprint INTEGER, PRIMARY KEY(media_id,timestamp_ms), FOREIGN KEY(media_id) REFERENCES media_index(id) ON DELETE CASCADE)""")
        db.execSQL("CREATE INDEX media_captured_at ON media_index(captured_at)")
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Future migrations must be additive. A model/dimension mismatch invalidates embeddings only, not OCR/metadata.
        if (oldVersion < 2) db.execSQL("ALTER TABLE media_index ADD COLUMN content_modified_at INTEGER NOT NULL DEFAULT 0")
        if (oldVersion < 3) {
            db.execSQL("ALTER TABLE media_index ADD COLUMN latitude REAL")
            db.execSQL("ALTER TABLE media_index ADD COLUMN longitude REAL")
            db.execSQL("ALTER TABLE media_index ADD COLUMN dominant_colors TEXT NOT NULL DEFAULT ''")
            db.execSQL("ALTER TABLE media_index ADD COLUMN preprocessing_version TEXT")
            db.execSQL("ALTER TABLE video_frame ADD COLUMN dominant_colors TEXT NOT NULL DEFAULT ''")
            db.execSQL("ALTER TABLE video_frame ADD COLUMN scene_fingerprint INTEGER")
        }
        if (oldVersion < 4) {
            db.execSQL("ALTER TABLE media_index ADD COLUMN display_name TEXT NOT NULL DEFAULT ''")
            db.execSQL("ALTER TABLE media_index ADD COLUMN duration_ms INTEGER")
        }
    }
    fun upsert(record: MediaRecord, modifiedAt: Long) {
        val values=ContentValues().apply {
            put("id",record.id);put("uri",record.uri);put("kind",record.kind.name);put("captured_at",record.capturedAtEpochMs)
            put("location",record.location);put("ocr",record.ocr);put("labels",record.labels.joinToString(SEPARATOR));put("dominant_colors",record.dominantColors.joinToString(SEPARATOR))
            put("embedding",record.embedding?.let(::floatsToBytes));put("embedding_dimension",record.embedding?.size);put("indexed_at",System.currentTimeMillis());put("content_modified_at",modifiedAt)
            put("display_name",record.displayName);record.durationMs?.let{put("duration_ms",it)}
        }
        writableDatabase.insertWithOnConflict("media_index",null,values,SQLiteDatabase.CONFLICT_REPLACE)
    }
    fun modifiedTimes(): Map<String,Long> = readableDatabase.query("media_index",arrayOf("uri","content_modified_at"),null,null,null,null,null).use { c -> buildMap { while(c.moveToNext()) put(c.getString(0),c.getLong(1)) } }
    fun records(): List<MediaRecord> = readableDatabase.query("media_index",null,null,null,null,null,null).use { c -> buildList {
        fun text(name:String)=c.getString(c.getColumnIndexOrThrow(name))
        while(c.moveToNext()) add(MediaRecord(
            id=c.getLong(c.getColumnIndexOrThrow("id")),kind=MediaKind.valueOf(text("kind")),capturedAtEpochMs=c.getLong(c.getColumnIndexOrThrow("captured_at")),location=c.getString(c.getColumnIndexOrThrow("location")),
            ocr=text("ocr"),labels=text("labels").split(SEPARATOR).filter(String::isNotBlank).toSet(),embedding=c.getBlob(c.getColumnIndexOrThrow("embedding"))?.let(::bytesToFloats),
            dominantColors=text("dominant_colors").split(SEPARATOR).filter(String::isNotBlank).toSet(),isScreenshot=text("display_name").contains("screenshot",true),uri=text("uri"),displayName=text("display_name"),
            durationMs=c.getColumnIndexOrThrow("duration_ms").let { i -> if(c.isNull(i)) null else c.getLong(i) },metadataTerms=setOf(text("display_name"),if(MediaKind.valueOf(text("kind"))==MediaKind.VIDEO) "video" else "photo")
        ))
    } }
    fun removeDeleted(existingUris: Set<String>) {
        writableDatabase.query("media_index", arrayOf("uri"), null, null, null, null, null).use { cursor ->
            val stale = mutableListOf<String>(); while (cursor.moveToNext()) cursor.getString(0).takeIf { it !in existingUris }?.let(stale::add)
            writableDatabase.beginTransaction(); try { stale.chunked(250).forEach { batch -> batch.forEach { writableDatabase.delete("media_index", "uri=?", arrayOf(it)) } }; writableDatabase.setTransactionSuccessful() } finally { writableDatabase.endTransaction() }
        }
    }
    companion object {
        const val SCHEMA_VERSION = 4;private const val SEPARATOR="\u001f"
        private fun floatsToBytes(values:FloatArray)=ByteBuffer.allocate(values.size*4).order(ByteOrder.LITTLE_ENDIAN).apply{values.forEach(::putFloat)}.array()
        private fun bytesToFloats(bytes:ByteArray)=ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).let{b->FloatArray(bytes.size/4){b.float}}
    }
}
