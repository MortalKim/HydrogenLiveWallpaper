package com.kim.hydrogenlivewallpaper.main

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.kim.hydrogenlivewallpaper.App
import com.kim.hydrogenlivewallpaper.ColorPickCallback
import com.kim.hydrogenlivewallpaper.Constant
import com.kim.hydrogenlivewallpaper.EventMessage
import com.kim.hydrogenlivewallpaper.ThreadUtils
import com.kim.hydrogenlivewallpaper.WallpaperSettings
import com.kim.hydrogenlivewallpaper.WallpaperUtils
import com.kim.hydrogenlivewallpaper.WallpaperUtils2
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.io.ByteArrayOutputStream

/**
 * @ClassName: MainViewModel
 * @Description: MainActivity's ViewModel
 * @Author: kim
 * @Date: 7/10/23 9:00 PM
 */
class MainViewModel : ViewModel() {
    var showAbout = mutableStateOf(false)
    var showLoadingDialog = mutableStateOf(false)
    var loadingMsg = mutableStateOf("请稍后...")
    var showColorPicker = mutableStateOf(false)
    var firstBitmapOrigin = mutableStateOf<Bitmap?>(null)
    var firstBitmap = mutableStateOf<Bitmap?>(null)
    var secondBitmapOrigin = mutableStateOf<Bitmap?>(null)
    var secondBitmap = mutableStateOf<Bitmap?>(null)

    var wallpaperSettings = mutableStateOf(WallpaperSettings())

    fun init(){
        val kv = MMKV.defaultMMKV()
        wallpaperSettings.value = kv.decodeParcelable(Constant.WallpaperSettings, WallpaperSettings::class.java,
            WallpaperSettings()
        )!!

        val firstBitMapBytes = kv.decodeBytes("firstView")
        val secondBitMapBytes = kv.decodeBytes("secondView")

        var op = BitmapFactory.Options()
        op.inPreferredConfig = Bitmap.Config.ARGB_8888;
        if(firstBitMapBytes != null){
            firstBitmap.value = BitmapFactory.decodeByteArray(firstBitMapBytes , 0, firstBitMapBytes.size,op);
            firstBitmapOrigin.value = BitmapFactory.decodeByteArray(firstBitMapBytes , 0, firstBitMapBytes.size,op);
        }
        if(secondBitMapBytes != null){
            secondBitmap.value = BitmapFactory.decodeByteArray(secondBitMapBytes , 0, secondBitMapBytes.size,op);
            secondBitmapOrigin.value = BitmapFactory.decodeByteArray(secondBitMapBytes , 0, secondBitMapBytes.size,op);
        }
    }

    fun selectFirstBitmap(uri: Uri){
        val contentResolver: ContentResolver = App.instance.contentResolver
        var inputStream = contentResolver.openInputStream(uri)
        val bm = BitmapFactory.decodeStream(inputStream)
        firstBitmap.value = bm
        firstBitmapOrigin.value = bm
        if(secondBitmap.value == null){
            secondBitmap.value = bm
            secondBitmapOrigin.value = bm
        }
    }

    fun selectSecondBitmap(uri: Uri){
        val contentResolver: ContentResolver = App.instance.contentResolver
        var inputStream = contentResolver.openInputStream(uri)
        val bm = BitmapFactory.decodeStream(inputStream)
        secondBitmap.value = bm
        secondBitmapOrigin.value = bm
    }

    fun secondUseFirst(){
        secondBitmap.value = firstBitmap.value
        secondBitmapOrigin.value = firstBitmap.value
    }

    fun readBitmap(){
        firstBitmap.value = WallpaperUtils2.firstBitmap
        secondBitmap.value = WallpaperUtils2.secondBitmap
    }

    fun makeColorBitmap(color: Int, width:Int, height:Int){
        val bitmap = Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888)
        var canvas = Canvas(bitmap)
        val paint = Paint()
        paint.color = color
        val rectF = RectF(0f, 0f, width.toFloat(), height.toFloat())
        canvas.drawRect(rectF, paint)
        secondBitmap.value = bitmap
        secondBitmapOrigin.value = bitmap
    }

    fun processBitmap(){
        loadingMsg.value = "请稍后..."
        showLoadingDialog.value = true
        var first = false
        var second = false
        // todo 判空
        CoroutineScope(Dispatchers.IO).launch {
            try {
                loadingMsg.value = "正在计算图片数据..."
                WallpaperUtils.getColorsFormBitmap(firstBitmapOrigin.value!!, object : ColorPickCallback {
                    override fun getSuccess(colors: ArrayList<Int>) {
                        WallpaperUtils2.makeFirstBitmap(
                            wallpaperSettings.value.firstBitmapGradientState.value,
                            firstBitmapOrigin.value!!,
                            colors[0],
                            colors[1]
                        )

                            firstBitmap.value = WallpaperUtils2.firstBitmap
                            WallpaperUtils.created = true
                            first = true
                            if(first && second){
                                saveRes()
                            }

                    }
                })

                WallpaperUtils.getColorsFormBitmap(secondBitmapOrigin.value!!, object : ColorPickCallback {
                    override fun getSuccess(colors: ArrayList<Int>) {
                        WallpaperUtils2.makeSecondBitmap(
                            wallpaperSettings.value.secondBitmapGradientState.value,
                            secondBitmapOrigin.value!!,
                            colors[0],
                            colors[1]
                        )

                            secondBitmap.value = WallpaperUtils2.secondBitmap
                            WallpaperUtils.created = true
                            second = true
                            if(first && second){
                                saveRes()

                            }

                    }
                })
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        ThreadUtils.runOnSubThread {

        }
    }

    fun saveRes(){
        loadingMsg.value = "正在保存数据..."
        var kv = MMKV.defaultMMKV(MMKV.MULTI_PROCESS_MODE, null)

        wallpaperSettings.value.firstBitmapGradient = wallpaperSettings.value.firstBitmapGradientState.value
        wallpaperSettings.value.secondPageMode = wallpaperSettings.value.secondBitmapGradientState.value

        kv.encode(Constant.WallpaperSettings, wallpaperSettings.value)
        CoroutineScope(Dispatchers.IO).launch {
            if(firstBitmap.value != null){
                var stream = ByteArrayOutputStream()
                firstBitmap.value!!.compress(Bitmap.CompressFormat.PNG, 100, stream);
                var bitmapByte = stream.toByteArray()
                kv.encode("firstView", bitmapByte)
            }
            if(secondBitmap.value != null){
                    var stream = ByteArrayOutputStream()
                    secondBitmap.value!!.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    var bitmapByte = stream.toByteArray()
                    kv.encode("secondView", bitmapByte)
            }
            EventBus.getDefault().post(EventMessage())
            showLoadingDialog.value = false
        }
    }
}
