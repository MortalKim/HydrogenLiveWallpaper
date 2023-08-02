package com.kim.hydrogenlivewallpaper.main

import android.Manifest
import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Intent
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.insets.LocalWindowInsets
import com.kim.hydrogenlivewallpaper.LiveWallpaperService
import com.kim.hydrogenlivewallpaper.WallpaperUtils2
import com.kim.hydrogenlivewallpaper.extension.startOffsetForPage
import com.kim.hydrogenlivewallpaper.main.composable.FirstPageSettings
import com.kim.hydrogenlivewallpaper.main.composable.SecondPageSettings
import com.kim.hydrogenlivewallpaper.main.ui.theme.HydrogenLiveWallpaperTheme
import pub.devrel.easypermissions.EasyPermissions
import kotlin.math.ceil
import kotlin.math.floor
import androidx.core.view.WindowCompat
import com.kim.hydrogenlivewallpaper.main.composable.TransparentSystemBars

/**
 * @ClassName: MainActivity
 * @Description: MainActivity
 * @Author: kim
 * @Date: 7/10/23 9:00 PM
 */
class MainActivity : ComponentActivity() {
    private lateinit var viewModel: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = MainViewModel()
        viewModel.init()
        CheckPermission()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            HydrogenLiveWallpaperTheme {
                // A surface container using the 'background' color from the theme
                TransparentSystemBars()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    Greeting(viewModel)
                }
            }
        }
    }

    fun CheckPermission(){
        val permissions = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        val hasPermissions =
            EasyPermissions.hasPermissions(this, *permissions)
        if (hasPermissions) { //拥有权限
            WallpaperUtils2.getWallpaperSettings(this){
                viewModel.readBitmap()
            }
        } else { //没有权限
            EasyPermissions.requestPermissions(this, "程序运行需要存储权限", 0, *permissions)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun Greeting(viewModel: MainViewModel) {
    val insets = LocalWindowInsets.current
    val horizontalState = rememberPagerState(initialPage = 0)
        Scaffold(topBar = {
            TopAppBar(
                title = {Text(text = "通用氢壁纸")},
                actions = {
                    Button(onClick = { viewModel.showAbout.value = true }) {
                        Icon(Icons.Filled.Info, "")
                    }
                }
            )
        }){
            Column (modifier = Modifier.padding(it)){
                HorizontalPager(
                    pageCount = 2,
                    modifier = Modifier
                        .weight(.7f)
                        .padding(
                            top = 32.dp
                        ),
                    state = horizontalState,
                    pageSpacing = 1.dp,
                    beyondBoundsPageCount = 9,
                ) { page ->
                    Box(
                        modifier = Modifier
                            .zIndex(page * 10f)
                            .padding(
                                start = 64.dp,
                                end = 32.dp,
                            )
                            .graphicsLayer {
                                val startOffset = horizontalState.startOffsetForPage(page)
                                translationX = size.width * (startOffset * .99f)

                                alpha = (2f - startOffset) / 2f

                                val blur = (startOffset * 20f).coerceAtLeast(0.1f)
                                renderEffect = RenderEffect
                                    .createBlurEffect(
                                        blur, blur, Shader.TileMode.DECAL
                                    )
                                    .asComposeRenderEffect()

                                val scale = 1f - (startOffset * .1f)
                                scaleX = scale
                                scaleY = scale
                            }
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                color = Color.Gray,
                                shape = RoundedCornerShape(20.dp)
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        if(page == 0){
                            if(viewModel.firstBitmap.value != null){
                                Image(
                                    painter = rememberAsyncImagePainter(
                                        viewModel.firstBitmap.value,
                                    ),
                                    contentScale = ContentScale.Crop,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            else{
                                Text(text = "选择一张图片以继续", modifier = Modifier.fillMaxSize(), textAlign = TextAlign.Center)
                            }

                        }
                        else{
                            if(viewModel.secondBitmap.value != null){
                                Image(
                                    painter = rememberAsyncImagePainter(
                                        viewModel.secondBitmap.value,
                                    ),
                                    contentScale = ContentScale.Crop,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            else{
                                Text(text = "选择一张图片以继续", modifier = Modifier.fillMaxSize(), textAlign = TextAlign.Center)
                            }

                        }

                    }
                }

                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                        .weight(.3f),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {

                    val verticalState = rememberPagerState()
                    Card (shape = RoundedCornerShape(23.dp), elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
                        modifier = Modifier
                            .padding(5.dp)
                            .fillMaxWidth()){
                        Row (modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp), verticalAlignment = Alignment.CenterVertically){
                            VerticalPager(
                                pageCount = 2,
                                state = verticalState,
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight(),
                                userScrollEnabled = false,
                                horizontalAlignment = Alignment.Start,
                            ) { page ->
                                Column(
                                    verticalArrangement = Arrangement.Top,
                                ) {
                                    if(page == 0){
                                        FirstPageSettings(viewModel)
                                    }
                                    else{
                                        SecondPageSettings(viewModel)
                                    }
                                }
                            }

                            LaunchedEffect(Unit) {
                                snapshotFlow {
                                    Pair(
                                        horizontalState.currentPage,
                                        horizontalState.currentPageOffsetFraction
                                    )
                                }.collect { (page, offset) ->
                                    verticalState.scrollToPage(page, offset)
                                }
                            }
//                    Button(modifier = Modifier.fillMaxHeight(), onClick = { /*TODO*/ }) {
//                        Text(text = "开始生成")
//                    }
                        }
                    }

                }
                Card (shape = RoundedCornerShape(23.dp), elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
                    modifier = Modifier
                        .padding(21.dp)
                        .fillMaxWidth()){
                    Row {
                        val configuration = LocalConfiguration.current
                        val screenHeight = configuration.screenHeightDp.dp
                        val screenWidth = configuration.screenWidthDp.dp
                        Button(modifier = Modifier.weight(1f), onClick = {
                            viewModel.processBitmap()
                        }) {
                            Text(text = "开始计算")
                        }
                        val baseContext = LocalContext.current
                        Button(modifier = Modifier.weight(1f), onClick = {
                            val intent = Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER)
                            intent.putExtra(
                                WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                                ComponentName(baseContext, LiveWallpaperService::class.java)
                            )
                            baseContext.startActivity(intent)
                        }) {
                            Text(text = "设置壁纸")
                        }
                    }
                }


                if(viewModel.showLoadingDialog.value){
                    BoxLoadingDialog(msg = viewModel.loadingMsg.value)
                }
                if(viewModel.showColorPicker.value){
                    val configuration = LocalConfiguration.current
                    val screenHeight = configuration.screenHeightDp.dp
                    val screenWidth = configuration.screenWidthDp.dp
                    ColorPicker(){
                        viewModel.showColorPicker.value = false
                        viewModel.makeColorBitmap(it.toArgb(), screenWidth.value.toInt(),screenHeight.value.toInt())
                    }
                }

                if(viewModel.showAbout.value) {
                    About {
                        viewModel.showAbout.value = false
                    }
                }
            }
        }


}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    HydrogenLiveWallpaperTheme {
        Greeting(MainViewModel())
    }
}

@Composable
fun RatingStars(
    modifier: Modifier = Modifier,
    rating: Float,
) {
    Row(
        modifier = modifier
    ) {

        for (i in 1..5) {
            val animatedScale by animateFloatAsState(
                targetValue = if (floor(rating) >= i) {
                    1f
                } else if (ceil(rating) < i) {
                    0f
                } else {
                    rating - rating.toInt()
                },
                animationSpec = spring(
                    stiffness = Spring.StiffnessMedium
                ),
                label = ""
            )

            Box(
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = rememberVectorPainter(image = Icons.Rounded.Star),
                    contentDescription = null,
                    modifier = Modifier.alpha(.1f),
                )
                Icon(
                    painter = rememberVectorPainter(image = Icons.Rounded.Star),
                    contentDescription = null,
                    modifier = Modifier.scale(animatedScale),
                    tint = Color(0xFFD59411)
                )
            }

        }

    }
}

