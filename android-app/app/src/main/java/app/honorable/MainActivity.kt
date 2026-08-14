package app.honorable

import android.os.Bundle
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import app.honorable.search.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState);enableEdgeToEdge(statusBarStyle=SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),navigationBarStyle=SystemBarStyle.dark(android.graphics.Color.rgb(7,24,45)));setContent { HonorableApp() } }
}

private val BrandNavy=Color(0xFF07182D)
private val BrandNavyDeep=Color(0xFF041326)
private val BrandNavyScrim=Color(0xFF06182D)
private val BrandGlass=Color(0xB3123D70)
private val BrandGlassStrong=Color(0xE00A2A50)
private val BrandIce=Color(0xFFF4F8FF)
private val BrandGlassEdge=Color(0xFFA9D9FF)
private val DarkColors = darkColorScheme(
    primary=Color(0xFF83D4FF),onPrimary=Color(0xFF06213D),primaryContainer=Color(0xFF163E6F),onPrimaryContainer=Color(0xFFDDF3FF),
    secondary=Color(0xFF4C8DFF),onSecondary=Color(0xFF07182D),secondaryContainer=Color(0xFF14365F),onSecondaryContainer=Color(0xFFE5F3FF),
    tertiary=Color(0xFFA9D9FF),onTertiary=Color(0xFF07182D),background=BrandNavy,onBackground=BrandIce,surface=Color(0xFF0C2547),onSurface=BrandIce,
    surfaceVariant=Color(0xFF14365F),onSurfaceVariant=Color(0xFFB8CBE5),outline=Color(0xFF6C91BF),outlineVariant=Color(0xFF28517F),
    error=Color(0xFFA9D9FF),onError=Color(0xFF07182D),errorContainer=Color(0xFF163E6F),onErrorContainer=BrandIce,
    surfaceTint=Color(0xFF83D4FF),inverseSurface=Color(0xFFDDF3FF),inverseOnSurface=BrandNavy,inversePrimary=Color(0xFF155FC6),scrim=BrandNavyScrim,
    surfaceBright=Color(0xFF1A4778),surfaceDim=BrandNavyDeep,surfaceContainerLowest=BrandNavyDeep,surfaceContainerLow=Color(0xFF09213D),
    surfaceContainer=Color(0xFF0C2B4F),surfaceContainerHigh=Color(0xFF12365F),surfaceContainerHighest=Color(0xFF194572)
)
private val AppType = Typography(
    displaySmall=Typography().displaySmall.copy(fontFamily=FontFamily.Serif,fontWeight=FontWeight.Medium,fontSize=43.sp,lineHeight=47.sp,letterSpacing=(-.8).sp),
    headlineLarge=Typography().headlineLarge.copy(fontFamily=FontFamily.Serif,fontWeight=FontWeight.Medium,fontSize=36.sp,lineHeight=41.sp),
    headlineMedium=Typography().headlineMedium.copy(fontFamily=FontFamily.Serif,fontWeight=FontWeight.Medium,fontSize=29.sp,lineHeight=35.sp),
    titleLarge=Typography().titleLarge.copy(fontWeight=FontWeight.SemiBold,fontSize=21.sp,lineHeight=27.sp),
    titleMedium=Typography().titleMedium.copy(fontWeight=FontWeight.SemiBold),
    bodyLarge=Typography().bodyLarge.copy(lineHeight=25.sp),
    labelLarge=Typography().labelLarge.copy(fontWeight=FontWeight.SemiBold,letterSpacing=.2.sp),
    labelMedium=Typography().labelMedium.copy(fontWeight=FontWeight.Medium,letterSpacing=.25.sp)
)
private val AppShapes=Shapes(extraSmall=RoundedCornerShape(18.dp),small=RoundedCornerShape(24.dp),medium=RoundedCornerShape(32.dp),large=RoundedCornerShape(42.dp),extraLarge=RoundedCornerShape(52.dp))

@Composable fun HonorableApp() {
    MaterialTheme(colorScheme=DarkColors,typography=AppType,shapes=AppShapes){AppBackground{HonorableShell()}}
}

@Composable private fun AppBackground(content:@Composable BoxScope.()->Unit){
    val c=MaterialTheme.colorScheme
    Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(BrandNavyDeep,Color(0xFF0A2A52),Color(0xFF071C38))))) {
        Canvas(Modifier.fillMaxSize()){
            drawRect(BrandGlassEdge.copy(.08f),topLeft=Offset(size.width*.08f,0f),size=androidx.compose.ui.geometry.Size(1f,size.height))
            drawRect(BrandGlassEdge.copy(.08f),topLeft=Offset(size.width*.92f,0f),size=androidx.compose.ui.geometry.Size(1f,size.height))
            drawCircle(Brush.radialGradient(listOf(c.secondary.copy(.20f),Color.Transparent)),radius=size.minDimension*.82f,center=Offset(size.width*1.02f,size.height*.04f))
            drawCircle(Brush.radialGradient(listOf(c.primary.copy(.10f),Color.Transparent)),radius=size.minDimension*.62f,center=Offset(-size.width*.12f,size.height*.82f))
        }
        content()
    }
}

private enum class MainTab(val label:String,val icon:ImageVector){HOME("Home",Icons.Rounded.Home),MEMORIES("Memories",Icons.Rounded.PhotoLibrary),TERMS("Terms",Icons.Rounded.Policy),ACTIVITY("Activity",Icons.Rounded.Timeline),SETTINGS("Settings",Icons.Rounded.Tune)}
private enum class Overlay { NONE, VIEWER, PRIVACY, PLUS }

@Composable private fun HonorableShell(){
    var tab by rememberSaveable{mutableStateOf(MainTab.HOME)};var overlay by rememberSaveable{mutableStateOf(Overlay.NONE)};var selected by remember{mutableStateOf<SearchMatch?>(null)}
    BackHandler(overlay!=Overlay.NONE){overlay=Overlay.NONE}
    LaunchedEffect(overlay,selected){if(overlay==Overlay.VIEWER&&selected==null)overlay=Overlay.NONE}
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
            when(overlay){Overlay.VIEWER->selected?.let{MediaViewer(it){overlay=Overlay.NONE}};Overlay.PRIVACY->PrivacyScreen{overlay=Overlay.NONE};Overlay.PLUS->PlusScreen{overlay=Overlay.NONE};else->Unit}
        }
    }
}

enum class OrbState { IDLE, THINKING, SEARCHING, LISTENING, RESULT, LOW_CONFIDENCE, ERROR }

@Composable fun HonorableOrb(state:OrbState,modifier:Modifier=Modifier,size:Int=150,icon:ImageVector=Icons.Rounded.AutoAwesome){
    val colors=MaterialTheme.colorScheme
    val motion=rememberInfiniteTransition(label="orb")
    val active=state==OrbState.SEARCHING
    val pulse by motion.animateFloat(if(active).88f else .72f,1f,infiniteRepeatable(tween(1300,easing=FastOutSlowInEasing),RepeatMode.Reverse),label="pulse")
    val rotation by motion.animateFloat(0f,if(active)360f else 24f,infiniteRepeatable(tween(if(active)2600 else 12000,easing=LinearEasing)),label="rotation")
    val accent=when(state){OrbState.ERROR->MaterialTheme.colorScheme.error;OrbState.LOW_CONFIDENCE->MaterialTheme.colorScheme.tertiary;else->MaterialTheme.colorScheme.secondary}
    Box(modifier.size(size.dp).semantics{contentDescription="Honorable scanner ${state.name.lowercase()}"},contentAlignment=Alignment.Center){
        Canvas(Modifier.fillMaxSize()){
            val r=this.size.minDimension*.43f
            drawCircle(Brush.radialGradient(listOf(colors.primaryContainer.copy(.82f),BrandGlass.copy(.42f),Color.Transparent)),r)
            drawCircle(BrandGlassEdge.copy(.36f),r,style=Stroke(1.dp.toPx()))
            drawCircle(BrandGlassEdge.copy(.20f),r*.68f,style=Stroke(1.dp.toPx()))
            drawCircle(BrandGlassEdge.copy(.13f),r*.34f,style=Stroke(1.dp.toPx()))
            drawArc(accent.copy(.95f),rotation,74f,false,style=Stroke(3.dp.toPx()))
            drawArc(colors.primary.copy(.58f),rotation+165f,32f,false,style=Stroke(1.dp.toPx()))
            repeat(12){i->val angle=Math.toRadians((i*30+rotation).toDouble());val start=Offset(center.x+kotlin.math.cos(angle).toFloat()*r*.78f,center.y+kotlin.math.sin(angle).toFloat()*r*.78f);val end=Offset(center.x+kotlin.math.cos(angle).toFloat()*r*.94f,center.y+kotlin.math.sin(angle).toFloat()*r*.94f);drawLine(if(i%3==0)accent.copy(.76f)else BrandGlassEdge.copy(.30f),start,end,if(i%3==0)2.dp.toPx()else 1.dp.toPx())}
            if(active){drawRect(Brush.horizontalGradient(listOf(Color.Transparent,accent.copy(.18f),accent.copy(pulse),accent.copy(.18f),Color.Transparent)),topLeft=Offset(0f,center.y-1.dp.toPx()),size=androidx.compose.ui.geometry.Size(this.size.width,2.dp.toPx()))}
            drawCircle(accent.copy(.08f+pulse*.08f),r*.18f)
        }
        Icon(icon,null,Modifier.size((size*.17f).dp),tint=accent)
    }
}

