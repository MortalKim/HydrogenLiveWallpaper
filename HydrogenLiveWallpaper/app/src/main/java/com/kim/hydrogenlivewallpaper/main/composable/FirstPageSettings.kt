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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
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
fun FirstPageSettings(mainViewModel: MainViewModel){
    Row (modifier = Modifier.fillMaxSize()){
        Column (modifier = Modifier){
            Text(text = stringResource(R.string.main_page))
            Row (verticalAlignment = Alignment.CenterVertically){
                Checkbox(checked = mainViewModel.wallpaperSettings.value.firstBitmapGradientState.value, onCheckedChange = {
                    mainViewModel.wallpaperSettings.value.firstBitmapGradientState.value = it
                })
                Text(text = stringResource(R.string.pic_base_gradient))
            }
        }
        val configuration = LocalConfiguration.current
        val screenHeight = configuration.screenHeightDp.dp
        val screenWidth = configuration.screenWidthDp.dp
        ImageSelectorAndCropper(text = stringResource(R.string.pick_pic), modifier = Modifier
            .padding(10.dp)
            .weight(1f)
            .fillMaxHeight(), height = screenHeight.value.toInt(), width = screenWidth.value.toInt()
        ){
            mainViewModel.selectFirstBitmap(uri = it)
        }
    }
}
