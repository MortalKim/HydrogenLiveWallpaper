package com.kim.hydrogenlivewallpaper.main.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
 * 第一页设置：保持原有逻辑（选择主图 + 是否叠加渐变），但避免请求无限高度。
 */
@Composable
fun FirstPageSettings(mainViewModel: MainViewModel) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = mainViewModel.wallpaperSettings.value.firstBitmapGradientState.value,
                        onCheckedChange = { mainViewModel.wallpaperSettings.value.firstBitmapGradientState.value = it }
                    )
                    Text(text = stringResource(R.string.pic_base_gradient))
                }
            }

            val configuration = LocalConfiguration.current
            val screenHeight = configuration.screenHeightDp.dp
            val screenWidth = configuration.screenWidthDp.dp
            ImageSelectorAndCropper(
                text = stringResource(R.string.pick_pic),
                modifier = Modifier.padding(start = 12.dp),
                height = screenHeight.value.toInt(),
                width = screenWidth.value.toInt(),
            ) {
                mainViewModel.selectFirstBitmap(uri = it)
            }
        }
    }
}
