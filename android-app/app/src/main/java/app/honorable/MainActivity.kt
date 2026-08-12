package app.honorable

import android.os.Build
import android.os.Bundle
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import app.honorable.search.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState); enableEdgeToEdge(); setContent { HonorableApp() } }
}

private val LightColors = lightColorScheme(primary=Color(0xFF075F84),onPrimary=Color.White,primaryContainer=Color(0xFFC7E9FA),secondary=Color(0xFF007B9F),tertiary=Color(0xFF4D5E91),background=Color(0xFFF4F9FC),surface=Color(0xFFF4F9FC),surfaceVariant=Color(0xFFDCEAF1),outlineVariant=Color(0xFFB8CDD8))
private val DarkColors = darkColorScheme(primary=Color(0xFF76D1FB),onPrimary=Color(0xFF003548),primaryContainer=Color(0xFF064B68),secondary=Color(0xFF54D6F5),tertiary=Color(0xFFB9C3FF),background=Color(0xFF061017),surface=Color(0xFF061017),surfaceVariant=Color(0xFF172A35),outlineVariant=Color(0xFF3B5968))
private val AppType = Typography(displaySmall=Typography().displaySmall.copy(fontWeight=FontWeight.Light,lineHeight=46.sp),headlineLarge=Typography().headlineLarge.copy(fontWeight=FontWeight.Normal,lineHeight=39.sp),headlineMedium=Typography().headlineMedium.copy(fontWeight=FontWeight.Normal),titleLarge=Typography().titleLarge.copy(fontWeight=FontWeight.Medium),bodyLarge=Typography().bodyLarge.copy(lineHeight=25.sp))

@Composable fun HonorableApp() {
    val dark=isSystemInDarkTheme();val context=LocalContext.current
    val colors=when{Build.VERSION.SDK_INT>=31&&dark->dynamicDarkColorScheme(context);Build.VERSION.SDK_INT>=31->dynamicLightColorScheme(context);dark->DarkColors;else->LightColors}
    MaterialTheme(colorScheme=colors,typography=AppType){AppBackground{HonorableShell()}}
}

@Composable private fun AppBackground(content:@Composable BoxScope.()->Unit){
    val c=MaterialTheme.colorScheme
    Box(Modifier.fillMaxSize().background(Brush.radialGradient(listOf(c.primary.copy(.14f),Color.Transparent),center=Offset(160f,80f),radius=880f)).background(c.background),content=content)
}

private enum class MainTab(val label:String,val icon:ImageVector){HOME("Home",Icons.Rounded.Home),MEMORIES("Memories",Icons.Rounded.PhotoLibrary),TERMS("Terms",Icons.Rounded.Policy),ACTIVITY("Activity",Icons.Rounded.Timeline),SETTINGS("Settings",Icons.Rounded.Tune)}
private enum class Overlay { NONE, VIEWER, PRIVACY, PLUS }

@Composable private fun HonorableShell(){
    var tab by rememberSaveable{mutableStateOf(MainTab.HOME)};var overlay by rememberSaveable{mutableStateOf(Overlay.NONE)};var selected by remember{mutableStateOf<SearchMatch?>(null)}
    BackHandler(overlay!=Overlay.NONE){overlay=Overlay.NONE}
    Box(Modifier.fillMaxSize()){
        AnimatedContent(tab,label="main-screen",transitionSpec={fadeIn(tween(180)) togetherWith fadeOut(tween(120))}){current->
            when(current){
                MainTab.HOME->HomeScreen({tab=MainTab.MEMORIES},{tab=MainTab.TERMS})
                MainTab.MEMORIES->MemoriesScreen{selected=it;overlay=Overlay.VIEWER}
                MainTab.TERMS->TermsScreen()
                MainTab.ACTIVITY->ActivityScreen()
                MainTab.SETTINGS->SettingsScreen({overlay=Overlay.PRIVACY},{overlay=Overlay.PLUS})
            }
        }
        FloatingGlassDock(tab,{tab=it},Modifier.align(Alignment.BottomCenter))
        AnimatedVisibility(overlay!=Overlay.NONE,enter=fadeIn()+scaleIn(initialScale=.97f),exit=fadeOut()+scaleOut(targetScale=.98f)){
            when(overlay){Overlay.VIEWER->selected?.let{MediaViewer(it){overlay=Overlay.NONE}}?:MediaViewer{overlay=Overlay.NONE};Overlay.PRIVACY->PrivacyScreen{overlay=Overlay.NONE};Overlay.PLUS->PlusScreen{overlay=Overlay.NONE};else->Unit}
        }
    }
}

enum class OrbState { IDLE, THINKING, SEARCHING, LISTENING, RESULT, LOW_CONFIDENCE, ERROR }

