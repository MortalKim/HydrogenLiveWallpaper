package com.kim.hydrogenlivewallpaper.main

import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Intent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.kim.hydrogenlivewallpaper.LiveWallpaperService
import com.kim.hydrogenlivewallpaper.R
import com.kim.hydrogenlivewallpaper.main.composable.FirstPageSettings
import com.kim.hydrogenlivewallpaper.main.composable.GradientBorderCard
import com.kim.hydrogenlivewallpaper.main.composable.SecondPageSettings

/**
 * Main screen.
 * 第一页/第二页为横向滑动关系（适用于纵向壁纸：左右两页拼接）。
 * 保留原有操作逻辑（所有动作仍委托给 [MainViewModel]）。
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun MainScreen(viewModel: MainViewModel) {
    val ctx = LocalContext.current

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = stringResource(R.string.MainActivityTitle)) },
                    actions = {
                        IconButton(onClick = { viewModel.showAbout.value = true }) {
                            Icon(Icons.Filled.Info, contentDescription = null)
                        }
                    }
                )
            },
            bottomBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.processBitmap() },
                    ) {
                        Text(text = stringResource(R.string.start_calculator))
                    }
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            val intent = Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER)
                            intent.putExtra(
                                WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                                ComponentName(ctx, LiveWallpaperService::class.java)
                            )
                            ctx.startActivity(intent)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    ) {
                        Text(text = stringResource(R.string.set_as_wallpaper))
                    }
                }
            }
        ) { padding ->
            val pagerState = rememberPagerState(initialPage = 0, pageCount = { 2 })

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) { page ->
                when (page) {
                    0 -> PageContent(
                        title = stringResource(R.string.main_page),
                        bitmap = viewModel.firstBitmap.value,
                    ) {
                        FirstPageSettings(viewModel)
                    }

                    else -> PageContent(
                        title = stringResource(R.string.second_page),
                        bitmap = viewModel.secondBitmap.value,
                    ) {
                        SecondPageSettings(viewModel)
                    }
                }
            }
        }
    }
}

@Composable
private fun PageContent(
    title: String,
    bitmap: Any?,
    settings: @Composable () -> Unit,
) {
    val configuration = LocalConfiguration.current

    // 设置区高度统一：进一步压缩给预览更多空间。
    val settingsMinHeight = 140.dp
    val settingsMaxHeight = 260.dp
    val settingsPreferredHeight = (configuration.screenHeightDp * 0.22f).dp

    Column(
        modifier = Modifier
            .fillMaxSize()
            // 外边距略减，减少无效留白
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 标题和设置里会有重复文案（例如 FirstPageSettings/SecondPageSettings 内部），
        // 这里改为更紧凑的样式并减少间距。
        Text(text = title, style = MaterialTheme.typography.titleSmall)

        // 竖屏壁纸预览：占据除设置区之外的剩余所有空间。
        GradientBorderCard(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = true),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                GradientBorderCard(
                    modifier = Modifier
                        .fillMaxHeight()
                        .aspectRatio(9f / 16f)
                ) {
                    if (bitmap != null) {
                        Image(
                            painter = rememberAsyncImagePainter(bitmap),
                            contentScale = ContentScale.Crop,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Text(
                            text = stringResource(R.string.no_image_hint),
                            modifier = Modifier.fillMaxSize(),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        // 操作/设置区：固定高度（两页一致），仅内部滚动。
        GradientBorderCard(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = settingsPreferredHeight, max = settingsPreferredHeight)
                .heightIn(min = settingsMinHeight, max = settingsMaxHeight),
            contentAlignment = Alignment.TopStart
        ) {
            // 关键点：
            // - Box 负责在内容不需要滚动时把它垂直居中
            // - Column 负责在内容较多时滚动
            val scrollState = rememberScrollState()
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        // 当内容大于卡片高度时，Column 会变得可滚动；否则按自身高度测量，从而被 Box 居中。
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    settings()
                }
            }
        }
    }
}
