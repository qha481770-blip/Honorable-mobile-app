package app.honorable.search

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/** Versioned, bounded local index. Raw media and frames are never copied into it. */
class LocalMediaDatabase(context: Context) : SQLiteOpenHelper(context, "honorable-media.db", null, SCHEMA_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""CREATE TABLE media_index(id INTEGER PRIMARY KEY, uri TEXT NOT NULL UNIQUE, kind TEXT NOT NULL, captured_at INTEGER NOT NULL, location TEXT, ocr TEXT NOT NULL DEFAULT '', labels TEXT NOT NULL DEFAULT '', embedding BLOB, model_id TEXT, embedding_dimension INTEGER, indexed_at INTEGER NOT NULL)""")
        db.execSQL("""CREATE TABLE video_frame(media_id INTEGER NOT NULL, timestamp_ms INTEGER NOT NULL, ocr TEXT NOT NULL DEFAULT '', labels TEXT NOT NULL DEFAULT '', embedding BLOB, PRIMARY KEY(media_id,timestamp_ms), FOREIGN KEY(media_id) REFERENCES media_index(id) ON DELETE CASCADE)""")
        db.execSQL("CREATE INDEX media_captured_at ON media_index(captured_at)")
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Future migrations must be additive. A model/dimension mismatch invalidates embeddings only, not OCR/metadata.
        if (oldVersion < 2) db.execSQL("ALTER TABLE media_index ADD COLUMN content_modified_at INTEGER NOT NULL DEFAULT 0")
    }
    fun removeDeleted(existingUris: Set<String>) {
        writableDatabase.query("media_index", arrayOf("uri"), null, null, null, null, null).use { cursor ->
            val stale = mutableListOf<String>(); while (cursor.moveToNext()) cursor.getString(0).takeIf { it !in existingUris }?.let(stale::add)
            writableDatabase.beginTransaction(); try { stale.chunked(250).forEach { batch -> batch.forEach { writableDatabase.delete("media_index", "uri=?", arrayOf(it)) } }; writableDatabase.setTransactionSuccessful() } finally { writableDatabase.endTransaction() }
        }
    }
    companion object { const val SCHEMA_VERSION = 2 }
}