@Composable fun HonorableOrb(state:OrbState,modifier:Modifier=Modifier,size:Int=150,icon:ImageVector=Icons.Rounded.AutoAwesome){
    val motion=rememberInfiniteTransition(label="orb")
    val breath by motion.animateFloat(1f,1.055f,infiniteRepeatable(tween(if(state==OrbState.IDLE)2600 else 1100,easing=FastOutSlowInEasing),RepeatMode.Reverse),label="breath")
    val rotation by motion.animateFloat(0f,360f,infiniteRepeatable(tween(if(state==OrbState.SEARCHING)1800 else 8000,easing=LinearEasing)),label="rotation")
    val accent=when(state){OrbState.ERROR->MaterialTheme.colorScheme.error;OrbState.LOW_CONFIDENCE->MaterialTheme.colorScheme.tertiary;else->MaterialTheme.colorScheme.primary}
    Box(modifier.size(size.dp).graphicsLayer{scaleX=breath;scaleY=breath;rotationZ=rotation*.025f}.semantics{contentDescription="Honorable AI ${state.name.lowercase()}"},contentAlignment=Alignment.Center){
        Canvas(Modifier.fillMaxSize()){
            drawCircle(accent.copy(.09f),radius=this.size.minDimension*.5f)
            drawCircle(Brush.radialGradient(listOf(Color.White.copy(.72f),accent.copy(.34f),accent.copy(.10f))),radius=this.size.minDimension*.40f)
            drawArc(Brush.sweepGradient(listOf(Color.Transparent,accent,Color.White,Color.Transparent)),rotation,280f,false,style=Stroke(width=2.2.dp.toPx()))
            drawCircle(Color.White.copy(.28f),radius=this.size.minDimension*.27f,center=Offset(this.size.width*.43f,this.size.height*.39f))
        }
        Icon(icon,null,Modifier.size((size*.25f).dp).graphicsLayer{rotationZ=-rotation*.025f},tint=accent)
    }
}

@Composable private fun OrbButton(icon:ImageVector,label:String,onClick:()->Unit){
    Column(horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(7.dp)){
        Pressable(onClick){pressed->GlassCircle(Modifier.size(58.dp).graphicsLayer{scaleX=if(pressed).94f else 1f;scaleY=scaleX}){Icon(icon,label,Modifier.size(23.dp),tint=MaterialTheme.colorScheme.primary)}}
        Text(label,style=MaterialTheme.typography.labelMedium,color=MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable private fun HomeScreen(openMemories:()->Unit,openTerms:()->Unit){
    LazyColumn(contentPadding=PaddingValues(22.dp,46.dp,22.dp,130.dp),verticalArrangement=Arrangement.spacedBy(25.dp)){
        item{BrandHeader()}
        item{Column(Modifier.fillMaxWidth(),horizontalAlignment=Alignment.CenterHorizontally){Text("What are you looking for?",style=MaterialTheme.typography.displaySmall);Spacer(Modifier.height(10.dp));HonorableOrb(OrbState.IDLE,size=170);GlassSearch("Describe a memory…",onClick=openMemories)}}
        item{Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceEvenly){OrbButton(Icons.Rounded.PhotoLibrary,"Memories",openMemories);OrbButton(Icons.Rounded.VideoLibrary,"Videos",openMemories);OrbButton(Icons.Rounded.Screenshot,"Screenshots",openMemories);OrbButton(Icons.Rounded.Policy,"Terms AI",openTerms)}}
        item{GlassCard{Row(verticalAlignment=Alignment.CenterVertically){GlassCircle(Modifier.size(44.dp)){Icon(Icons.Rounded.PhotoLibrary,null,tint=MaterialTheme.colorScheme.primary)};Spacer(Modifier.width(13.dp));Column(Modifier.weight(1f)){Text("Your private library",style=MaterialTheme.typography.titleMedium);Text("Open Memories to grant access and build the local index.",style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant)}}}}
    }
}

@Composable private fun BrandHeader(){Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically){Column(Modifier.weight(1f)){Text("HONORABLE",style=MaterialTheme.typography.labelLarge,color=MaterialTheme.colorScheme.primary,letterSpacing=2.6.sp);Text("Private intelligence",style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant)};GlassCircle(Modifier.size(50.dp)){Icon(Icons.Rounded.Person,"Profile and settings")}}}

@Composable fun GlassSearch(hint:String,value:String="",focused:Boolean=false,onValueChange:(String)->Unit={},onClick:()->Unit){
    GlassSurface(Modifier.fillMaxWidth().height(if(focused)76.dp else 66.dp).clickable(enabled=!focused,onClick=onClick),shape=RoundedCornerShape(if(focused)28.dp else 33.dp),alpha=.64f){
        Row(Modifier.fillMaxSize().padding(start=18.dp,end=9.dp),verticalAlignment=Alignment.CenterVertically){Icon(Icons.Rounded.Search,null,tint=MaterialTheme.colorScheme.primary);Spacer(Modifier.width(12.dp));if(focused)BasicTextField(value,onValueChange,Modifier.weight(1f),singleLine=true,textStyle=MaterialTheme.typography.bodyLarge.copy(color=MaterialTheme.colorScheme.onSurface))else Text(if(value.isBlank())hint else value,Modifier.weight(1f),color=MaterialTheme.colorScheme.onSurfaceVariant);GlassCircle(Modifier.size(48.dp)){Icon(Icons.Rounded.ArrowUpward,"Search",tint=MaterialTheme.colorScheme.primary)}}
    }
}

private enum class SearchStage { LANDING, FOCUS, SEARCHING, RESULTS }
@Composable private fun MemoriesScreen(openViewer:(SearchMatch)->Unit){
    val vm:MemoriesViewModel=viewModel();val backend by vm.state.collectAsStateWithLifecycle();var stage by rememberSaveable{mutableStateOf(SearchStage.LANDING)};var query by rememberSaveable{mutableStateOf("")};var filter by rememberSaveable{mutableStateOf("All")}
    val permissionLauncher=rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){vm.permissionResult()}
    LaunchedEffect(backend){if(backend is MemorySearchState.Results)stage=SearchStage.RESULTS}
    Column(Modifier.fillMaxSize().padding(top=42.dp)){
        AnimatedContent(stage,Modifier.weight(1f),label="memory-state",transitionSpec={fadeIn(tween(220))+slideInVertically{it/16} togetherWith fadeOut(tween(120))}){current->
            when(current){
                SearchStage.LANDING->when(val state=backend){MemorySearchState.PermissionRequired->PermissionState{permissionLauncher.launch(MemoriesViewModel.permissions())};is MemorySearchState.Indexing->IndexingState(state.progress);is MemorySearchState.Failed->BackendEmpty(state.message);else->MemoryLanding({stage=SearchStage.FOCUS},{query=it;stage=SearchStage.SEARCHING;vm.search(it)})}
                SearchStage.FOCUS->SearchFocus(query,{query=it},{if(query.isNotBlank()){stage=SearchStage.SEARCHING;vm.search(query)}},{stage=SearchStage.LANDING})
                SearchStage.SEARCHING->SearchInMotion(query)
                SearchStage.RESULTS->SearchResults(query,filter,{filter=it},{stage=SearchStage.FOCUS},(backend as? MemorySearchState.Results)?.matches.orEmpty(),openViewer)
            }
        }
    }
}

