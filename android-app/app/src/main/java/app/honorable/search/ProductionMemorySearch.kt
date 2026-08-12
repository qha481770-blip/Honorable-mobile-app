package app.honorable.search

import android.Manifest
import android.app.Application
import android.content.ContentUris
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Size
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

data class IndexProgress(val processed:Int=0,val total:Int=0)
sealed interface MemorySearchState {
    data object PermissionRequired:MemorySearchState
    data class Indexing(val progress:IndexProgress):MemorySearchState
    data class Ready(val count:Int):MemorySearchState
    data class Results(val query:String,val matches:List<SearchMatch>):MemorySearchState
    data class Failed(val message:String):MemorySearchState
}

data class DiscoveredMedia(val id:Long,val uri:String,val kind:MediaKind,val capturedAt:Long,val modifiedAt:Long,val name:String,val durationMs:Long?)

class AndroidMediaIndexer(private val context:Context,private val database:LocalMediaDatabase) {
    private val resolver=context.contentResolver
    suspend fun synchronize(progress:(IndexProgress)->Unit):IndexStats=withContext(Dispatchers.IO) {
        val discovered=discover();val known=database.modifiedTimes();var added=0;var updated=0;var processed=0
        discovered.forEach { item ->
            if(known[item.uri]!=item.modifiedAt) {
                val bitmap=thumbnail(item)
                val screenshot=item.name.contains("screenshot",true)
                val labels=bitmap?.let{label(it)}?:emptySet()
                val colors=bitmap?.let{sampleColors(it)}?:emptySet()
                val ocr=if(screenshot&&bitmap!=null) recognize(bitmap) else ""
                database.upsert(MediaRecord(item.id,item.kind,item.capturedAt,ocr=ocr,labels=labels,metadataTerms=setOf(item.name,if(item.kind==MediaKind.VIDEO)"video" else "photo"),dominantColors=colors,isScreenshot=screenshot,uri=item.uri,displayName=item.name,durationMs=item.durationMs),item.modifiedAt)
                if(item.uri in known)updated++ else added++
            }
            progress(IndexProgress(++processed,discovered.size))
        }
        val uris=discovered.mapTo(mutableSetOf()){it.uri};val deleted=known.keys.count{it !in uris};database.removeDeleted(uris)
        IndexStats(added,updated,deleted)
    }
    private fun discover():List<DiscoveredMedia> {
        val result=mutableListOf<DiscoveredMedia>()
        fun query(collection:Uri,kind:MediaKind) {
            val projection=arrayOf(MediaStore.MediaColumns._ID,MediaStore.MediaColumns.DATE_TAKEN,MediaStore.MediaColumns.DATE_MODIFIED,MediaStore.MediaColumns.DISPLAY_NAME,if(kind==MediaKind.VIDEO)MediaStore.Video.VideoColumns.DURATION else MediaStore.MediaColumns.SIZE)
            resolver.query(collection,projection,null,null,"${MediaStore.MediaColumns.DATE_TAKEN} DESC")?.use { c ->
                val id=c.getColumnIndexOrThrow(MediaStore.MediaColumns._ID);val taken=c.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_TAKEN);val modified=c.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_MODIFIED);val name=c.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME);val last=c.getColumnIndexOrThrow(projection.last())
                while(c.moveToNext()){val mediaId=c.getLong(id);result+=DiscoveredMedia(mediaId,ContentUris.withAppendedId(collection,mediaId).toString(),kind,c.getLong(taken).takeIf{it>0}?:c.getLong(modified)*1000,c.getLong(modified)*1000,c.getString(name).orEmpty(),if(kind==MediaKind.VIDEO)c.getLong(last) else null)}
            }
        }
        query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,MediaKind.IMAGE);query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,MediaKind.VIDEO)
        return result
    }
    private fun thumbnail(item:DiscoveredMedia):Bitmap?=runCatching { resolver.loadThumbnail(Uri.parse(item.uri),Size(384,384),null) }.getOrNull()
    private suspend fun recognize(bitmap:Bitmap):String=suspendCancellableCoroutine { continuation ->
        val client=TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);client.process(InputImage.fromBitmap(bitmap,0)).addOnSuccessListener{continuation.resume(it.text)}.addOnFailureListener{continuation.resume("")}.addOnCompleteListener{client.close()}
    }
    private suspend fun label(bitmap:Bitmap):Set<String> = suspendCancellableCoroutine { continuation ->
        val client=ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS);client.process(InputImage.fromBitmap(bitmap,0)).addOnSuccessListener{labels->continuation.resume(labels.filter{it.confidence>=.55f}.mapTo(linkedSetOf()){it.text.lowercase()})}.addOnFailureListener{continuation.resume(emptySet())}.addOnCompleteListener{client.close()}
    }
    private fun sampleColors(bitmap:Bitmap):Set<String>{val w=bitmap.width;val h=bitmap.height;if(w==0||h==0)return emptySet();val pixels=IntArray(256){i->bitmap.getPixel((i%16*w/16).coerceAtMost(w-1),(i/16*h/16).coerceAtMost(h-1))};return ColorEvidenceAnalyzer.dominantColors(pixels)}
}

class MemoriesViewModel(application:Application):AndroidViewModel(application) {
    private val db=LocalMediaDatabase(application);private val indexer=AndroidMediaIndexer(application,db);private val parser=QueryParser();private val ranker=SearchRanker()
    private val mutableState=MutableStateFlow<MemorySearchState>(if(hasPermission(application))MemorySearchState.Indexing(IndexProgress()) else MemorySearchState.PermissionRequired)
    val state:StateFlow<MemorySearchState> = mutableState.asStateFlow()
    init { if(hasPermission(application))refresh() }
    fun permissionResult(){if(hasPermission(getApplication()))refresh()else mutableState.value=MemorySearchState.PermissionRequired}
    fun refresh(){viewModelScope.launch { mutableState.value=MemorySearchState.Indexing(IndexProgress());runCatching{indexer.synchronize{mutableState.value=MemorySearchState.Indexing(it)};db.records()}.onSuccess{mutableState.value=MemorySearchState.Ready(it.size)}.onFailure{mutableState.value=MemorySearchState.Failed(it.message?:"Unable to index media")}}}
    fun search(raw:String){viewModelScope.launch(Dispatchers.Default){val records=db.records();val query=parser.parse(raw);val matches=ranker.rank(query,records);mutableState.value=MemorySearchState.Results(raw,matches)}}
    companion object {
        fun permissions()=if(Build.VERSION.SDK_INT>=33)arrayOf(Manifest.permission.READ_MEDIA_IMAGES,Manifest.permission.READ_MEDIA_VIDEO)else arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        fun hasPermission(context:Context)=permissions().all{ContextCompat.checkSelfPermission(context,it)==PackageManager.PERMISSION_GRANTED}
    }
}
