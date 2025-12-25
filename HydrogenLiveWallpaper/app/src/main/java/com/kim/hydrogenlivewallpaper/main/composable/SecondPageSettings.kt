package com.kim.hydrogenlivewallpaper.main.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kim.hydrogenlivewallpaper.Constant
import com.kim.hydrogenlivewallpaper.R
import com.kim.hydrogenlivewallpaper.main.ImageSelectorAndCropper
import com.kim.hydrogenlivewallpaper.main.MainViewModel

/**
 * 第二页设置：将较多配置折叠，点击悬浮展开调整。
 */
@Composable
fun SecondPageSettings(mainViewModel: MainViewModel) {
    val showAdvanced = remember { mutableStateOf(false) }

    val mode = mainViewModel.wallpaperSettings.value.secondBitmapGradientState.value
    val modeLabel = when (mode) {
        Constant.StandalonePic -> stringResource(R.string.picture)
        Constant.HalfGradient -> stringResource(R.string.pic_base_gradient)
        Constant.JustGradient -> stringResource(R.string.pic_base_gradient_color)
        Constant.JustColor -> stringResource(R.string.pic_base_color)
        Constant.Color -> stringResource(R.string.custom_color)
        else -> mode.toString()
    }

    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp
    val useCompactActionLayout = screenWidthDp <= 360

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = stringResource(R.string.second_page), style = MaterialTheme.typography.titleSmall)
                Text(
                    text = stringResource(R.string.second_mode) + ": " + modeLabel,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Button(onClick = { showAdvanced.value = true }) {
                Text(text = "展开设置")
            }
        }

        // 主要操作：选择图片 / 使用主屏图片 / 选颜色
        if (mode != Constant.Color) {
            val screenHeight = configuration.screenHeightDp.dp
            val screenWidth = configuration.screenWidthDp.dp

            if (useCompactActionLayout) {
                // 小屏：两行，避免按钮太挤导致点按困难
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { mainViewModel.secondUseFirst() }
                    ) {
                        Text(text = stringResource(R.string.use_main_page_pic))
                    }

                    Box(modifier = Modifier.fillMaxWidth()) {
                        ImageSelectorAndCropper(
                            text = stringResource(R.string.pick_pic_oneline),
                            modifier = Modifier.fillMaxWidth(),
                            height = screenHeight.value.toInt(),
                            width = screenWidth.value.toInt()
                        ) {
                            mainViewModel.selectSecondBitmap(uri = it)
                        }
                    }
                }
            } else {
                // 常规：同一行，节省纵向空间，让壁纸预览占比更大
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = { mainViewModel.secondUseFirst() }
                    ) {
                        Text(text = stringResource(R.string.use_main_page_pic))
                    }

                    // ImageSelectorAndCropper 内部本身会渲染一个可点击入口；这里用 Box 包起来并限制在一列宽度内。
                    Box(modifier = Modifier.weight(1f)) {
                        ImageSelectorAndCropper(
                            text = stringResource(R.string.pick_pic_oneline),
                            modifier = Modifier.fillMaxWidth(),
                            height = screenHeight.value.toInt(),
                            width = screenWidth.value.toInt()
                        ) {
                            mainViewModel.selectSecondBitmap(uri = it)
                        }
                    }
                }
            }
        } else {
            Button(onClick = { mainViewModel.showColorPicker.value = true }) {
                Text(text = stringResource(R.string.pick_color))
            }
        }
    }

    // 悬浮展开：完整模式选择
    FloatingSheet(
        visible = showAdvanced.value,
        title = stringResource(R.string.second_page),
        onDismissRequest = { showAdvanced.value = false }
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = stringResource(R.string.second_mode), style = MaterialTheme.typography.titleSmall)

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = mode == Constant.StandalonePic,
                    onCheckedChange = {
                        mainViewModel.wallpaperSettings.value.secondBitmapGradientState.value = Constant.StandalonePic
                    }
                )
                Text(text = stringResource(R.string.picture))
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = mode == Constant.HalfGradient,
                    onCheckedChange = {
                        mainViewModel.wallpaperSettings.value.secondBitmapGradientState.value = Constant.HalfGradient
                    }
                )
                Text(text = stringResource(R.string.pic_base_gradient))
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = mode == Constant.JustGradient,
                    onCheckedChange = {
                        mainViewModel.wallpaperSettings.value.secondBitmapGradientState.value = Constant.JustGradient
                    }
                )
                Text(text = stringResource(R.string.pic_base_gradient_color))
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = mode == Constant.JustColor,
                    onCheckedChange = {
                        mainViewModel.wallpaperSettings.value.secondBitmapGradientState.value = Constant.JustColor
                    }
                )
                Text(text = stringResource(R.string.pic_base_color))
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = mode == Constant.Color,
                    onCheckedChange = {
                        mainViewModel.wallpaperSettings.value.secondBitmapGradientState.value = Constant.Color
                    }
                )
                Text(text = stringResource(R.string.custom_color))
            }
        }
    }
}