@Composable private fun MemoryLanding(focus:()->Unit,search:(String)->Unit){LazyColumn(contentPadding=PaddingValues(22.dp,10.dp,22.dp,125.dp),verticalArrangement=Arrangement.spacedBy(24.dp)){item{Row(verticalAlignment=Alignment.CenterVertically){Column(Modifier.weight(1f)){Text("Memories",style=MaterialTheme.typography.displaySmall);Text("Ask for a moment. Not a filename.",color=MaterialTheme.colorScheme.onSurfaceVariant)};HonorableOrb(OrbState.IDLE,size=82)}};item{GlassSearch("Describe a memory…",onClick=focus)};item{LazyRow(horizontalArrangement=Arrangement.spacedBy(10.dp)){items(listOf("tennis outside","red car in snow","birthday cake","flight screenshot")){suggestion->SuggestionPill(suggestion){search(suggestion)}}}};item{PrivacyPill("Searches stay on this device")}}}

@Composable private fun PermissionState(grant:()->Unit){Column(Modifier.fillMaxSize().padding(28.dp),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.Center){Icon(Icons.Rounded.PhotoLibrary,null,Modifier.size(64.dp),tint=MaterialTheme.colorScheme.primary);Text("Allow photo and video access",style=MaterialTheme.typography.headlineMedium);Text("Honorable needs access only when you search your library.",color=MaterialTheme.colorScheme.onSurfaceVariant);Spacer(Modifier.height(18.dp));Button(grant){Text("Choose media access")}}}
@Composable private fun IndexingState(progress:IndexProgress){Column(Modifier.fillMaxSize(),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.Center){HonorableOrb(OrbState.SEARCHING);Text("Indexing your library",style=MaterialTheme.typography.headlineMedium);Text(if(progress.total==0)"Discovering media…" else "${progress.processed} of ${progress.total}",color=MaterialTheme.colorScheme.onSurfaceVariant)}}
@Composable private fun BackendEmpty(message:String){Column(Modifier.fillMaxSize(),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.Center){Text(message,style=MaterialTheme.typography.titleLarge);Text("No sample results are shown.",color=MaterialTheme.colorScheme.onSurfaceVariant)}}

@Composable private fun SearchFocus(query:String,onQuery:(String)->Unit,search:()->Unit,close:()->Unit){Column(Modifier.fillMaxSize().padding(22.dp,14.dp,22.dp,120.dp),horizontalAlignment=Alignment.CenterHorizontally){Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.End){OrbIcon(Icons.Rounded.Close,"Close",close)};HonorableOrb(if(query.isBlank())OrbState.LISTENING else OrbState.THINKING,size=190);Text("Describe the moment",style=MaterialTheme.typography.headlineMedium);Text("People, places, colors, text—even a feeling.",color=MaterialTheme.colorScheme.onSurfaceVariant);Spacer(Modifier.height(25.dp));GlassSearch("Try “blue shirt at tennis”",query,true,onQuery,search);Spacer(Modifier.height(18.dp));Row(horizontalArrangement=Arrangement.spacedBy(12.dp)){OrbButton(Icons.Rounded.Mic,"Speak"){};OrbButton(Icons.Rounded.Tune,"Refine"){};OrbButton(Icons.Rounded.ArrowUpward,"Search",search)}}}

@Composable private fun SearchInMotion(query:String){Column(Modifier.fillMaxSize().padding(bottom=110.dp),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.Center){HonorableOrb(OrbState.SEARCHING,size=220);Text("Searching your memories",style=MaterialTheme.typography.headlineMedium);Text(query.ifBlank{"Understanding the moment…"},color=MaterialTheme.colorScheme.onSurfaceVariant);Spacer(Modifier.height(16.dp));PrivacyPill()}}