@Composable private fun OrbButton(icon:ImageVector,label:String,onClick:()->Unit){
    Column(horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(7.dp)){
        Pressable(onClick){pressed->GlassCircle(Modifier.size(58.dp).graphicsLayer{scaleX=if(pressed).94f else 1f;scaleY=scaleX}){Icon(icon,label,Modifier.size(23.dp),tint=MaterialTheme.colorScheme.primary)}}
        Text(label,style=MaterialTheme.typography.labelMedium,color=MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable private fun HomeScreen(openMemories:()->Unit,openTerms:()->Unit){
    LazyColumn(contentPadding=PaddingValues(0.dp,38.dp,0.dp,118.dp)){
        item{Box(Modifier.padding(horizontal=20.dp)){BrandHeader()}}
        item{PremiumHero(openMemories)}
        item{ArchiveRoute("01","MEMORIES","Find one moment in thousands",Icons.Rounded.PhotoLibrary,openMemories)}
        item{ArchiveRoute("02","TERMS AI","See beneath the fine print",Icons.Rounded.Policy,openTerms)}
        item{Row(Modifier.fillMaxWidth().padding(20.dp,22.dp,20.dp,8.dp),verticalAlignment=Alignment.CenterVertically){Box(Modifier.width(3.dp).height(52.dp).background(MaterialTheme.colorScheme.secondary));Spacer(Modifier.width(13.dp));Column(Modifier.weight(1f)){PremiumEyebrow("PRIVATE BY ARCHITECTURE");Text("Nothing leaves this device.",Modifier.padding(top=4.dp),color=MaterialTheme.colorScheme.onSurfaceVariant)};Icon(Icons.Rounded.Lock,null,tint=MaterialTheme.colorScheme.secondary)}}
    }
}

@Composable private fun PremiumHero(openMemories:()->Unit){
    val accent=MaterialTheme.colorScheme.secondary
    val heroShape=RoundedCornerShape(52.dp)
    Box(Modifier.fillMaxWidth().padding(horizontal=12.dp,vertical=18.dp).height(500.dp).clip(heroShape).background(Brush.linearGradient(listOf(BrandGlassStrong,MaterialTheme.colorScheme.primaryContainer.copy(.54f),BrandGlass.copy(.64f)))).border(1.dp,BrandGlassEdge.copy(.26f),heroShape)){
        Text("H",Modifier.align(Alignment.TopEnd).offset(x=36.dp,y=(-72).dp),fontFamily=FontFamily.Serif,fontSize=330.sp,lineHeight=330.sp,color=MaterialTheme.colorScheme.primary.copy(.075f))
        Canvas(Modifier.matchParentSize()){drawLine(accent.copy(.82f),Offset(20.dp.toPx(),34.dp.toPx()),Offset(size.width-20.dp.toPx(),34.dp.toPx()),1.dp.toPx());drawLine(BrandGlassEdge.copy(.14f),Offset(size.width*.70f,34.dp.toPx()),Offset(size.width*.70f,size.height-92.dp.toPx()),1.dp.toPx())}
        Column(Modifier.fillMaxSize().padding(horizontal=20.dp)){
            Spacer(Modifier.height(58.dp));PremiumEyebrow("PRIVATE INTELLIGENCE / 001");Spacer(Modifier.height(12.dp))
            Text("FIND",style=MaterialTheme.typography.displaySmall.copy(fontSize=72.sp,lineHeight=68.sp,letterSpacing=(-2.4).sp),color=MaterialTheme.colorScheme.onBackground)
            Text("WHAT",style=MaterialTheme.typography.displaySmall.copy(fontSize=72.sp,lineHeight=68.sp,letterSpacing=(-2.4).sp),color=MaterialTheme.colorScheme.onBackground)
            Text("MATTERS",style=MaterialTheme.typography.displaySmall.copy(fontSize=72.sp,lineHeight=68.sp,letterSpacing=(-2.4).sp),color=MaterialTheme.colorScheme.secondary)
            Text("Describe it. Honorable finds the real moment—privately, on your device.",Modifier.widthIn(max=285.dp).padding(top=20.dp),color=MaterialTheme.colorScheme.onSurfaceVariant,style=MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.weight(1f));ArchiveSearchCommand("DESCRIBE A MEMORY",openMemories);Spacer(Modifier.height(18.dp))
        }
    }
}

@Composable private fun ArchiveRoute(number:String,title:String,subtitle:String,icon:ImageVector,onClick:()->Unit){val shape=RoundedCornerShape(58.dp);Row(Modifier.padding(horizontal=12.dp,vertical=6.dp).fillMaxWidth().height(116.dp).clip(shape).background(BrandGlass.copy(.62f)).border(1.dp,BrandGlassEdge.copy(.24f),shape).clickable(onClick=onClick).padding(horizontal=14.dp),verticalAlignment=Alignment.CenterVertically){Box(Modifier.size(66.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(.14f)).border(1.dp,MaterialTheme.colorScheme.primary.copy(.42f),CircleShape),contentAlignment=Alignment.Center){Text(number,fontFamily=FontFamily.Serif,fontSize=27.sp,color=MaterialTheme.colorScheme.primary)};Column(Modifier.padding(start=16.dp).weight(1f)){Text(title,fontWeight=FontWeight.Bold,letterSpacing=1.8.sp);Text(subtitle,Modifier.padding(top=7.dp),color=MaterialTheme.colorScheme.onSurfaceVariant)};Box(Modifier.size(52.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(.18f)).border(1.dp,MaterialTheme.colorScheme.primary.copy(.48f),CircleShape),contentAlignment=Alignment.Center){Icon(Icons.Rounded.ArrowOutward,"Open $title",Modifier.size(20.dp),tint=MaterialTheme.colorScheme.primary)}}}

@Composable private fun ArchiveSearchCommand(label:String,onClick:()->Unit){Row(Modifier.fillMaxWidth().heightIn(min=72.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary).clickable(onClick=onClick).padding(start=22.dp,end=8.dp),verticalAlignment=Alignment.CenterVertically){Icon(Icons.Rounded.Search,null,Modifier.size(22.dp),tint=MaterialTheme.colorScheme.onPrimary);Text(label,Modifier.padding(start=14.dp).weight(1f),color=MaterialTheme.colorScheme.onPrimary,fontWeight=FontWeight.Bold,letterSpacing=1.3.sp);Box(Modifier.size(56.dp).clip(CircleShape).background(MaterialTheme.colorScheme.secondary),contentAlignment=Alignment.Center){Icon(Icons.Rounded.ArrowUpward,"Search",tint=MaterialTheme.colorScheme.onSecondary)}}}

@Composable private fun BrandHeader(){Row(Modifier.fillMaxWidth().height(48.dp),verticalAlignment=Alignment.CenterVertically){Text("H/",fontFamily=FontFamily.Serif,fontWeight=FontWeight.Bold,color=MaterialTheme.colorScheme.secondary,fontSize=25.sp);Spacer(Modifier.width(10.dp));Text("HONORABLE",Modifier.weight(1f),style=MaterialTheme.typography.labelLarge,color=MaterialTheme.colorScheme.onSurface,letterSpacing=3.2.sp);Text("LOCAL / PRIVATE",style=MaterialTheme.typography.labelSmall,color=MaterialTheme.colorScheme.onSurfaceVariant,letterSpacing=1.1.sp)}}

@Composable fun GlassSearch(hint:String,value:String="",focused:Boolean=false,dark:Boolean=false,onValueChange:(String)->Unit={},onClick:()->Unit){
    val focusRequester=remember{FocusRequester()};val keyboard=LocalSoftwareKeyboardController.current
    LaunchedEffect(focused){if(focused){focusRequester.requestFocus();keyboard?.show()}}
    GlassSurface(Modifier.fillMaxWidth().heightIn(min=if(focused)76.dp else 70.dp).clickable(enabled=!focused,onClick=onClick),shape=CircleShape,alpha=if(focused).82f else .68f,dark=dark){
        val foreground=if(dark)BrandIce else MaterialTheme.colorScheme.onSurface
        Row(Modifier.fillMaxSize().padding(start=20.dp,end=8.dp),verticalAlignment=Alignment.CenterVertically){Text("✦",color=MaterialTheme.colorScheme.primary,fontSize=18.sp);Spacer(Modifier.width(13.dp));if(focused)BasicTextField(value,onValueChange,Modifier.weight(1f).focusRequester(focusRequester),singleLine=true,textStyle=MaterialTheme.typography.bodyLarge.copy(color=foreground),keyboardOptions=KeyboardOptions(imeAction=ImeAction.Search),keyboardActions=KeyboardActions(onSearch={keyboard?.hide();onClick()}),decorationBox={inner->if(value.isBlank())Text(hint,color=foreground.copy(.50f));inner()})else Text(if(value.isBlank())hint else value,Modifier.weight(1f),color=foreground.copy(.72f),maxLines=1,overflow=TextOverflow.Ellipsis);Surface(onClick={keyboard?.hide();onClick()},modifier=Modifier.size(56.dp),shape=CircleShape,color=MaterialTheme.colorScheme.secondary){Box(contentAlignment=Alignment.Center){Icon(Icons.Rounded.ArrowUpward,"Search",Modifier.size(20.dp),tint=MaterialTheme.colorScheme.onSecondary)}}}
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
                SearchStage.LANDING->when(val state=backend){MemorySearchState.PermissionRequired->PermissionState{permissionLauncher.launch(MemoriesViewModel.permissions())};is MemorySearchState.Indexing->IndexingState(state.progress);is MemorySearchState.Failed->BackendEmpty(state.message);is MemorySearchState.Ready->MemoryLanding(state.count,{stage=SearchStage.FOCUS},{query=it;stage=SearchStage.SEARCHING;vm.search(it)});else->MemoryLanding(null,{stage=SearchStage.FOCUS},{query=it;stage=SearchStage.SEARCHING;vm.search(it)})}
                SearchStage.FOCUS->SearchFocus(query,{query=it},{if(query.isNotBlank()){stage=SearchStage.SEARCHING;vm.search(query)}},{stage=SearchStage.LANDING})
                SearchStage.SEARCHING->SearchInMotion(query)
                SearchStage.RESULTS->SearchResults(query,filter,{filter=it},{stage=SearchStage.FOCUS},(backend as? MemorySearchState.Results)?.matches.orEmpty(),openViewer)
            }
        }
    }
}

@Composable private fun MemoryLanding(count:Int?,focus:()->Unit,search:(String)->Unit){LazyColumn(contentPadding=PaddingValues(0.dp,4.dp,0.dp,116.dp)){item{Column(Modifier.padding(horizontal=20.dp)){PremiumEyebrow("ARCHIVE / ${count?.let{"$it ITEMS"}?:"LOCAL"}");Text("SEARCH\nMEMORY",Modifier.padding(top=10.dp),style=MaterialTheme.typography.displaySmall.copy(fontSize=61.sp,lineHeight=57.sp,letterSpacing=(-1.8).sp));Text("Scenes. Colors. Text. Video moments.",Modifier.padding(top=12.dp,bottom=25.dp),color=MaterialTheme.colorScheme.onSurfaceVariant);ArchiveSearchCommand("DESCRIBE THE MOMENT",focus)}};item{Row(Modifier.padding(20.dp,32.dp,20.dp,13.dp)){PremiumEyebrow("START WITH A DETAIL");Spacer(Modifier.weight(1f));Text("04 PROMPTS",style=MaterialTheme.typography.labelSmall,color=MaterialTheme.colorScheme.onSurfaceVariant)}};items(listOf("white beach with tall grass","red car in snow","birthday cake by a window","flight confirmation").withIndex().toList()){(index,prompt)->ArchivePrompt(index+1,prompt){search(prompt)}};item{Row(Modifier.padding(20.dp,25.dp),verticalAlignment=Alignment.CenterVertically){Box(Modifier.size(8.dp).background(MaterialTheme.colorScheme.secondary));Spacer(Modifier.width(11.dp));Text("OFFLINE / PRIVATE / ON DEVICE",style=MaterialTheme.typography.labelSmall,letterSpacing=1.1.sp,color=MaterialTheme.colorScheme.onSurfaceVariant)}}}}

@Composable private fun ArchivePrompt(number:Int,text:String,onClick:()->Unit){Row(Modifier.padding(horizontal=12.dp,vertical=5.dp).fillMaxWidth().heightIn(min=76.dp).clip(CircleShape).background(BrandGlass.copy(.58f)).border(1.dp,BrandGlassEdge.copy(.22f),CircleShape).clickable(onClick=onClick).padding(horizontal=10.dp),verticalAlignment=Alignment.CenterVertically){Box(Modifier.size(54.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(.14f)),contentAlignment=Alignment.Center){Text(number.toString().padStart(2,'0'),fontFamily=FontFamily.Serif,fontSize=22.sp,color=MaterialTheme.colorScheme.primary)};Text(text,Modifier.padding(start=15.dp).weight(1f),style=MaterialTheme.typography.bodyLarge);Box(Modifier.size(46.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(.12f)),contentAlignment=Alignment.Center){Icon(Icons.Rounded.ArrowOutward,"Search $text",Modifier.size(19.dp),tint=MaterialTheme.colorScheme.primary)}}}

@Composable private fun PermissionState(grant:()->Unit){Box(Modifier.fillMaxSize().padding(bottom=100.dp)){Text("01",Modifier.align(Alignment.TopEnd).offset(x=18.dp,y=(-34).dp),fontFamily=FontFamily.Serif,fontSize=210.sp,color=MaterialTheme.colorScheme.onBackground.copy(.045f));Column(Modifier.fillMaxSize().padding(20.dp)){Spacer(Modifier.height(34.dp));PremiumEyebrow("CHAPTER 01 / PERMISSION");Spacer(Modifier.weight(.6f));Text("OPEN\nTHE\nARCHIVE",style=MaterialTheme.typography.displaySmall.copy(fontSize=62.sp,lineHeight=57.sp,letterSpacing=(-2).sp));Row(Modifier.padding(top=24.dp).widthIn(max=330.dp)){Box(Modifier.width(3.dp).height(92.dp).background(MaterialTheme.colorScheme.secondary));Text("Grant photo and video access. Honorable builds a private search index locally—your media is never uploaded.",Modifier.padding(start=15.dp),color=MaterialTheme.colorScheme.onSurfaceVariant,style=MaterialTheme.typography.bodyLarge)};Spacer(Modifier.weight(1f));ArchiveSearchCommand("GRANT MEDIA ACCESS",grant);Text("LOCAL PROCESSING / REVOCABLE ANY TIME",Modifier.padding(top=14.dp),style=MaterialTheme.typography.labelSmall,color=MaterialTheme.colorScheme.onSurfaceVariant,letterSpacing=1.sp)}}}
@Composable private fun IndexingState(progress:IndexProgress){val fraction=if(progress.total>0)progress.processed.toFloat()/progress.total else 0f;Box(Modifier.fillMaxSize().padding(bottom=100.dp)){Column(Modifier.fillMaxSize().padding(20.dp)){Spacer(Modifier.height(34.dp));PremiumEyebrow("SCANNING / LOCAL INDEX");Spacer(Modifier.weight(.5f));Text(if(progress.total>0)"${(fraction*100).toInt()}%" else "—",fontFamily=FontFamily.Serif,fontSize=112.sp,lineHeight=108.sp,color=MaterialTheme.colorScheme.onBackground);Box(Modifier.fillMaxWidth().height(2.dp).background(MaterialTheme.colorScheme.surfaceVariant)){Box(Modifier.fillMaxWidth(fraction.coerceIn(0f,1f)).fillMaxHeight().background(MaterialTheme.colorScheme.secondary))};Row(Modifier.fillMaxWidth().padding(top=13.dp)){Text(if(progress.total==0)"DISCOVERING MEDIA" else "${progress.processed} / ${progress.total}",style=MaterialTheme.typography.labelLarge,letterSpacing=1.2.sp);Spacer(Modifier.weight(1f));Text("OFFLINE",color=MaterialTheme.colorScheme.secondary,style=MaterialTheme.typography.labelLarge)};Spacer(Modifier.weight(.7f));HonorableOrb(OrbState.SEARCHING,Modifier.align(Alignment.CenterHorizontally),size=210);Spacer(Modifier.weight(1f));Text("Building the visual index",style=MaterialTheme.typography.headlineMedium);Text("Photos first. Representative video frames next.",Modifier.padding(top=8.dp),color=MaterialTheme.colorScheme.onSurfaceVariant)}}}
@Composable private fun BackendEmpty(message:String){Column(Modifier.fillMaxSize().padding(28.dp,28.dp,28.dp,120.dp),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.Center){AccentIcon(Icons.Rounded.SearchOff,64.dp);Text(message,Modifier.padding(top=18.dp),style=MaterialTheme.typography.headlineMedium,textAlign=TextAlign.Center);Text("Try a broader description or search another moment.",Modifier.padding(top=8.dp),color=MaterialTheme.colorScheme.onSurfaceVariant,textAlign=TextAlign.Center)}}

@Composable private fun SearchFocus(query:String,onQuery:(String)->Unit,search:()->Unit,close:()->Unit){Column(Modifier.fillMaxSize().padding(20.dp,8.dp,20.dp,108.dp)){Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween,verticalAlignment=Alignment.CenterVertically){PremiumEyebrow("MEMORIES / QUERY INPUT");SquareIconButton(Icons.Rounded.Close,"Close",close)};Spacer(Modifier.height(22.dp));Text("DESCRIBE\nTHE MOMENT",style=MaterialTheme.typography.displaySmall.copy(fontSize=52.sp,lineHeight=49.sp));Spacer(Modifier.weight(.5f));HonorableOrb(if(query.isBlank())OrbState.LISTENING else OrbState.THINKING,Modifier.align(Alignment.CenterHorizontally),size=216);Spacer(Modifier.weight(.5f));Text("A SCENE / COLOR / PERSON / SIGN / FEELING",style=MaterialTheme.typography.labelSmall,color=MaterialTheme.colorScheme.onSurfaceVariant,letterSpacing=1.1.sp);Spacer(Modifier.height(12.dp));GlassSearch("blue shirt at tennis",value=query,focused=true,onValueChange=onQuery,onClick=search);Spacer(Modifier.height(10.dp));ArchiveSearchCommand("SEARCH PRIVATE ARCHIVE",search)}}

