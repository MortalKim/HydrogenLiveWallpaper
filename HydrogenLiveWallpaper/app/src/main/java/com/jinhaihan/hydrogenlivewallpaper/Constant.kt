package com.jinhaihan.hydrogenlivewallpaper

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

        const val FollowFirst = 0
        const val JustGradient = 1
        const val JustColor = 2
        const val StandalonePic = 3


    }
    @IntDef(value = [FollowFirst,JustGradient,JustColor,StandalonePic])
    @Retention(AnnotationRetention.SOURCE)
    annotation class SecondPageMode
}