@Composable private fun SearchResults(query:String,filter:String,onFilter:(String)->Unit,edit:()->Unit,matches:List<SearchMatch>,openViewer:(SearchMatch)->Unit){val visible=matches.filter{filter=="All"||filter=="Videos"&&it.media.kind==MediaKind.VIDEO||filter=="Photos"&&it.media.kind==MediaKind.IMAGE||filter=="Screenshots"&&it.media.isScreenshot};Column(Modifier.fillMaxSize()){Row(Modifier.padding(horizontal=16.dp),verticalAlignment=Alignment.CenterVertically){OrbIcon(Icons.AutoMirrored.Rounded.ArrowBack,"Edit search",edit);Spacer(Modifier.width(12.dp));GlassSearch("Search",query,onClick=edit)};LazyRow(Modifier.padding(vertical=12.dp),contentPadding=PaddingValues(horizontal=16.dp),horizontalArrangement=Arrangement.spacedBy(9.dp)){items(listOf("All","Photos","Videos","Screenshots")){CircleFilter(it,filter==it){onFilter(it)}}};if(visible.isEmpty())BackendEmpty("No matching memories found") else LazyVerticalGrid(GridCells.Adaptive(160.dp),contentPadding=PaddingValues(6.dp,0.dp,6.dp,125.dp),horizontalArrangement=Arrangement.spacedBy(5.dp),verticalArrangement=Arrangement.spacedBy(5.dp)){items(visible,key={it.media.uri}){match->RealMemory(match,Modifier.aspectRatio(.92f)){openViewer(match)}}}}}

@Composable private fun RealMemory(match:SearchMatch,modifier:Modifier,onClick:()->Unit){val media=match.media;Box(modifier.clip(RoundedCornerShape(20.dp)).clickable(onClick=onClick)){AsyncImage(Uri.parse(media.uri),media.displayName,Modifier.fillMaxSize(),contentScale=ContentScale.Crop);Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent,Color.Transparent,Color.Black.copy(.72f)))));Column(Modifier.align(Alignment.BottomStart).padding(13.dp)){Text(media.displayName.ifBlank{if(media.kind==MediaKind.VIDEO)"Video" else "Photo"},color=Color.White,style=MaterialTheme.typography.titleSmall,maxLines=1);Text(match.explanations.firstOrNull()?:"Low-confidence match",color=Color.White.copy(.78f),style=MaterialTheme.typography.labelSmall,maxLines=1)}}}

private data class MemoryItem(val title:String,val match:String,val res:Int,val video:Boolean=false,val tall:Boolean=false)
private val memoryItems=listOf(MemoryItem("Saturday match","tennis · outside · blue shirt",R.drawable.memory_tennis,true,true),MemoryItem("Snow day","red vehicle · snow · night",R.drawable.memory_snow_car,false),MemoryItem("Birthday","cake · window · candles",R.drawable.memory_birthday,false,true),MemoryItem("Practice","serve · court · 01:42",R.drawable.memory_tennis,true),MemoryItem("Winter drive","red car · December",R.drawable.memory_snow_car),MemoryItem("Morning wishes","birthday · warm light",R.drawable.memory_birthday))

@Composable private fun CinematicMemory(item:MemoryItem,modifier:Modifier,onClick:()->Unit){Box(modifier.clip(RoundedCornerShape(20.dp)).clickable(onClick=onClick).semantics{contentDescription="${item.title}, matched ${item.match}"}){Image(painterResource(item.res),null,Modifier.fillMaxSize(),contentScale=ContentScale.Crop);Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent,Color.Transparent,Color.Black.copy(.72f)))));Column(Modifier.align(Alignment.BottomStart).padding(13.dp)){Row(verticalAlignment=Alignment.CenterVertically){Text(item.title,Modifier.weight(1f),color=Color.White,style=MaterialTheme.typography.titleSmall);if(item.video)MiniGlassPill("▶ 01:42")};Text(item.match,color=Color.White.copy(.76f),style=MaterialTheme.typography.labelSmall,maxLines=1,overflow=TextOverflow.Ellipsis)}}}

@Composable private fun MediaViewer(match:SearchMatch,close:()->Unit){val context=LocalContext.current;val media=match.media;Box(Modifier.fillMaxSize().background(Color.Black)){AsyncImage(Uri.parse(media.uri),media.displayName,Modifier.fillMaxSize(),contentScale=ContentScale.Fit);Row(Modifier.statusBarsPadding().padding(18.dp).fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween){OrbIcon(Icons.AutoMirrored.Rounded.ArrowBack,"Back",close,true);OrbIcon(Icons.Rounded.OpenInNew,"Open media",{context.startActivity(Intent(Intent.ACTION_VIEW,Uri.parse(media.uri)).setDataAndType(Uri.parse(media.uri),if(media.kind==MediaKind.VIDEO)"video/*" else "image/*").addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION))},true)};GlassSurface(Modifier.align(Alignment.BottomCenter).navigationBarsPadding().padding(18.dp,18.dp,18.dp,30.dp),shape=RoundedCornerShape(25.dp),dark=true){Column(Modifier.padding(18.dp)){Text(match.confidence.name.replace('_',' '),color=Color(0xFF8DDBFF),style=MaterialTheme.typography.labelMedium);Text(match.explanations.joinToString(" · ").ifBlank{"Low-confidence local match"},color=Color.White,style=MaterialTheme.typography.titleMedium);match.bestTimestampMs?.let{Text("Best video moment ${it/60000}:${(it/1000%60).toString().padStart(2,'0')}",color=Color.White.copy(.75f))}}}}}