@Composable private fun SearchInMotion(query:String){Box(Modifier.fillMaxSize().padding(bottom=100.dp)){Text("SCAN",Modifier.align(Alignment.TopEnd).offset(x=14.dp,y=52.dp),fontFamily=FontFamily.Serif,fontSize=92.sp,color=MaterialTheme.colorScheme.onBackground.copy(.04f));Column(Modifier.fillMaxSize().padding(20.dp)){Spacer(Modifier.height(30.dp));PremiumEyebrow("LOCAL SEMANTIC SEARCH / ACTIVE");Spacer(Modifier.weight(1f));HonorableOrb(OrbState.SEARCHING,Modifier.align(Alignment.CenterHorizontally),size=270);Spacer(Modifier.height(25.dp));Text("SEARCHING\nYOUR ARCHIVE",style=MaterialTheme.typography.displaySmall.copy(fontSize=48.sp,lineHeight=45.sp));Text(query.ifBlank{"Understanding the scene…"},Modifier.padding(top=12.dp),color=MaterialTheme.colorScheme.secondary,style=MaterialTheme.typography.bodyLarge);Spacer(Modifier.weight(1f));Row(Modifier.fillMaxWidth().border(1.dp,MaterialTheme.colorScheme.outlineVariant).padding(16.dp),verticalAlignment=Alignment.CenterVertically){Box(Modifier.size(7.dp).background(MaterialTheme.colorScheme.secondary));Spacer(Modifier.width(10.dp));Text("NOTHING UPLOADED",style=MaterialTheme.typography.labelSmall,letterSpacing=1.1.sp);Spacer(Modifier.weight(1f));Text("OFFLINE",color=MaterialTheme.colorScheme.onSurfaceVariant,style=MaterialTheme.typography.labelSmall)}}}}

