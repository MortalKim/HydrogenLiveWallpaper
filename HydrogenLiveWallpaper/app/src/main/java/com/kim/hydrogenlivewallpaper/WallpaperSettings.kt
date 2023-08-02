package com.kim.hydrogenlivewallpaper

import android.os.Parcelable
import androidx.compose.runtime.mutableStateOf
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

/**
 * @ClassName: WallpapgerSettings
 * @Description: java类作用描述
 * @Author: kim
 * @Date: 7/27/23 10:31 PM
 */
@Parcelize
class WallpaperSettings(
    var firstBitmapGradient: Boolean = false,
    @Constant.SecondPageMode var secondPageMode: Int = Constant.StandalonePic
) : Parcelable {
    @IgnoredOnParcel
    val firstBitmapGradientState = mutableStateOf(firstBitmapGradient)
    @IgnoredOnParcel
    val secondBitmapGradientState = mutableStateOf(secondPageMode)
}