@Composable private fun MediaViewer(close:()->Unit){Box(Modifier.fillMaxSize().background(Color.Black)){Image(painterResource(R.drawable.memory_tennis),null,Modifier.fillMaxSize(),contentScale=ContentScale.Crop);Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Black.copy(.36f),Color.Transparent,Color.Black.copy(.72f)))));Row(Modifier.statusBarsPadding().padding(18.dp).fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween){OrbIcon(Icons.AutoMirrored.Rounded.ArrowBack,"Back",close,true);Row(horizontalArrangement=Arrangement.spacedBy(10.dp)){OrbIcon(Icons.Rounded.Info,"Information",{},true);OrbIcon(Icons.Rounded.Share,"Share",{},true)}};GlassSurface(Modifier.align(Alignment.BottomCenter).navigationBarsPadding().padding(18.dp,18.dp,18.dp,30.dp),shape=RoundedCornerShape(25.dp),dark=true){Column(Modifier.padding(18.dp)){Text("MATCHED MOMENT",color=Color(0xFF8DDBFF),style=MaterialTheme.typography.labelMedium,letterSpacing=1.5.sp);Text("tennis · outside · blue shirt",color=Color.White,style=MaterialTheme.typography.titleLarge);Spacer(Modifier.height(12.dp));Row(verticalAlignment=Alignment.CenterVertically){MiniGlassPill("Best match  01:42");Spacer(Modifier.weight(1f));OrbIcon(Icons.Rounded.PlayArrow,"Play from matching moment",{},true)}}}}}

@Composable private fun TermsScreen(){
    var result by rememberSaveable{mutableStateOf(false)}
    if(result){ TermsResult{result=false}; return }
    LazyColumn(contentPadding=PaddingValues(22.dp,48.dp,22.dp,130.dp),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(20.dp)){
        item{Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically){Column(Modifier.weight(1f)){Text("Terms AI",style=MaterialTheme.typography.displaySmall);Text("See the agreement beneath the agreement.",color=MaterialTheme.colorScheme.onSurfaceVariant)};HonorableOrb(OrbState.IDLE,size=104,icon=Icons.Rounded.Policy)}}
        item{GlassCard{Column(Modifier.padding(vertical=10.dp),horizontalAlignment=Alignment.CenterHorizontally){HonorableOrb(OrbState.THINKING,size=116,icon=Icons.Rounded.Description);Text("Bring in an agreement",style=MaterialTheme.typography.headlineMedium);Text("Private analysis turns dense language into clear decisions.",Modifier.padding(12.dp),color=MaterialTheme.colorScheme.onSurfaceVariant);Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceEvenly){OrbButton(Icons.Rounded.Link,"Paste link"){};OrbButton(Icons.Rounded.ContentPaste,"Paste text"){};OrbButton(Icons.Rounded.UploadFile,"Import file"){}}}}}
        item{Button({result=true},Modifier.fillMaxWidth().height(58.dp),shape=RoundedCornerShape(29.dp)){Icon(Icons.Rounded.AutoAwesome,null);Spacer(Modifier.width(10.dp));Text("Analyze agreement")}}
        item{PrivacyPill("Analysis remains on this device")}
        item{Text("Informational only · Not legal advice",style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant)}
    }
}

@Composable private fun TermsResult(back:()->Unit){BackHandler(onBack=back);LazyColumn(contentPadding=PaddingValues(22.dp,44.dp,22.dp,130.dp),verticalArrangement=Arrangement.spacedBy(14.dp)){item{Row(verticalAlignment=Alignment.CenterVertically){OrbIcon(Icons.AutoMirrored.Rounded.ArrowBack,"Back",back);Spacer(Modifier.width(14.dp));Text("Agreement health",style=MaterialTheme.typography.headlineLarge)}};item{GlassCard{Row(Modifier.padding(8.dp),verticalAlignment=Alignment.CenterVertically){CircularRiskIndicator(.58f);Spacer(Modifier.width(20.dp));Column{Text("MODERATE",color=MaterialTheme.colorScheme.tertiary,style=MaterialTheme.typography.labelLarge,letterSpacing=1.6.sp);Text("A few clauses need attention",style=MaterialTheme.typography.titleLarge);Text("Clear overall, with renewal and dispute limits.",color=MaterialTheme.colorScheme.onSurfaceVariant)}}}};item{GlassCard{Text("In one minute",style=MaterialTheme.typography.titleLarge);Text("The service renews annually. You can cancel before billing, but refunds are limited and disputes use arbitration.",Modifier.padding(top=8.dp),color=MaterialTheme.colorScheme.onSurfaceVariant)}};items(listOf("Important" to Icons.Rounded.PriorityHigh,"Watch out" to Icons.Rounded.Visibility,"Good" to Icons.Rounded.ThumbUp,"Money" to Icons.Rounded.Payments,"Cancellation" to Icons.Rounded.EventBusy,"Privacy" to Icons.Rounded.Lock,"Your rights" to Icons.Rounded.Balance)){(title,icon)->GlassDisclosure(title,icon)}}}