@Composable private fun SearchResults(query:String,filter:String,onFilter:(String)->Unit,edit:()->Unit,matches:List<SearchMatch>,openViewer:(SearchMatch)->Unit){
    var more by rememberSaveable(query,filter){mutableStateOf(false)}
    val ranked=matches.filter{filter=="All"||filter=="Videos"&&it.media.kind==MediaKind.VIDEO||filter=="Photos"&&it.media.kind==MediaKind.IMAGE||filter=="Screenshots"&&it.media.isScreenshot}
    Column(Modifier.fillMaxSize()){
        Row(Modifier.fillMaxWidth().padding(horizontal=20.dp),verticalAlignment=Alignment.CenterVertically){SquareIconButton(Icons.AutoMirrored.Rounded.ArrowBack,"Edit search",edit);Spacer(Modifier.width(15.dp));Column(Modifier.weight(1f)){PremiumEyebrow("RETRIEVAL / COMPLETE");Text("BEST MATCH",style=MaterialTheme.typography.headlineMedium)};Text("01",fontFamily=FontFamily.Serif,fontSize=44.sp,color=MaterialTheme.colorScheme.secondary)}
        LazyRow(Modifier.padding(vertical=14.dp),contentPadding=PaddingValues(horizontal=20.dp),horizontalArrangement=Arrangement.spacedBy(7.dp)){items(listOf("All","Photos","Videos","Screenshots")){CircleFilter(it,filter==it){onFilter(it)}}}
        if(ranked.isEmpty())BackendEmpty("No confident match yet") else LazyColumn(contentPadding=PaddingValues(bottom=122.dp)){
            item(key="best-${ranked.first().media.uri}"){BestMatchCard(ranked.first()){openViewer(ranked.first())}}
            if(ranked.size>1&&!more)item(key="more-button"){Row(Modifier.padding(horizontal=12.dp,vertical=8.dp).fillMaxWidth().height(68.dp).clip(CircleShape).background(BrandGlass.copy(.68f)).border(1.dp,BrandGlassEdge.copy(.22f),CircleShape).clickable{more=true}.padding(start=22.dp,end=9.dp),verticalAlignment=Alignment.CenterVertically){Text("VIEW MORE MATCHES",Modifier.weight(1f),fontWeight=FontWeight.Bold,letterSpacing=1.2.sp);Box(Modifier.size(50.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(.15f)),contentAlignment=Alignment.Center){Text("+${ranked.size-1}",fontFamily=FontFamily.Serif,fontSize=23.sp,color=MaterialTheme.colorScheme.primary)}}}
            if(more){item(key="more-heading"){Row(Modifier.padding(20.dp,26.dp,20.dp,12.dp)){PremiumEyebrow("MORE MATCHES / ${ranked.size-1}")}};item(key="more-film"){LazyRow(contentPadding=PaddingValues(horizontal=20.dp),horizontalArrangement=Arrangement.spacedBy(9.dp)){items(ranked.drop(1),key={it.media.uri}){match->RealMemory(match,Modifier.width(178.dp).height(244.dp)){openViewer(match)}}}}}
        }
    }
}

@Composable private fun BestMatchCard(match:SearchMatch,onClick:()->Unit){
    val media=match.media;val reason=match.explanations.firstOrNull()?:"Strongest local result";val shape=RoundedCornerShape(48.dp)
    Column(Modifier.fillMaxWidth().clip(shape).clickable(onClick=onClick).semantics(mergeDescendants=true){contentDescription="Best ${media.kind.name.lowercase()} match. $reason"}){
        Box(Modifier.fillMaxWidth().height(430.dp)){
            AsyncImage(Uri.parse(media.uri),null,Modifier.fillMaxSize(),contentScale=ContentScale.Crop)
            Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(BrandNavyScrim.copy(.62f),Color.Transparent,BrandNavyScrim.copy(.46f)))))
            Text("01",Modifier.align(Alignment.TopStart).padding(20.dp),fontFamily=FontFamily.Serif,fontSize=82.sp,lineHeight=82.sp,color=BrandIce.copy(.94f))
            Column(Modifier.align(Alignment.CenterEnd).padding(end=16.dp),horizontalAlignment=Alignment.End){Text("CONFIDENCE",style=MaterialTheme.typography.labelSmall,letterSpacing=1.3.sp,color=BrandIce);Box(Modifier.padding(top=8.dp).width(3.dp).height(120.dp).background(BrandGlassEdge.copy(.24f))){Box(Modifier.align(Alignment.BottomCenter).fillMaxWidth().fillMaxHeight(if(match.confidence==MatchConfidence.STRONG).92f else .58f).background(MaterialTheme.colorScheme.primary))}}
            if(media.kind==MediaKind.VIDEO)Box(Modifier.align(Alignment.BottomEnd).padding(17.dp)){MiniGlassPill(match.bestTimestampMs?.let(::formatMoment)?:"VIDEO")}
        }
        Row(Modifier.fillMaxWidth().background(BrandGlassStrong).border(1.dp,BrandGlassEdge.copy(.24f)).padding(start=24.dp,top=18.dp,end=12.dp,bottom=18.dp),verticalAlignment=Alignment.CenterVertically){Column(Modifier.weight(1f)){Text(if(media.kind==MediaKind.VIDEO)"MATCHING VIDEO MOMENT" else "MATCHING PHOTO",color=MaterialTheme.colorScheme.onSurface,fontWeight=FontWeight.Bold,letterSpacing=1.1.sp);Text(reason,Modifier.padding(top=7.dp),color=MaterialTheme.colorScheme.onSurfaceVariant,maxLines=2,overflow=TextOverflow.Ellipsis)};Box(Modifier.size(58.dp).clip(CircleShape).background(MaterialTheme.colorScheme.secondary),contentAlignment=Alignment.Center){Icon(Icons.Rounded.ArrowOutward,"Open match",tint=MaterialTheme.colorScheme.onSecondary)}}
    }
}

