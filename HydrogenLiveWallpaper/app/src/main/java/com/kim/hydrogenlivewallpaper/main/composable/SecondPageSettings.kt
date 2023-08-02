package com.kim.hydrogenlivewallpaper.main.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.kim.hydrogenlivewallpaper.Constant
import com.kim.hydrogenlivewallpaper.main.ImageSelectorAndCropper
import com.kim.hydrogenlivewallpaper.main.MainViewModel

/**
 * @ClassName: FirstPageSettings
 * @Description: java类作用描述
 * @Author: kim
 * @Date: 7/27/23 10:27 PM
 */
@Composable
fun SecondPageSettings(mainViewModel: MainViewModel){
    Row (modifier = Modifier.fillMaxSize()){
        LazyColumn (){
            item {
                Text(text = "副页")

                Text(text = "-- 副页模式 -- 滑动列表 --")

                Row (verticalAlignment = Alignment.CenterVertically){
                    Checkbox(checked = mainViewModel.wallpaperSettings.value.secondBitmapGradientState.value == Constant.StandalonePic, onCheckedChange = {
                        mainViewModel.wallpaperSettings.value.secondBitmapGradientState.value = Constant.StandalonePic
                    })
                    Text(text = "图片")
                }

                Row (verticalAlignment = Alignment.CenterVertically){
                    Checkbox(checked = mainViewModel.wallpaperSettings.value.secondBitmapGradientState.value == Constant.HalfGradient, onCheckedChange = {
                        mainViewModel.wallpaperSettings.value.secondBitmapGradientState.value = Constant.HalfGradient
                    })
                    Text(text = "基于图片的半屏渐变")
                }

                Row (verticalAlignment = Alignment.CenterVertically){
                    Checkbox(checked = mainViewModel.wallpaperSettings.value.secondBitmapGradientState.value == Constant.JustGradient, onCheckedChange = {
                        mainViewModel.wallpaperSettings.value.secondBitmapGradientState.value = Constant.JustGradient
                    })
                    Text(text = "基于图片的渐变色")
                }

                Row (verticalAlignment = Alignment.CenterVertically){
                    Checkbox(checked = mainViewModel.wallpaperSettings.value.secondBitmapGradientState.value == Constant.JustColor, onCheckedChange = {
                        mainViewModel.wallpaperSettings.value.secondBitmapGradientState.value = Constant.JustColor
                    })
                    Text(text = "基于图片计算的纯色")
                }

                Row (verticalAlignment = Alignment.CenterVertically){
                    Checkbox(checked = mainViewModel.wallpaperSettings.value.secondBitmapGradientState.value == Constant.Color, onCheckedChange = {
                        mainViewModel.wallpaperSettings.value.secondBitmapGradientState.value = Constant.Color
                    })
                    Text(text = "自定义纯色")
                }
            }

        }

        Column (modifier = Modifier.weight(1f)){
            if(mainViewModel.wallpaperSettings.value.secondBitmapGradientState.value != Constant.Color){
                Button(modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
                    .weight(1f), onClick = { mainViewModel.secondUseFirst() }) {
                    Text(text = "使用主页图")
                }
                val configuration = LocalConfiguration.current
                val screenHeight = configuration.screenHeightDp.dp
                val screenWidth = configuration.screenWidthDp.dp
                ImageSelectorAndCropper(text = "选择图片", modifier = Modifier
                    .padding(10.dp)
                    .weight(1f)
                    .fillMaxHeight(), screenHeight.value.toInt(), screenWidth.value.toInt()){
                    mainViewModel.selectSecondBitmap(uri = it)
                }
            }
            else{
                Button(onClick = { mainViewModel.showColorPicker.value = true }) {
                    Text(text = "选择颜色")
                }
            }
        }
    }

}
