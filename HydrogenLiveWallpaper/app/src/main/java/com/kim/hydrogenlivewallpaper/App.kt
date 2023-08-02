package com.kim.hydrogenlivewallpaper

import android.app.Application
import com.tencent.mmkv.MMKV

/**
 *   @author JHH
 *   @date 7/14/2020
 *   @Describe:
 */
class App:Application() {
    companion object{
        lateinit var instance:App
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        MMKV.initialize(this)
    }
}