@Composable private fun RealMemory(match:SearchMatch,modifier:Modifier,onClick:()->Unit){val media=match.media;val reason=match.explanations.firstOrNull()?:"Local match";val shape=RoundedCornerShape(38.dp);Box(modifier.clip(shape).clickable(onClick=onClick).border(1.dp,BrandGlassEdge.copy(.24f),shape).semantics(mergeDescendants=true){contentDescription="${media.kind.name.lowercase()} match. $reason"}){AsyncImage(Uri.parse(media.uri),null,Modifier.fillMaxSize(),contentScale=ContentScale.Crop);Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent,Color.Transparent,BrandNavyScrim.copy(.92f)))));if(media.kind==MediaKind.VIDEO)Box(Modifier.align(Alignment.TopEnd).padding(9.dp)){MiniGlassPill(match.bestTimestampMs?.let(::formatMoment)?:"VIDEO")};Column(Modifier.align(Alignment.BottomStart).padding(16.dp)){PremiumEyebrow(if(media.kind==MediaKind.VIDEO)"VIDEO" else "PHOTO",light=true);Text(reason,Modifier.padding(top=5.dp),color=BrandIce,style=MaterialTheme.typography.labelLarge,maxLines=2,overflow=TextOverflow.Ellipsis)}}}

@Composable private fun MediaViewer(match:SearchMatch,close:()->Unit){val context=LocalContext.current;val media=match.media;val sheet=RoundedCornerShape(topStart=28.dp,topEnd=28.dp,bottomEnd=0.dp,bottomStart=0.dp);Box(Modifier.fillMaxSize().background(BrandNavyDeep)){AsyncImage(Uri.parse(media.uri),null,Modifier.fillMaxSize(),contentScale=ContentScale.Fit);Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(BrandNavyScrim.copy(.68f),Color.Transparent,BrandNavyScrim.copy(.82f)))));Row(Modifier.statusBarsPadding().padding(18.dp).fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween){SquareIconButton(Icons.AutoMirrored.Rounded.ArrowBack,"Back",close,true);SquareIconButton(Icons.AutoMirrored.Rounded.OpenInNew,"Open media",{context.startActivity(Intent(Intent.ACTION_VIEW,Uri.parse(media.uri)).setDataAndType(Uri.parse(media.uri),if(media.kind==MediaKind.VIDEO)"video/*" else "image/*").addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION))},true)};Column(Modifier.align(Alignment.BottomCenter).navigationBarsPadding().fillMaxWidth().clip(sheet).background(BrandGlassStrong).border(1.dp,BrandGlassEdge.copy(.26f),sheet).padding(20.dp)){Row{Text("01",fontFamily=FontFamily.Serif,fontSize=58.sp,color=MaterialTheme.colorScheme.primary);Spacer(Modifier.weight(1f));PremiumEyebrow("BEST LOCAL MATCH")};Text(if(media.kind==MediaKind.VIDEO)"MATCHING VIDEO MOMENT" else "MATCHING PHOTO",Modifier.padding(top=4.dp),color=MaterialTheme.colorScheme.onSurface,fontWeight=FontWeight.Bold,letterSpacing=1.2.sp);Text(match.explanations.joinToString(" · ").ifBlank{"Strongest local result"},Modifier.padding(top=8.dp),color=MaterialTheme.colorScheme.onSurfaceVariant,style=MaterialTheme.typography.bodyMedium);Row(Modifier.padding(top=16.dp),verticalAlignment=Alignment.CenterVertically){Text(confidenceLabel(match).uppercase(),style=MaterialTheme.typography.labelSmall,color=MaterialTheme.colorScheme.primary);match.bestTimestampMs?.let{Spacer(Modifier.width(15.dp));Text("MOMENT ${formatMoment(it)}",style=MaterialTheme.typography.labelSmall,color=MaterialTheme.colorScheme.primary)};Spacer(Modifier.weight(1f));Icon(Icons.Rounded.Lock,null,Modifier.size(17.dp),tint=MaterialTheme.colorScheme.primary)}}}}

private fun formatMoment(ms:Long):String="${ms/60000}:${(ms/1000%60).toString().padStart(2,'0')}"
private fun confidenceLabel(match:SearchMatch)=when(match.confidence){MatchConfidence.STRONG->"High confidence";MatchConfidence.POSSIBLE->"Possible match";MatchConfidence.WEAK->"Closest match"}

@Composable private fun TermsScreen(){
    var result by rememberSaveable{mutableStateOf(false)}
    if(result){ TermsResult{result=false}; return }
    LazyColumn(contentPadding=PaddingValues(0.dp,44.dp,0.dp,118.dp)){
        item{Column(Modifier.padding(horizontal=20.dp)){PremiumEyebrow("DOCUMENT INTELLIGENCE / 002");Text("READ\nBETWEEN\nTHE LINES",Modifier.padding(top=12.dp),style=MaterialTheme.typography.displaySmall.copy(fontSize=58.sp,lineHeight=54.sp,letterSpacing=(-1.8).sp));Text("Private analysis for the agreements that shape your choices.",Modifier.padding(top=14.dp,bottom=27.dp).widthIn(max=320.dp),color=MaterialTheme.colorScheme.onSurfaceVariant,style=MaterialTheme.typography.bodyLarge)}}
        item{ArchiveActionRow("01","PASTE A LINK",Icons.Rounded.Link){}}
        item{ArchiveActionRow("02","PASTE TEXT",Icons.Rounded.ContentPaste){}}
        item{ArchiveActionRow("03","IMPORT A FILE",Icons.Rounded.UploadFile){}}
        item{Box(Modifier.padding(20.dp,24.dp,20.dp,0.dp)){PremiumPrimaryButton("Analyze agreement",Icons.Rounded.AutoAwesome,Modifier.fillMaxWidth()){result=true}}}
        item{Row(Modifier.padding(20.dp,18.dp),verticalAlignment=Alignment.CenterVertically){Box(Modifier.size(7.dp).background(MaterialTheme.colorScheme.secondary));Spacer(Modifier.width(10.dp));Text("LOCAL ANALYSIS / INFORMATIONAL ONLY",style=MaterialTheme.typography.labelSmall,color=MaterialTheme.colorScheme.onSurfaceVariant,letterSpacing=.9.sp)}}
    }
}

@Composable private fun ArchiveActionRow(number:String,label:String,icon:ImageVector,onClick:()->Unit){Row(Modifier.padding(horizontal=12.dp,vertical=6.dp).fillMaxWidth().height(86.dp).clip(CircleShape).background(BrandGlass.copy(.62f)).border(1.dp,BrandGlassEdge.copy(.24f),CircleShape).clickable(onClick=onClick).padding(horizontal=10.dp),verticalAlignment=Alignment.CenterVertically){Box(Modifier.size(58.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(.14f)),contentAlignment=Alignment.Center){Text(number,fontFamily=FontFamily.Serif,fontSize=25.sp,color=MaterialTheme.colorScheme.primary)};Icon(icon,null,Modifier.padding(start=15.dp).size(22.dp),tint=MaterialTheme.colorScheme.primary);Text(label,Modifier.padding(start=17.dp).weight(1f),fontWeight=FontWeight.Bold,letterSpacing=1.2.sp);Box(Modifier.size(48.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(.12f)),contentAlignment=Alignment.Center){Icon(Icons.Rounded.ArrowOutward,"Open $label",Modifier.size(19.dp),tint=MaterialTheme.colorScheme.primary)}}}

