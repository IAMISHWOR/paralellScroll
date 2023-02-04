package com.example.parallescrolleffect

import android.content.res.Resources
import android.os.Bundle
import android.view.animation.OvershootInterpolator
import android.window.SplashScreen
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.parallescrolleffect.ui.theme.ParalleScrollEffectTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
           Navigation()
        }
    }

}

@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "splash_screen") {
        composable("splash_screen") {
            SplashScreen(navController = navController)
        }
        composable("main_screen") {
            MainScreen()
        }
    }

}

@Composable
fun SplashScreen(navController: NavController){
    val scale = remember {
        androidx.compose.animation.core.Animatable(0.3f)
    }
    LaunchedEffect(key1 = true){
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 1500,
                easing = {
                    OvershootInterpolator(15f).getInterpolation(it)
                }
            )
        )
        delay(2000)
        navController.navigate("main_screen")
    }
    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color(0xff101010)), contentAlignment = Alignment.Center){
        Image(painter = painterResource(id = R.drawable.logog), contentDescription = "logo",Modifier.scale(scale.value))
    }
}
@Composable
fun MainScreen() {
    val moonScrollSpeed = 0.08f
    val midBgScrollSpeed = 0.03f

    val imageHeight = (LocalConfiguration.current.screenWidthDp * (2f / 3f)).dp
    val lazyListState = rememberLazyListState()

    var moonOffset by remember {
        mutableStateOf(0f)
    }
    var midBgOffset by remember {
        mutableStateOf(0f)
    }

    val nestedScrollConnection = object : NestedScrollConnection {
        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
            val delta = available.y
            val layoutInfo = lazyListState.layoutInfo
            // Check if the first item is visible
            if(lazyListState.firstVisibleItemIndex == 0) {
                return Offset.Zero
            }
            if(layoutInfo.visibleItemsInfo.lastOrNull()?.index == layoutInfo.totalItemsCount - 1) {
                return Offset.Zero
            }
            moonOffset += delta * moonScrollSpeed
            midBgOffset += delta * midBgScrollSpeed
            return Offset.Zero
        }
    }
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .nestedScroll(nestedScrollConnection),
        state = lazyListState
    ) {
        items(10) {
            Text(
                text = "The IK brand",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }
        item {
            Box(
                modifier = Modifier
                    .clipToBounds()
                    .fillMaxWidth()
                    .height(imageHeight + midBgOffset.toDp())
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0xFFf36b21),
                                Color(0xFFf9a521)
                            )
                        )
                    )
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_moonbg),
                    contentDescription = "moon",
                    contentScale = ContentScale.FillWidth,
                    alignment = Alignment.BottomCenter,
                    modifier = Modifier.matchParentSize()
                        .graphicsLayer {
                            translationY = moonOffset
                        }
                )
                Image(
                    painter = painterResource(id = R.drawable.ic_midbg),
                    contentDescription = "mid bg",
                    contentScale = ContentScale.FillWidth,
                    alignment = Alignment.BottomCenter,
                    modifier = Modifier.matchParentSize()
                        .graphicsLayer {
                            translationY = midBgOffset
                        }
                )
                Image(
                    painter = painterResource(id = R.drawable.ic_treebg),
                    contentDescription = "outer bg",
                    contentScale = ContentScale.FillWidth,
                    alignment = Alignment.BottomCenter,
                    modifier = Modifier.matchParentSize()
                )
            }
        }
        items(10) {
            Text(
                text = "The IK brand",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }
    }
}
private fun Float.toDp(): Dp {
    return (this / Resources.getSystem().displayMetrics.density).dp
}