@Composable private fun ActivityScreen(){LazyColumn(contentPadding=PaddingValues(22.dp,48.dp,22.dp,130.dp),verticalArrangement=Arrangement.spacedBy(6.dp)){item{Text("Activity",style=MaterialTheme.typography.displaySmall);Text("A quiet record of what Honorable understood.",color=MaterialTheme.colorScheme.onSurfaceVariant);Spacer(Modifier.height(22.dp))};items(listOf(Triple("Tennis outside","12 moments found",Icons.Rounded.Search),Triple("Subscription terms","Moderate agreement health",Icons.Rounded.Policy),Triple("Library synchronized","48 new memories indexed",Icons.Rounded.Sync),Triple("Flight screenshot","Air Canada text matched",Icons.Rounded.Screenshot))){(title,detail,icon)->TimelineRow(title,detail,icon)}}}

@Composable private fun SettingsScreen(openPrivacy:()->Unit,openPlus:()->Unit){LazyColumn(contentPadding=PaddingValues(22.dp,48.dp,22.dp,130.dp),verticalArrangement=Arrangement.spacedBy(20.dp)){item{Row(verticalAlignment=Alignment.CenterVertically){Column(Modifier.weight(1f)){Text("Settings",style=MaterialTheme.typography.displaySmall);Text("Make Honorable yours.",color=MaterialTheme.colorScheme.onSurfaceVariant)};GlassCircle(Modifier.size(60.dp)){Icon(Icons.Rounded.Person,"Account")}}};item{SettingCluster("PRIVACY",listOf(SettingItem("Privacy promise","Everything stays local",Icons.Rounded.Shield,openPrivacy),SettingItem("Permissions","Photos and videos",Icons.Rounded.Key,{})))};item{SettingCluster("INTELLIGENCE",listOf(SettingItem("AI & search","Local fallback active",Icons.Rounded.AutoAwesome,{}),SettingItem("Storage & index","9,812 memories",Icons.Rounded.Storage,{})))};item{SettingCluster("HONORABLE",listOf(SettingItem("Honorable Plus","Explore premium",Icons.Rounded.Diamond,openPlus),SettingItem("Appearance","System · Dynamic color",Icons.Rounded.Palette,{}),SettingItem("About","Version 0.1.0",Icons.Rounded.Info,{})))}}}

@Composable private fun PrivacyScreen(close:()->Unit){OverlayScaffold(close){LazyColumn(contentPadding=PaddingValues(24.dp,16.dp,24.dp,42.dp),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(20.dp)){item{HonorableOrb(OrbState.RESULT,size=184,icon=Icons.Rounded.Shield);Text("Your memories stay yours.",style=MaterialTheme.typography.displaySmall);Text("Honorable is designed around your device—not a cloud copy of your life.",color=MaterialTheme.colorScheme.onSurfaceVariant)};item{Row(horizontalArrangement=Arrangement.spacedBy(10.dp)){PrivacyNode("Local AI",Icons.Rounded.AutoAwesome,Modifier.weight(1f));PrivacyNode("Local OCR",Icons.Rounded.TextFields,Modifier.weight(1f))}};item{Row(horizontalArrangement=Arrangement.spacedBy(10.dp)){PrivacyNode("Local index",Icons.Rounded.Storage,Modifier.weight(1f));PrivacyNode("No upload",Icons.Rounded.CloudOff,Modifier.weight(1f))}};item{PrivacyPill("Photos, frames and search text remain on device")}}}}

@Composable private fun PlusScreen(close:()->Unit){var annual by rememberSaveable{mutableStateOf(true)};OverlayScaffold(close){LazyColumn(contentPadding=PaddingValues(24.dp,10.dp,24.dp,42.dp),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(20.dp)){item{HonorableOrb(OrbState.RESULT,size=190,icon=Icons.Rounded.Diamond);Text("Honorable Plus",style=MaterialTheme.typography.displaySmall);Text("More ways to understand. Still private.",color=MaterialTheme.colorScheme.onSurfaceVariant)};item{GlassCard{listOf("Advanced Memories AI","Advanced video search","Terms AI","No ads","Future premium intelligence").forEach{Row(Modifier.padding(vertical=8.dp),verticalAlignment=Alignment.CenterVertically){GlassCircle(Modifier.size(34.dp)){Icon(Icons.Rounded.Check,null,Modifier.size(17.dp),tint=MaterialTheme.colorScheme.primary)};Spacer(Modifier.width(12.dp));Text(it)}}}};item{Row(horizontalArrangement=Arrangement.spacedBy(10.dp)){PlanChoice("Monthly","$5.99",!annual,Modifier.weight(1f)){annual=false};PlanChoice("Annual","$39.99",annual,Modifier.weight(1f)){annual=true}}};item{Button({},Modifier.fillMaxWidth().height(58.dp),shape=RoundedCornerShape(29.dp)){Text("Continue")}};item{Text("Preview only · Store processing is not configured",style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant)}}}}

