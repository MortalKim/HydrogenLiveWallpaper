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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kim.hydrogenlivewallpaper.Constant
import com.kim.hydrogenlivewallpaper.R
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
                Text(text = stringResource(R.string.second_page))

                Text(text = stringResource(R.string.second_mode))

                Row (verticalAlignment = Alignment.CenterVertically){
                    Checkbox(checked = mainViewModel.wallpaperSettings.value.secondBitmapGradientState.value == Constant.StandalonePic, onCheckedChange = {
                        mainViewModel.wallpaperSettings.value.secondBitmapGradientState.value = Constant.StandalonePic
                    })
                    Text(text = stringResource(R.string.picture))
                }

                Row (verticalAlignment = Alignment.CenterVertically){
                    Checkbox(checked = mainViewModel.wallpaperSettings.value.secondBitmapGradientState.value == Constant.HalfGradient, onCheckedChange = {
                        mainViewModel.wallpaperSettings.value.secondBitmapGradientState.value = Constant.HalfGradient
                    })
                    Text(text = stringResource(R.string.pic_base_gradient))
                }

                Row (verticalAlignment = Alignment.CenterVertically){
                    Checkbox(checked = mainViewModel.wallpaperSettings.value.secondBitmapGradientState.value == Constant.JustGradient, onCheckedChange = {
                        mainViewModel.wallpaperSettings.value.secondBitmapGradientState.value = Constant.JustGradient
                    })
                    Text(text = stringResource(R.string.pic_base_gradient_color))
                }

                Row (verticalAlignment = Alignment.CenterVertically){
                    Checkbox(checked = mainViewModel.wallpaperSettings.value.secondBitmapGradientState.value == Constant.JustColor, onCheckedChange = {
                        mainViewModel.wallpaperSettings.value.secondBitmapGradientState.value = Constant.JustColor
                    })
                    Text(text = stringResource(R.string.pic_base_color))
                }

                Row (verticalAlignment = Alignment.CenterVertically){
                    Checkbox(checked = mainViewModel.wallpaperSettings.value.secondBitmapGradientState.value == Constant.Color, onCheckedChange = {
                        mainViewModel.wallpaperSettings.value.secondBitmapGradientState.value = Constant.Color
                    })
                    Text(text = stringResource(R.string.custom_color))
                }
            }

        }

        Column (modifier = Modifier.weight(1f)){
            if(mainViewModel.wallpaperSettings.value.secondBitmapGradientState.value != Constant.Color){
                Button(modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
                    .weight(1f), onClick = { mainViewModel.secondUseFirst() }) {
                    Text(text = stringResource(R.string.use_main_page_pic))
                }
                val configuration = LocalConfiguration.current
                val screenHeight = configuration.screenHeightDp.dp
                val screenWidth = configuration.screenWidthDp.dp
                ImageSelectorAndCropper(text = stringResource(R.string.pick_pic_oneline), modifier = Modifier
                    .padding(10.dp)
                    .weight(1f)
                    .fillMaxHeight(), height = screenHeight.value.toInt(), width = screenWidth.value.toInt()
                ){
                    mainViewModel.selectSecondBitmap(uri = it)
                }
            }
            else{
                Button(onClick = { mainViewModel.showColorPicker.value = true }) {
                    Text(text = stringResource(R.string.pick_color))
                }
            }
        }
    }

}
