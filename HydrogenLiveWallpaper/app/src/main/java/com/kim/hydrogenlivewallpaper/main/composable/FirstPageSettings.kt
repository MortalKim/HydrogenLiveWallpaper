package com.kim.hydrogenlivewallpaper.main.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.kim.hydrogenlivewallpaper.main.ImageSelectorAndCropper
import com.kim.hydrogenlivewallpaper.main.MainViewModel

/**
 * @ClassName: FirstPageSettings
 * @Description: java类作用描述
 * @Author: kim
 * @Date: 7/27/23 10:27 PM
 */
@Composable
fun FirstPageSettings(mainViewModel: MainViewModel){
    Row (modifier = Modifier.fillMaxSize()){
        Column (modifier = Modifier){
            Text(text = "主页")
            Row (verticalAlignment = Alignment.CenterVertically){
                Checkbox(checked = mainViewModel.wallpaperSettings.value.firstBitmapGradientState.value, onCheckedChange = {
                    mainViewModel.wallpaperSettings.value.firstBitmapGradientState.value = it
                })
                Text(text = "基于图片的半屏渐变")
            }
        }
        val configuration = LocalConfiguration.current
        val screenHeight = configuration.screenHeightDp.dp
        val screenWidth = configuration.screenWidthDp.dp
        ImageSelectorAndCropper(text = "选择\n图片", modifier = Modifier.padding(10.dp).weight(1f).fillMaxHeight(), screenHeight.value.toInt(), screenWidth.value.toInt()){
            mainViewModel.selectFirstBitmap(uri = it)
        }
    }
}
