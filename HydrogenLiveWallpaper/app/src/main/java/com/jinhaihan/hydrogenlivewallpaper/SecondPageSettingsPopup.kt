package com.jinhaihan.hydrogenlivewallpaper

import android.graphics.Color
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.RadioButton
import android.widget.TextView
import com.jaredrummler.android.colorpicker.ColorPickerDialog
import com.jaredrummler.android.colorpicker.ColorShape
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.listener.OnResultCallbackListener
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

        var setSecondPageEqualMain_CheckBox = view.findViewById<RadioButton>(R.id.setSecondPageEqualMain_CheckBox)
        var setSecondPageGradient_CheckBox = view.findViewById<RadioButton>(R.id.setSecondPageGradient_CheckBox)
        var setUserColor_CheckBox = view.findViewById<RadioButton>(R.id.setUserColor_CheckBox)
        var StandAlone_CheckBox = view.findViewById<RadioButton>(R.id.StandAlone_CheckBox)
        var setColor_btn = view.findViewById<Button>(R.id.setColor_btn)
        var setPic_btn = view.findViewById<Button>(R.id.setPic_btn)
        var picGradient = view.findViewById<CheckBox>(R.id.pic_gradient)


        //读取设置
        if(!WallpaperUtils.isSettingDone){
            WallpaperUtils.getSettings()
        }

        when(WallpaperUtils.secondPageMode){
            Constant.FollowFirst->{setSecondPageEqualMain_CheckBox.isChecked = true}
            Constant.JustGradient->{setSecondPageGradient_CheckBox.isChecked = true}
            Constant.JustColor->{setUserColor_CheckBox.isChecked = true}
            Constant.StandalonePic->{StandAlone_CheckBox.isChecked = true}
        }

        setSecondPageEqualMain_CheckBox.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked)
                kv.encode(Constant.SecondPageMode, Constant.FollowFirst)
        }


        setSecondPageGradient_CheckBox.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked) kv.encode(Constant.SecondPageMode, Constant.JustGradient)
        }

        setUserColor_CheckBox.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked) kv.encode(Constant.SecondPageMode, Constant.JustColor)
        }

        StandAlone_CheckBox.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked )kv.encode(Constant.SecondPageMode, Constant.StandalonePic)
        }

        picGradient.isChecked = kv.decodeBool(Constant.SecPicGradient, true)
        picGradient.setOnCheckedChangeListener { _, isChecked ->
            kv.encode(Constant.SecPicGradient, isChecked)
        }

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

        setPic_btn.setOnClickListener {
            PictureSelector.create(context)
                .openGallery(PictureMimeType.ofImage())
                .selectionMode(PictureConfig.SINGLE)
                .imageEngine(GlideEngine.createGlideEngine())
                .isPageStrategy(false)
                .isEnableCrop(true)
                .cropImageWideHigh(width,height)
                .forResult(object : OnResultCallbackListener<LocalMedia?> {
                    override fun onResult(result: List<LocalMedia?>?) {
                        result?.get(0)?.let { it1 -> WallpaperUtils.saveSecondImagePath(it1) }
                    }

                    override fun onCancel() {
                        // onCancel Callback
                    }
                })
        }

        val okButton = view.findViewById<TextView>(R.id.ok)
        okButton.setOnClickListener {
            dismiss()
        }
        return view
    }
}