@Composable private fun FloatingGlassDock(selected:MainTab,onSelect:(MainTab)->Unit,modifier:Modifier=Modifier){GlassSurface(modifier.navigationBarsPadding().padding(horizontal=14.dp,vertical=10.dp).fillMaxWidth().height(76.dp),shape=RoundedCornerShape(38.dp),alpha=.74f,shadow=16.dp){Row(Modifier.fillMaxSize().padding(horizontal=8.dp),horizontalArrangement=Arrangement.SpaceAround,verticalAlignment=Alignment.CenterVertically){MainTab.entries.forEach{tab->val active=tab==selected;val color by animateColorAsState(if(active)MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,label="dock-color");Column(Modifier.width(62.dp).clickable{onSelect(tab)}.semantics{this.selected=active;contentDescription=tab.label},horizontalAlignment=Alignment.CenterHorizontally){Box(Modifier.size(42.dp).clip(CircleShape).background(if(active)MaterialTheme.colorScheme.primary.copy(.16f) else Color.Transparent),contentAlignment=Alignment.Center){Icon(tab.icon,null,tint=color)};AnimatedVisibility(active){Text(tab.label,style=MaterialTheme.typography.labelSmall,color=color)}}}}}}

@Composable fun GlassSurface(modifier:Modifier=Modifier,shape:Shape=RoundedCornerShape(24.dp),alpha:Float=.60f,shadow:Dp=5.dp,dark:Boolean=false,content:@Composable BoxScope.()->Unit){val base=if(dark)Color(0xFF071822) else MaterialTheme.colorScheme.surfaceVariant;Box(modifier.shadow(shadow,shape,ambientColor=MaterialTheme.colorScheme.primary.copy(.12f)).clip(shape).background(base.copy(alpha)).border(1.dp,Color.White.copy(if(dark).16f else .54f),shape),contentAlignment=Alignment.Center,content=content)}
@Composable private fun GlassCard(modifier:Modifier=Modifier,content:@Composable ColumnScope.()->Unit){GlassSurface(modifier.fillMaxWidth()){Column(Modifier.padding(18.dp),content=content)}}
@Composable private fun GlassCircle(modifier:Modifier,content:@Composable BoxScope.()->Unit){GlassSurface(modifier,CircleShape,.55f,4.dp,content=content)}
@Composable private fun OrbIcon(icon:ImageVector,description:String,onClick:()->Unit,dark:Boolean=false){GlassSurface(Modifier.size(50.dp).clickable(onClick=onClick),CircleShape,.58f,4.dp,dark){Icon(icon,description,tint=if(dark)Color.White else MaterialTheme.colorScheme.primary)}}
@Composable private fun CircleFilter(label:String,active:Boolean,onClick:()->Unit){val color by animateColorAsState(if(active)MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(.5f),label="filter");Surface(Modifier.height(45.dp).clickable(onClick=onClick),shape=RoundedCornerShape(23.dp),color=color,border=BorderStroke(1.dp,if(active)MaterialTheme.colorScheme.primary.copy(.48f) else Color.White.copy(.28f))){Row(Modifier.padding(horizontal=15.dp),verticalAlignment=Alignment.CenterVertically){if(active){Icon(Icons.Rounded.Check,null,Modifier.size(17.dp));Spacer(Modifier.width(6.dp))};Text(label)}}}
@Composable private fun CircularRiskIndicator(score:Float){Box(Modifier.size(106.dp),contentAlignment=Alignment.Center){CircularProgressIndicator({score},Modifier.fillMaxSize(),strokeWidth=8.dp,trackColor=MaterialTheme.colorScheme.surfaceVariant);Column(horizontalAlignment=Alignment.CenterHorizontally){Text("58",style=MaterialTheme.typography.headlineMedium);Text("/ 100",style=MaterialTheme.typography.labelSmall)}}}
@Composable private fun GlassDisclosure(title:String,icon:ImageVector){var open by rememberSaveable(title){mutableStateOf(title=="Important")};GlassCard(Modifier.clickable{open=!open}){Row(verticalAlignment=Alignment.CenterVertically){GlassCircle(Modifier.size(42.dp)){Icon(icon,null,Modifier.size(20.dp),tint=MaterialTheme.colorScheme.primary)};Spacer(Modifier.width(13.dp));Text(title,Modifier.weight(1f),style=MaterialTheme.typography.titleMedium);Icon(if(open)Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,if(open)"Collapse" else "Expand")};AnimatedVisibility(open){Text("Automatic renewal and individual arbitration deserve a closer look.",Modifier.padding(top=12.dp),color=MaterialTheme.colorScheme.onSurfaceVariant)}}}
private data class SettingItem(val title:String,val detail:String,val icon:ImageVector,val click:()->Unit)
@Composable private fun SettingCluster(label:String,items:List<SettingItem>){Column{Text(label,Modifier.padding(start=6.dp,bottom=9.dp),color=MaterialTheme.colorScheme.primary,style=MaterialTheme.typography.labelMedium,letterSpacing=1.4.sp);GlassCard{items.forEachIndexed{i,item->if(i>0)HorizontalDivider(Modifier.padding(vertical=10.dp),color=MaterialTheme.colorScheme.outlineVariant.copy(.5f));Row(Modifier.fillMaxWidth().clickable(onClick=item.click).padding(vertical=4.dp),verticalAlignment=Alignment.CenterVertically){GlassCircle(Modifier.size(44.dp)){Icon(item.icon,null,Modifier.size(21.dp),tint=MaterialTheme.colorScheme.primary)};Spacer(Modifier.width(13.dp));Column(Modifier.weight(1f)){Text(item.title,style=MaterialTheme.typography.titleMedium);Text(item.detail,style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant)};Icon(Icons.Rounded.ChevronRight,null)}}}}}
@Composable private fun TimelineRow(title:String,detail:String,icon:ImageVector){Row(Modifier.height(IntrinsicSize.Min)){Column(horizontalAlignment=Alignment.CenterHorizontally){GlassCircle(Modifier.size(46.dp)){Icon(icon,null,Modifier.size(21.dp),tint=MaterialTheme.colorScheme.primary)};Box(Modifier.width(1.dp).weight(1f).background(MaterialTheme.colorScheme.primary.copy(.22f)))};Spacer(Modifier.width(14.dp));GlassCard(Modifier.padding(bottom=10.dp)){Text(title,style=MaterialTheme.typography.titleMedium);Text(detail,color=MaterialTheme.colorScheme.onSurfaceVariant);Text("Today",Modifier.padding(top=8.dp),style=MaterialTheme.typography.labelSmall,color=MaterialTheme.colorScheme.primary)}}}
@Composable private fun PrivacyNode(title:String,icon:ImageVector,modifier:Modifier){GlassCard(modifier){Column(Modifier.fillMaxWidth(),horizontalAlignment=Alignment.CenterHorizontally){GlassCircle(Modifier.size(58.dp)){Icon(icon,null,tint=MaterialTheme.colorScheme.primary)};Spacer(Modifier.height(10.dp));Text(title,style=MaterialTheme.typography.titleMedium)}}}
@Composable private fun PlanChoice(name:String,price:String,selected:Boolean,modifier:Modifier,onClick:()->Unit){Surface(modifier.clickable(onClick=onClick),shape=RoundedCornerShape(22.dp),color=if(selected)MaterialTheme.colorScheme.primaryContainer.copy(.64f)else MaterialTheme.colorScheme.surfaceVariant.copy(.5f),border=BorderStroke(if(selected)2.dp else 1.dp,if(selected)MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant)){Column(Modifier.padding(18.dp)){Text(name,style=MaterialTheme.typography.titleMedium);Text(price,style=MaterialTheme.typography.headlineSmall);if(selected)Text("BEST VALUE",style=MaterialTheme.typography.labelSmall,color=MaterialTheme.colorScheme.primary)}}}
@Composable private fun OverlayScaffold(close:()->Unit,content:@Composable BoxScope.()->Unit){AppBackground{Box(Modifier.fillMaxSize().statusBarsPadding()){OrbIcon(Icons.Rounded.Close,"Close",close);Box(Modifier.fillMaxSize().padding(top=58.dp),content=content)}}}
@Composable private fun SuggestionPill(text:String,onClick:()->Unit){Surface(Modifier.clickable(onClick=onClick),shape=RoundedCornerShape(22.dp),color=MaterialTheme.colorScheme.surfaceVariant.copy(.54f),border=BorderStroke(1.dp,Color.White.copy(.34f))){Text(text,Modifier.padding(horizontal=16.dp,vertical=11.dp),style=MaterialTheme.typography.labelLarge)}}
@Composable private fun MiniGlassPill(text:String){Surface(shape=RoundedCornerShape(14.dp),color=Color.Black.copy(.38f),border=BorderStroke(1.dp,Color.White.copy(.24f))){Text(text,Modifier.padding(horizontal=8.dp,vertical=5.dp),color=Color.White,style=MaterialTheme.typography.labelSmall)}}
@Composable private fun PrivacyPill(text:String="Private search · Nothing uploaded"){Surface(shape=RoundedCornerShape(20.dp),color=MaterialTheme.colorScheme.primaryContainer.copy(.48f)){Row(Modifier.padding(horizontal=14.dp,vertical=9.dp),verticalAlignment=Alignment.CenterVertically){Icon(Icons.Rounded.Lock,null,Modifier.size(16.dp));Spacer(Modifier.width(7.dp));Text(text,style=MaterialTheme.typography.labelMedium)}}}
@Composable private fun SectionLabel(title:String,detail:String){Row(verticalAlignment=Alignment.Bottom){Text(title,Modifier.weight(1f),style=MaterialTheme.typography.titleLarge);Text(detail,style=MaterialTheme.typography.labelMedium,color=MaterialTheme.colorScheme.primary)}}
@Composable private fun Pressable(onClick:()->Unit,content: @Composable (Boolean)->Unit){var pressed by remember{mutableStateOf(false)};Box(Modifier.clickable{onClick()}.pointerInput(Unit){detectTapGestures(onPress={pressed=true;tryAwaitRelease();pressed=false})}){content(pressed)}}
