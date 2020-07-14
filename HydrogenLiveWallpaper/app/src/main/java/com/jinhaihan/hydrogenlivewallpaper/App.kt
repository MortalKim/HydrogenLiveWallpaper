package com.jinhaihan.hydrogenlivewallpaper

import android.app.Application
import com.tencent.mmkv.MMKV

/**
 *   @author JHH
 *   @date 7/14/2020
 *   @Describe:
 */
class App:Application() {
    override fun onCreate() {
        super.onCreate()
        MMKV.initialize(this)
    }
}