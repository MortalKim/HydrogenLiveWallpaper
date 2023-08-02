package com.kim.hydrogenlivewallpaper

import android.annotation.SuppressLint
import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import androidx.palette.graphics.Palette
import com.tencent.mmkv.MMKV


/**
 * @ClassName: WallpaperUtils2
 * @Description: next wallpaper
 * @Author: kim
 * @Date: 7/10/23 11:35 PM
 */
object WallpaperUtils2 {
    var firstBitmap:Bitmap? = null
    var secondBitmap:Bitmap? = null

    @SuppressLint("MissingPermission")
    fun getWallpaperSettings(context: Context, callback: ()->Unit){
        val kv = MMKV.defaultMMKV()
        val firstBytes = kv.decodeBytes(Constant.FirstBitmapOriginalKey)
        val secondBytes = kv.decodeBytes(Constant.SecondBitmapOriginalKey)
        firstBitmap = if(firstBytes != null){
            BitmapFactory.decodeByteArray(firstBytes, 0, firstBytes!!.size)
        } else{
            null
        }
        secondBitmap = if(secondBytes != null){
            BitmapFactory.decodeByteArray(secondBytes, 0, secondBytes!!.size)
        } else{
            null
        }
        callback()
    }

    @SuppressLint("MissingPermission")
    fun readWallpaper(context: Context): Bitmap? {
        var wallpaperManager = WallpaperManager.getInstance(context)
        var wallpaperDrawable = wallpaperManager.drawable
        return (wallpaperDrawable as BitmapDrawable).bitmap.copy(Bitmap.Config.ARGB_8888,true)
    }

    fun makeFirstBitmap(gradient: Boolean,bgBitmap: Bitmap,darkColor: Int, color: Int){
        firstBitmap = if(gradient){
            getGradientBitMap(bgBitmap,darkColor, color)
        } else{
            bgBitmap
        }
    }

    fun makeSecondBitmap(@Constant.SecondPageMode secondPageMode: Int = Constant.StandalonePic, bgBitmap: Bitmap,darkColor: Int, color: Int){
        when(secondPageMode){
            Constant.StandalonePic ->{
                secondBitmap = bgBitmap
            }
            Constant.HalfGradient -> {
                secondBitmap = getGradientBitMap(bgBitmap,darkColor, color)
            }
            Constant.JustGradient -> {
                secondBitmap = WallpaperUtils.getGradient(bgBitmap, darkColor, color)
            }
            Constant.JustColor ->{
                var canvas = Canvas(bgBitmap)
                val paint = Paint()
                if(WallpaperUtils.isUserSavedColor){
                    paint.color = WallpaperUtils.userSecondPageColor!!
                }
                else{
                    paint.color = color
                }
                val rectF = RectF(0f, 0f, canvas.width.toFloat(), canvas.height.toFloat())
                canvas.drawRect(rectF, paint)
                secondBitmap = bgBitmap
            }
            Constant.Color -> {

            }
        }

    }

    /**
     * 创建自上而下的渐变Bitmap
     */
    fun getGradientBitMap(bgBitmap: Bitmap,darkColor: Int, color: Int):Bitmap{
        val bitmap = Bitmap.createBitmap(bgBitmap.width,bgBitmap.height,Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawBitmap(getGradient(bgBitmap,darkColor,color),0f,0f,null)
        canvas.drawBitmap(getImageToChange(bgBitmap)!!,0f,0f,null)
        return bitmap
    }

    /**
     * 创建对应颜色的渐变效果
     */
    fun getGradient(bgBitmap: Bitmap,darkColor: Int, color: Int):Bitmap{
        val bitmap = Bitmap.createBitmap(bgBitmap.width,bgBitmap.height,Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val bgColors = IntArray(2)
        bgColors[0] = darkColor
        bgColors[1] = color
        val paint = Paint()
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
        val gradient = LinearGradient(
            0f,
            0f,
            0f,
            canvas.height.toFloat(),
            bgColors[0],
            bgColors[1],
            Shader.TileMode.CLAMP
        )
        paint.shader = gradient
        paint.style = Paint.Style.FILL
        val rectF = RectF(0f, 0f, canvas.width.toFloat(), canvas.height.toFloat())
        // mCanvas.drawRoundRect(rectF,16,16,mPaint); 这个用来绘制圆角的哈
        canvas.drawRect(rectF, paint)
        return bitmap
    }

    /**
     * 图渐变算法
     */
    fun getImageToChange(mBitmap: Bitmap): Bitmap? {
        //Log.d(TAG, "with=" + mBitmap.width + "--height=" + mBitmap.height)
        val createBitmap = Bitmap.createBitmap(
            mBitmap.width,
            mBitmap.height,
            Bitmap.Config.ARGB_8888
        )
        val mWidth = mBitmap.width
        val mHeight = mBitmap.height
        Log.i(WallpaperUtils.TAG,"位图高度："+ mBitmap.height)
        for (i in 0 until mHeight) {
            for (j in 0 until mWidth) {
                var color = mBitmap.getPixel(j, i)
                val g = Color.green(color)
                val r = Color.red(color)
                val b = Color.blue(color)
                var a = Color.alpha(color)
                val index = i * 1.0f / mHeight
                if (index > 0.25f) {
                    //val temp = i - mHeight / 4.0f
                    //旧算法
                    //a = 255 - (temp / 700 * 255).toInt()
                    a = 255 - (((i * 1.0f / mHeight) - 0.3f) * 3.3 *255).toInt()
                    //Log.e("Testssss","255-((("+i.toString() + "/" + mHeight.toString() + ") - 0,25f ) * 2 * 255 = " + a.toString())
                    if(a<0) a = 0
                    else if(a>255) a = 255
                }
                color = Color.argb(a, r, g, b)
                createBitmap.setPixel(j, i, color)
            }
        }
        return createBitmap
    }

    fun getColorsFormBitmap(bitmap: Bitmap,colorPickCallback: ColorPickCallback){
        Palette.from(bitmap).generate(object : Palette.PaletteAsyncListener {
            override fun onGenerated(palette: Palette?) {
                var colorArray = ArrayList<Int>()
                if (palette == null) {
                    colorPickCallback.getSuccess(colorArray)
                    return
                }
                //palette取色不一定取得到某些特定的颜色，这里通过取多种颜色来避免取不到颜色的情况
                when {
                    palette.getDarkVibrantColor(Color.TRANSPARENT) !== Color.TRANSPARENT -> {
                        colorArray.add(palette.getDarkVibrantColor(Color.TRANSPARENT))
                        colorArray.add(palette.getVibrantColor(Color.TRANSPARENT))
                    }
                    palette.getDarkMutedColor(Color.TRANSPARENT) !== Color.TRANSPARENT -> {
                        colorArray.add(palette.getDarkMutedColor(Color.TRANSPARENT))
                        colorArray.add(palette.getMutedColor(Color.TRANSPARENT))
                    }
                    else -> {
                        colorArray.add(palette.getLightMutedColor(Color.TRANSPARENT))
                        colorArray.add(palette.getLightVibrantColor(Color.TRANSPARENT))
                    }
                }
                colorPickCallback.getSuccess(colorArray)
            }
        })
    }
}
