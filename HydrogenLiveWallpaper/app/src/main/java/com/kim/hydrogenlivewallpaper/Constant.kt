package com.kim.hydrogenlivewallpaper

import androidx.annotation.IntDef


class Constant{
    companion object{
        const val isUserSavedColor = "isUserSavedColor"
        const val UserColor = "UserColor"
        const val isFirstNeedProcess = "isFirstNeedProcess"
        const val isSecondPageGradient = "isSecondPageGradient"
        const val isSecondPageBitmapGradient = "isSecondPageBitmapGradient"

        const val FirstImagePathKey = "ImagePath1"
        const val SecondImagePathKey = "ImagePath2"

        const val SecondPageMode = "SecondPageMode"
        const val SecPicGradient = "SecPicGradient"

        const val StandalonePic = 0
        const val HalfGradient = 1
        const val JustGradient = 2
        const val JustColor = 3
        const val Color = 4


        // new keys
        const val FirstBitmapOriginalKey = "FirstBitmapOriginal"
        const val SecondBitmapOriginalKey = "SecondBitmapOriginal"
        const val WallpaperSettings = "WallpaperSettings"
    }
    @IntDef(value = [HalfGradient,JustGradient,JustColor,StandalonePic, Color])
    @Retention(AnnotationRetention.SOURCE)
    annotation class SecondPageMode
}