@Composable private fun TermsResult(back:()->Unit){BackHandler(onBack=back);LazyColumn(contentPadding=PaddingValues(20.dp,44.dp,20.dp,132.dp),verticalArrangement=Arrangement.spacedBy(14.dp)){item{Row(verticalAlignment=Alignment.CenterVertically){OrbIcon(Icons.AutoMirrored.Rounded.ArrowBack,"Back",back);Spacer(Modifier.width(14.dp));Column{PremiumEyebrow("TERMS AI");Text("Agreement health",style=MaterialTheme.typography.headlineLarge)}}};item{GlassCard{Row(Modifier.padding(8.dp),verticalAlignment=Alignment.CenterVertically){CircularRiskIndicator(.58f);Spacer(Modifier.width(20.dp));Column{PremiumEyebrow("MODERATE");Text("A few clauses need attention",Modifier.padding(top=5.dp),style=MaterialTheme.typography.titleLarge);Text("Renewal and dispute terms deserve a closer look.",Modifier.padding(top=4.dp),color=MaterialTheme.colorScheme.onSurfaceVariant)}}}};item{GlassCard{PremiumEyebrow("THE ONE-MINUTE READ");Text("The service renews annually. You can cancel before billing, but refunds are limited and disputes use arbitration.",Modifier.padding(top=10.dp),style=MaterialTheme.typography.bodyLarge,color=MaterialTheme.colorScheme.onSurfaceVariant)}};items(listOf("Important" to Icons.Rounded.PriorityHigh,"Watch out" to Icons.Rounded.Visibility,"Good" to Icons.Rounded.ThumbUp,"Money" to Icons.Rounded.Payments,"Cancellation" to Icons.Rounded.EventBusy,"Privacy" to Icons.Rounded.Lock,"Your rights" to Icons.Rounded.Balance)){(title,icon)->GlassDisclosure(title,icon)}}}

@Composable private fun ActivityScreen(){LazyColumn(contentPadding=PaddingValues(0.dp,44.dp,0.dp,118.dp)){item{Column(Modifier.padding(horizontal=20.dp)){PageHeader("Activity","A forensic record of what Honorable understood.",Icons.Rounded.Timeline);Spacer(Modifier.height(20.dp))}};items(listOf(Triple("Tennis outside","12 moments found",Icons.Rounded.Search),Triple("Subscription terms","Moderate agreement health",Icons.Rounded.Policy),Triple("Library synchronized","48 new memories indexed",Icons.Rounded.Sync),Triple("Flight screenshot","Air Canada text matched",Icons.Rounded.Screenshot)).withIndex().toList()){(index,event)->ArchiveActivityRow(index,event.first,event.second,event.third)}}}

@Composable private fun ArchiveActivityRow(index:Int,title:String,detail:String,icon:ImageVector){Row(Modifier.padding(horizontal=12.dp,vertical=6.dp).fillMaxWidth().heightIn(min=104.dp).clip(RoundedCornerShape(52.dp)).background(BrandGlass.copy(.56f)).border(1.dp,BrandGlassEdge.copy(.20f),RoundedCornerShape(52.dp)).padding(horizontal=12.dp),verticalAlignment=Alignment.CenterVertically){Box(Modifier.size(72.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(.13f)),contentAlignment=Alignment.Center){Column(horizontalAlignment=Alignment.CenterHorizontally){Text("TODAY",style=MaterialTheme.typography.labelSmall,color=MaterialTheme.colorScheme.primary,letterSpacing=.7.sp);Text((9+index).toString().padStart(2,'0')+":${(index*13).toString().padStart(2,'0')}",fontFamily=FontFamily.Serif,fontSize=18.sp)}};Column(Modifier.padding(start=16.dp).weight(1f)){Text(title.uppercase(),fontWeight=FontWeight.Bold,letterSpacing=.7.sp);Text(detail,Modifier.padding(top=6.dp),color=MaterialTheme.colorScheme.onSurfaceVariant)};Box(Modifier.size(48.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(.12f)),contentAlignment=Alignment.Center){Icon(icon,null,tint=MaterialTheme.colorScheme.primary)}}}

@Composable private fun SettingsScreen(openPrivacy:()->Unit,openPlus:()->Unit){LazyColumn(contentPadding=PaddingValues(20.dp,48.dp,20.dp,132.dp),verticalArrangement=Arrangement.spacedBy(20.dp)){item{PageHeader("Settings","Privacy, intelligence, and your experience.",Icons.Rounded.Tune)};item{PremiumUpgradeCard(openPlus)};item{SettingCluster("PRIVACY",listOf(SettingItem("Privacy promise","Everything stays local",Icons.Rounded.Shield,openPrivacy),SettingItem("Permissions","Photos and videos",Icons.Rounded.Key,{})))};item{SettingCluster("INTELLIGENCE",listOf(SettingItem("AI & search","Private on-device search",Icons.Rounded.AutoAwesome,{}),SettingItem("Storage & index","Your local media library",Icons.Rounded.Storage,{})))};item{SettingCluster("HONORABLE",listOf(SettingItem("Appearance","System theme · Honorable palette",Icons.Rounded.Palette,{}),SettingItem("About","Version 0.1.0",Icons.Rounded.Info,{})))}}}

@Composable private fun PrivacyScreen(close:()->Unit){OverlayScaffold(close){LazyColumn(contentPadding=PaddingValues(0.dp,4.dp,0.dp,42.dp)){item{Column(Modifier.padding(horizontal=20.dp)){PremiumEyebrow("THE HONORABLE PROMISE / 001");Text("YOURS.\nFULL STOP.",Modifier.padding(top=18.dp),style=MaterialTheme.typography.displaySmall.copy(fontSize=65.sp,lineHeight=60.sp,letterSpacing=(-2).sp));Text("Private intelligence should not require a cloud copy of your life.",Modifier.padding(top=18.dp,bottom=28.dp).widthIn(max=310.dp),color=MaterialTheme.colorScheme.onSurfaceVariant,style=MaterialTheme.typography.bodyLarge)}};item{ArchiveTrustRow("01","LOCAL AI",Icons.Rounded.AutoAwesome)};item{ArchiveTrustRow("02","LOCAL OCR",Icons.Rounded.TextFields)};item{ArchiveTrustRow("03","LOCAL INDEX",Icons.Rounded.Storage)};item{ArchiveTrustRow("04","NO UPLOAD",Icons.Rounded.CloudOff)};item{Row(Modifier.padding(20.dp,24.dp),verticalAlignment=Alignment.CenterVertically){Box(Modifier.width(3.dp).height(54.dp).background(MaterialTheme.colorScheme.secondary));Text("Photos, frames, and searches remain on this device.",Modifier.padding(start=14.dp),color=MaterialTheme.colorScheme.onSurfaceVariant)}}}}}

@Composable private fun ArchiveTrustRow(number:String,title:String,icon:ImageVector){Row(Modifier.padding(horizontal=12.dp,vertical=5.dp).fillMaxWidth().height(78.dp).clip(CircleShape).background(BrandGlass.copy(.60f)).border(1.dp,BrandGlassEdge.copy(.22f),CircleShape).padding(horizontal=10.dp),verticalAlignment=Alignment.CenterVertically){Box(Modifier.size(56.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(.14f)),contentAlignment=Alignment.Center){Text(number,fontFamily=FontFamily.Serif,fontSize=23.sp,color=MaterialTheme.colorScheme.primary)};Text(title,Modifier.padding(start=16.dp).weight(1f),fontWeight=FontWeight.Bold,letterSpacing=1.3.sp);Box(Modifier.size(48.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(.12f)),contentAlignment=Alignment.Center){Icon(icon,null,Modifier.size(23.dp),tint=MaterialTheme.colorScheme.primary)}}}

@Composable private fun PlusScreen(close:()->Unit){var annual by rememberSaveable{mutableStateOf(true)};OverlayScaffold(close){LazyColumn(contentPadding=PaddingValues(0.dp,4.dp,0.dp,42.dp)){item{val shape=RoundedCornerShape(52.dp);Box(Modifier.padding(horizontal=12.dp).fillMaxWidth().height(300.dp).clip(shape).background(Brush.linearGradient(listOf(MaterialTheme.colorScheme.secondary.copy(.84f),MaterialTheme.colorScheme.primaryContainer.copy(.82f),BrandGlassStrong))).border(1.dp,BrandGlassEdge.copy(.30f),shape)){Text("+",Modifier.align(Alignment.TopEnd).offset(x=10.dp,y=(-58).dp),fontFamily=FontFamily.Serif,fontSize=300.sp,color=MaterialTheme.colorScheme.primary.copy(.12f));Column(Modifier.fillMaxSize().padding(28.dp)){Text("HONORABLE / PLUS",color=MaterialTheme.colorScheme.onSurface,fontWeight=FontWeight.Bold,letterSpacing=1.5.sp);Spacer(Modifier.weight(1f));Text("MORE\nINTELLIGENCE.",style=MaterialTheme.typography.displaySmall.copy(fontSize=52.sp,lineHeight=48.sp),color=MaterialTheme.colorScheme.onSurface);Text("THE SAME PRIVATE FOUNDATION.",Modifier.padding(top=12.dp),color=MaterialTheme.colorScheme.onSurfaceVariant,style=MaterialTheme.typography.labelLarge)}}};item{Column(Modifier.padding(20.dp)){PremiumEyebrow("WHAT'S INCLUDED");listOf("Advanced Memories AI","Advanced video search","Terms AI","No ads","Future premium intelligence").forEachIndexed{i,label->Row(Modifier.padding(vertical=3.dp).fillMaxWidth().height(56.dp).clip(CircleShape).background(BrandGlass.copy(.42f)).border(1.dp,BrandGlassEdge.copy(.20f),CircleShape),verticalAlignment=Alignment.CenterVertically){Box(Modifier.padding(start=6.dp).size(44.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(.13f)),contentAlignment=Alignment.Center){Text((i+1).toString().padStart(2,'0'),fontFamily=FontFamily.Serif,color=MaterialTheme.colorScheme.primary)};Text(label.uppercase(),Modifier.padding(start=13.dp),fontWeight=FontWeight.SemiBold,style=MaterialTheme.typography.labelMedium)}};Spacer(Modifier.height(22.dp));Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){PlanChoice("Monthly","$5.99",!annual,Modifier.weight(1f)){annual=false};PlanChoice("Annual","$39.99",annual,Modifier.weight(1f)){annual=true}};Spacer(Modifier.height(12.dp));PremiumPrimaryButton("Continue",Icons.AutoMirrored.Rounded.ArrowForward,Modifier.fillMaxWidth()){};Text("PREVIEW ONLY / STORE PROCESSING NOT CONFIGURED",Modifier.padding(top=12.dp),style=MaterialTheme.typography.labelSmall,color=MaterialTheme.colorScheme.onSurfaceVariant,letterSpacing=.6.sp)}}}}}

