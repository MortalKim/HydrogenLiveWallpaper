package com.jinhaihan.hydrogenlivewallpaper

import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import com.tencent.mmkv.MMKV
import razerdp.basepopup.BasePopupWindow

/**
 *   @author JHH
 *   @date 7/14/2020
 *   @Describe:
 */
class MainPageSettingsPopup(val context: MainActivity) : BasePopupWindow(context) {
    override fun onCreateContentView(): View {
        val view = createPopupById(R.layout.settings_main)
        val kv = MMKV.defaultMMKV()

        //读取设置
        if(!WallpaperUtils.isSettingDone){
           WallpaperUtils.getSettings()
        }

        var setMainPageTransCheckBox = view.findViewById<CheckBox>(R.id.setMainPageTrans_CheckBox)
        setMainPageTransCheckBox.isChecked = WallpaperUtils.isFirstNeedProcess
        setMainPageTransCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->
            kv.encode(Constant.isFirstNeedProcess, isChecked)
        }

        var okButton = view.findViewById<TextView>(R.id.ok)
        okButton.setOnClickListener {
            dismiss()
        }
        return view
    }
}