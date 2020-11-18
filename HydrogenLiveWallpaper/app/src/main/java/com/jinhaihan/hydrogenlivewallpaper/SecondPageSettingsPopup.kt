package com.jinhaihan.hydrogenlivewallpaper

import android.graphics.Color
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import com.jaredrummler.android.colorpicker.ColorPickerDialog
import com.jaredrummler.android.colorpicker.ColorShape
import com.tencent.mmkv.MMKV
import razerdp.basepopup.BasePopupWindow

/**
 *   @author JHH
 *   @date 7/14/2020
 *   @Describe:
 */
class SecondPageSettingsPopup(val context: MainActivity) : BasePopupWindow(context) {
    override fun onCreateContentView(): View {
        val view = createPopupById(R.layout.settings_second)
        val kv = MMKV.defaultMMKV()

        //读取设置
        if(!WallpaperUtils.isSettingDone){
            WallpaperUtils.getSettings()
        }

        var setSecondPageEqualMain_CheckBox = view.findViewById<CheckBox>(R.id.setSecondPageEqualMain_CheckBox)
        setSecondPageEqualMain_CheckBox.isChecked = WallpaperUtils.isSecondPageBitmapGradient
        setSecondPageEqualMain_CheckBox.setOnCheckedChangeListener { buttonView, isChecked ->
            kv.encode(Constant.isSecondPageBitmapGradient, isChecked)
        }

        var setSecondPageGradient_CheckBox = view.findViewById<CheckBox>(R.id.setSecondPageGradient_CheckBox)
        setSecondPageGradient_CheckBox.isChecked = WallpaperUtils.isSecondPageGradient
        setSecondPageGradient_CheckBox.setOnCheckedChangeListener { buttonView, isChecked ->
            kv.encode(Constant.isSecondPageGradient, isChecked)
        }

        var setColor_btn = view.findViewById<Button>(R.id.setColor_btn)
        setColor_btn.setOnClickListener {
            val dialog = ColorPickerDialog.newBuilder()
                .setDialogType(ColorPickerDialog.TYPE_PRESETS)
                .setDialogTitle(R.string.PickColorTitle)
                .setColorShape(ColorShape.CIRCLE)
                .setPresets(context.resources.getIntArray(R.array.demo_colors))
                .setAllowPresets(true)
                .setAllowCustom(true)
                .setShowAlphaSlider(true)
                .setShowColorShades(true)
                .setColor(Color.BLACK)
                .create()
            dialog.setColorPickerDialogListener(context)
            context.supportFragmentManager
                .beginTransaction()
                .add(dialog, "ColorDialog")
                .commitAllowingStateLoss()
        }

        val okButton = view.findViewById<TextView>(R.id.ok)
        okButton.setOnClickListener {
            dismiss()
        }
        return view
    }
}