@Composable private fun FloatingGlassDock(selected:MainTab,onSelect:(MainTab)->Unit,modifier:Modifier=Modifier){Row(modifier.navigationBarsPadding().padding(horizontal=12.dp).padding(bottom=10.dp).fillMaxWidth().height(78.dp).shadow(18.dp,CircleShape,ambientColor=MaterialTheme.colorScheme.secondary.copy(.20f),spotColor=MaterialTheme.colorScheme.secondary.copy(.28f)).clip(CircleShape).background(BrandGlassStrong).border(1.dp,BrandGlassEdge.copy(.28f),CircleShape),verticalAlignment=Alignment.CenterVertically){MainTab.entries.forEach{tab->val active=tab==selected;val background by animateColorAsState(if(active)MaterialTheme.colorScheme.primary.copy(.18f) else Color.Transparent,label="dock-bg");val foreground=if(active)MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant;Column(Modifier.weight(1f).padding(5.dp).fillMaxHeight().clip(CircleShape).background(background).clickable{onSelect(tab)}.semantics{this.selected=active;contentDescription=tab.label;role=Role.Tab},verticalArrangement=Arrangement.Center,horizontalAlignment=Alignment.CenterHorizontally){Icon(tab.icon,null,Modifier.size(if(active)23.dp else 20.dp),tint=foreground);Text(tab.label.uppercase(),Modifier.padding(top=4.dp),style=MaterialTheme.typography.labelSmall.copy(fontSize=8.sp),letterSpacing=.45.sp,color=foreground,fontWeight=if(active)FontWeight.Bold else FontWeight.Normal)}}}}

@Composable private fun PageHeader(title:String,subtitle:String,icon:ImageVector){Column(Modifier.fillMaxWidth()){Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically){PremiumEyebrow("HONORABLE / ${title.uppercase()}");Spacer(Modifier.weight(1f));Icon(icon,null,Modifier.size(24.dp),tint=MaterialTheme.colorScheme.secondary)};Text(title.uppercase(),Modifier.padding(top=13.dp),style=MaterialTheme.typography.displaySmall.copy(fontSize=52.sp,lineHeight=49.sp,letterSpacing=(-1.5).sp));Text(subtitle,Modifier.padding(top=8.dp),color=MaterialTheme.colorScheme.onSurfaceVariant,style=MaterialTheme.typography.bodyLarge);HorizontalDivider(Modifier.padding(top=18.dp),color=MaterialTheme.colorScheme.secondary.copy(.65f))}}
@Composable private fun PremiumEyebrow(text:String,light:Boolean=false){Text(text,color=if(light)BrandIce else MaterialTheme.colorScheme.primary,style=MaterialTheme.typography.labelSmall,fontWeight=FontWeight.Bold,letterSpacing=1.55.sp)}
@Composable private fun AccentIcon(icon:ImageVector,size:Dp=46.dp){Surface(Modifier.size(size),CircleShape,color=MaterialTheme.colorScheme.primaryContainer.copy(.68f),border=BorderStroke(1.dp,BrandGlassEdge.copy(.32f))){Box(contentAlignment=Alignment.Center){Icon(icon,null,Modifier.size((size.value*.42f).dp),tint=MaterialTheme.colorScheme.primary)}}}
@Composable private fun PremiumPrimaryButton(label:String,icon:ImageVector,modifier:Modifier=Modifier,onClick:()->Unit){Button(onClick,modifier.heightIn(min=66.dp),shape=CircleShape,colors=ButtonDefaults.buttonColors(containerColor=MaterialTheme.colorScheme.primary,contentColor=MaterialTheme.colorScheme.onPrimary),elevation=ButtonDefaults.buttonElevation(defaultElevation=0.dp,pressedElevation=0.dp),contentPadding=PaddingValues(start=24.dp,end=7.dp)){Text(label.uppercase(),Modifier.weight(1f),style=MaterialTheme.typography.labelLarge,letterSpacing=1.1.sp);Box(Modifier.size(52.dp).clip(CircleShape).background(MaterialTheme.colorScheme.secondary),contentAlignment=Alignment.Center){Icon(icon,null,Modifier.size(19.dp),tint=MaterialTheme.colorScheme.onSecondary)}}}
@Composable private fun PremiumUpgradeCard(onClick:()->Unit){Row(Modifier.fillMaxWidth().height(142.dp).clip(RoundedCornerShape(71.dp)).background(Brush.linearGradient(listOf(MaterialTheme.colorScheme.secondary.copy(.76f),MaterialTheme.colorScheme.primaryContainer.copy(.80f),BrandGlassStrong))).border(1.dp,BrandGlassEdge.copy(.30f),RoundedCornerShape(71.dp)).clickable(onClick=onClick).padding(horizontal=12.dp),verticalAlignment=Alignment.CenterVertically){Box(Modifier.size(86.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(.13f)),contentAlignment=Alignment.Center){Text("+",fontFamily=FontFamily.Serif,fontSize=70.sp,color=MaterialTheme.colorScheme.primary.copy(.52f))};Column(Modifier.padding(start=16.dp).weight(1f)){Text("HONORABLE PLUS",color=MaterialTheme.colorScheme.onSurface,fontWeight=FontWeight.Bold,letterSpacing=1.4.sp);Text("MORE INTELLIGENCE",Modifier.padding(top=7.dp),style=MaterialTheme.typography.titleLarge,color=MaterialTheme.colorScheme.onSurface);Text("Same private foundation",color=MaterialTheme.colorScheme.onSurfaceVariant)};Box(Modifier.size(58.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(.16f)),contentAlignment=Alignment.Center){Icon(Icons.Rounded.ArrowOutward,"Open Honorable Plus",tint=MaterialTheme.colorScheme.primary)}}}

@Composable fun GlassSurface(modifier:Modifier=Modifier,shape:Shape=RoundedCornerShape(32.dp),alpha:Float=.64f,shadow:Dp=0.dp,dark:Boolean=false,content:@Composable BoxScope.()->Unit){val colors=MaterialTheme.colorScheme;val base=if(dark)Color(0xFF0A2A50)else Color(0xFF123D70);val resolvedAlpha=if(dark)maxOf(alpha,.74f)else alpha;val elevation=if(shadow.value>0f)shadow else 10.dp;Box(modifier.shadow(elevation,shape,ambientColor=colors.secondary.copy(.16f),spotColor=colors.secondary.copy(.24f)).clip(shape).background(Brush.linearGradient(listOf(base.copy(resolvedAlpha),colors.primaryContainer.copy(resolvedAlpha*.52f)))).border(1.dp,BrandGlassEdge.copy(.28f),shape),contentAlignment=Alignment.Center,content=content)}
@Composable private fun GlassCard(modifier:Modifier=Modifier,content:@Composable ColumnScope.()->Unit){GlassSurface(modifier.fillMaxWidth(),shape=RoundedCornerShape(32.dp)){Column(Modifier.padding(22.dp),content=content)}}
@Composable private fun GlassCircle(modifier:Modifier,content:@Composable BoxScope.()->Unit){GlassSurface(modifier,CircleShape,.66f,8.dp,content=content)}
@Composable private fun OrbIcon(icon:ImageVector,description:String,onClick:()->Unit,dark:Boolean=false)=SquareIconButton(icon,description,onClick,dark)
@Composable private fun SquareIconButton(icon:ImageVector,description:String,onClick:()->Unit,dark:Boolean=false){GlassSurface(Modifier.size(52.dp).clickable(onClick=onClick),CircleShape,if(dark).76f else .58f,8.dp,dark){Icon(icon,description,Modifier.size(21.dp),tint=if(dark)BrandIce else MaterialTheme.colorScheme.primary)}}
@Composable private fun CircleFilter(label:String,active:Boolean,onClick:()->Unit){val color by animateColorAsState(if(active)MaterialTheme.colorScheme.primary.copy(.20f) else BrandGlass.copy(.54f),label="filter");Surface(Modifier.heightIn(min=48.dp).clickable(onClick=onClick).semantics{selected=active},shape=RoundedCornerShape(18.dp),color=color,border=BorderStroke(1.dp,if(active)MaterialTheme.colorScheme.primary else BrandGlassEdge.copy(.22f))){Row(Modifier.padding(horizontal=15.dp,vertical=11.dp),verticalAlignment=Alignment.CenterVertically){Text(label.uppercase(),color=if(active)MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,style=MaterialTheme.typography.labelSmall,fontWeight=FontWeight.Bold,letterSpacing=.8.sp)}}}
@Composable private fun CircularRiskIndicator(score:Float){Box(Modifier.size(106.dp),contentAlignment=Alignment.Center){CircularProgressIndicator({score},Modifier.fillMaxSize(),color=MaterialTheme.colorScheme.secondary,strokeWidth=8.dp,trackColor=MaterialTheme.colorScheme.surfaceVariant);Column(horizontalAlignment=Alignment.CenterHorizontally){Text("58",style=MaterialTheme.typography.headlineMedium);Text("/ 100",style=MaterialTheme.typography.labelSmall)}}}
@Composable private fun GlassDisclosure(title:String,icon:ImageVector){var open by rememberSaveable(title){mutableStateOf(title=="Important")};GlassCard(Modifier.clickable{open=!open}){Row(verticalAlignment=Alignment.CenterVertically){GlassCircle(Modifier.size(42.dp)){Icon(icon,null,Modifier.size(20.dp),tint=MaterialTheme.colorScheme.primary)};Spacer(Modifier.width(13.dp));Text(title,Modifier.weight(1f),style=MaterialTheme.typography.titleMedium);Icon(if(open)Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,if(open)"Collapse" else "Expand")};AnimatedVisibility(open){Text("Automatic renewal and individual arbitration deserve a closer look.",Modifier.padding(top=12.dp),color=MaterialTheme.colorScheme.onSurfaceVariant)}}}
private data class SettingItem(val title:String,val detail:String,val icon:ImageVector,val click:()->Unit)
@Composable private fun SettingCluster(label:String,items:List<SettingItem>){Column{PremiumEyebrow(label);Spacer(Modifier.height(9.dp));GlassCard{items.forEachIndexed{i,item->if(i>0)HorizontalDivider(Modifier.padding(vertical=11.dp),color=MaterialTheme.colorScheme.outlineVariant.copy(.64f));Row(Modifier.fillMaxWidth().heightIn(min=56.dp).clickable(onClick=item.click),verticalAlignment=Alignment.CenterVertically){AccentIcon(item.icon,44.dp);Spacer(Modifier.width(13.dp));Column(Modifier.weight(1f)){Text(item.title,style=MaterialTheme.typography.titleMedium);Text(item.detail,Modifier.padding(top=2.dp),style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant)};Icon(Icons.Rounded.ChevronRight,null,Modifier.size(19.dp),tint=MaterialTheme.colorScheme.onSurfaceVariant)}}}}}
@Composable private fun TimelineRow(title:String,detail:String,icon:ImageVector){Row(Modifier.height(IntrinsicSize.Min)){Column(horizontalAlignment=Alignment.CenterHorizontally){GlassCircle(Modifier.size(46.dp)){Icon(icon,null,Modifier.size(21.dp),tint=MaterialTheme.colorScheme.primary)};Box(Modifier.width(1.dp).weight(1f).background(MaterialTheme.colorScheme.primary.copy(.22f)))};Spacer(Modifier.width(14.dp));GlassCard(Modifier.padding(bottom=10.dp)){Text(title,style=MaterialTheme.typography.titleMedium);Text(detail,color=MaterialTheme.colorScheme.onSurfaceVariant);Text("Today",Modifier.padding(top=8.dp),style=MaterialTheme.typography.labelSmall,color=MaterialTheme.colorScheme.primary)}}}
@Composable private fun PrivacyNode(title:String,icon:ImageVector,modifier:Modifier){GlassCard(modifier){Column(Modifier.fillMaxWidth(),horizontalAlignment=Alignment.CenterHorizontally){GlassCircle(Modifier.size(58.dp)){Icon(icon,null,tint=MaterialTheme.colorScheme.primary)};Spacer(Modifier.height(10.dp));Text(title,style=MaterialTheme.typography.titleMedium)}}}
@Composable private fun PlanChoice(name:String,price:String,selected:Boolean,modifier:Modifier,onClick:()->Unit){Surface(modifier.heightIn(min=132.dp).clickable(onClick=onClick).semantics{this.selected=selected},shape=RoundedCornerShape(20.dp),color=if(selected)MaterialTheme.colorScheme.primary.copy(.18f) else BrandGlass.copy(.52f),border=BorderStroke(1.dp,if(selected)MaterialTheme.colorScheme.primary else BrandGlassEdge.copy(.22f))){Column(Modifier.padding(17.dp)){Text(if(selected)"SELECTED" else "PLAN",style=MaterialTheme.typography.labelSmall,color=if(selected)MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,letterSpacing=1.sp);Text(name.uppercase(),Modifier.padding(top=10.dp),style=MaterialTheme.typography.labelLarge,color=MaterialTheme.colorScheme.onSurface);Text(price,style=MaterialTheme.typography.headlineMedium,color=MaterialTheme.colorScheme.onSurface);if(selected&&name=="Annual")Text("BEST VALUE",style=MaterialTheme.typography.labelSmall,color=MaterialTheme.colorScheme.primary)}}}
@Composable private fun OverlayScaffold(close:()->Unit,content:@Composable BoxScope.()->Unit){AppBackground{Box(Modifier.fillMaxSize().statusBarsPadding()){SquareIconButton(Icons.Rounded.Close,"Close",close);Box(Modifier.fillMaxSize().padding(top=58.dp),content=content)}}}
@Composable private fun SuggestionPill(text:String,onClick:()->Unit){Surface(Modifier.heightIn(min=48.dp).clickable(onClick=onClick),shape=RoundedCornerShape(18.dp),color=BrandGlass.copy(.60f),border=BorderStroke(1.dp,BrandGlassEdge.copy(.24f))){Row(Modifier.padding(horizontal=16.dp,vertical=12.dp),verticalAlignment=Alignment.CenterVertically){Text(text,style=MaterialTheme.typography.labelLarge);Spacer(Modifier.width(10.dp));Icon(Icons.Rounded.ArrowOutward,null,Modifier.size(16.dp),tint=MaterialTheme.colorScheme.primary)}}}
@Composable private fun MiniGlassPill(text:String){Surface(shape=RoundedCornerShape(14.dp),color=BrandGlassStrong,border=BorderStroke(1.dp,BrandGlassEdge.copy(.34f))){Text(text,Modifier.padding(horizontal=8.dp,vertical=5.dp),color=BrandIce,style=MaterialTheme.typography.labelSmall)}}
@Composable private fun PrivacyPill(text:String="Private search · Nothing uploaded"){Surface(shape=RoundedCornerShape(18.dp),color=BrandGlass.copy(.62f),border=BorderStroke(1.dp,BrandGlassEdge.copy(.24f))){Row(Modifier.padding(horizontal=14.dp,vertical=10.dp),verticalAlignment=Alignment.CenterVertically){Icon(Icons.Rounded.Lock,null,Modifier.size(15.dp),tint=MaterialTheme.colorScheme.primary);Spacer(Modifier.width(8.dp));Text(text,style=MaterialTheme.typography.labelMedium,color=MaterialTheme.colorScheme.onSurface)}}}
@Composable private fun SectionLabel(title:String,detail:String){Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.Bottom){Text(title,Modifier.weight(1f),style=MaterialTheme.typography.titleLarge);Text(detail,style=MaterialTheme.typography.labelSmall,fontWeight=FontWeight.Bold,letterSpacing=1.1.sp,color=MaterialTheme.colorScheme.secondary)}}
@Composable private fun Pressable(onClick:()->Unit,content:@Composable (Boolean)->Unit){val source=remember{MutableInteractionSource()};val pressed by source.collectIsPressedAsState();Box(Modifier.clickable(interactionSource=source,indication=null,onClick=onClick)){content(pressed)}}
