package com.kim.hydrogenlivewallpaper

import android.content.Context
import android.graphics.*
import android.util.Log
import androidx.palette.graphics.Palette
import com.luck.picture.lib.entity.LocalMedia
import com.tencent.mmkv.MMKV


class WallpaperUtils {
    companion object{
        var created = false
        var TAG = "WallpaperUtils"
        var finalFirstBitmap : Bitmap?=null
        var finalSecondBitmap : Bitmap?=null

        var isFirstNeedProcess : Boolean = true

        var isUserSavedColor : Boolean = false
        var userSecondPageColor : Int? = null

        @Constant.SecondPageMode
        var secondPageMode = Constant.HalfGradient

        //MainActivity和Service都需要读取设置，为防止重复读取，设立此项
        var isSettingDone = false

        fun makeTwoBitmap(context: Context,bitmap: Bitmap,darkColor: Int, color: Int){
            var bit1 = bitmap.copy(Bitmap.Config.ARGB_8888,true)
            finalFirstBitmap = makeFirstBitmap(bit1,darkColor,color)
            var bit2 = bitmap.copy(Bitmap.Config.ARGB_8888,true)
            finalSecondBitmap = makeSecondBitmap(context,bit2,darkColor,color)
        }

        fun makeFirstBitmap(bitmap: Bitmap,darkColor: Int, color: Int):Bitmap{
            return if(!isFirstNeedProcess) bitmap
            else getGradientBitMap(bitmap,darkColor, color)
        }

        fun makeSecondBitmap(context: Context,bitmap: Bitmap,darkColor: Int, color: Int):Bitmap{
            var canvas = Canvas(bitmap)
            val kv = MMKV.defaultMMKV()
            val paint = Paint()
            when(secondPageMode){
                Constant.HalfGradient -> {
                    //渲染原图渐变
                    canvas.drawBitmap(getGradientBitMap(bitmap, darkColor, color), 0f, 0f, null)

                }
                Constant.JustGradient->{
                    canvas.drawBitmap(getGradient(bitmap, darkColor, color), 0f, 0f, null)
                }
                Constant.JustColor->{
                    if(isUserSavedColor){
                        paint.color = userSecondPageColor!!

                    }
                    else{
                        paint.color = color
                    }
                    val rectF = RectF(0f, 0f, canvas.width.toFloat(), canvas.height.toFloat())
                    canvas.drawRect(rectF, paint)
                }
                Constant.StandalonePic->{
                    //读取保存的副页
                    var bit = getSecondSavedImage(context)
                    //如果没有则取主页
                    if(bit == null) bit = bitmap
                    //计算缩放
                    val xScale: Float = (bitmap.width.toFloat()  ) / bit.width
                    bit = Bitmap.createScaledBitmap(bit,(xScale*(bit.width)).toInt(),(xScale* bit.height).toInt(),true)
                    //清除canvas
                    canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
                    paint.style = Paint.Style.FILL
                    if(kv.decodeBool(Constant.SecPicGradient, true)){
                        getColorsFormBitmap(bit,object :ColorPickCallback{
                            override fun getSuccess(colors: ArrayList<Int>) {
                                canvas.drawBitmap(getGradientBitMap(bit, colors[0],
                                    colors[1]), 0f, 0f, paint)
                            }

                        })
                    }
                    else{
                        canvas.drawBitmap(bit, 0f, 0f, paint)
                    }
                }
            }
            return bitmap
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
            Log.i(TAG,"位图高度："+ mBitmap.height)
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

        fun getFirstSavedImage(context: Context):Bitmap?{
            var kv = MMKV.defaultMMKV()
            var bean = kv.decodeParcelable(Constant.FirstImagePathKey,LocalMedia::class.java)
            return if(bean != null){
                var op = BitmapFactory.Options()
                op.inPreferredConfig = Bitmap.Config.ARGB_8888
                BitmapFactory.decodeFile(bean.cutPath, op)?.copy(Bitmap.Config.ARGB_8888,true)
            } else{
                null
            }
        }

        fun getSecondSavedImage(context: Context):Bitmap?{
            var kv = MMKV.defaultMMKV()
            var bean = kv.decodeParcelable(Constant.SecondImagePathKey,LocalMedia::class.java)
            return if(bean != null){
                var op = BitmapFactory.Options()
                op.inPreferredConfig = Bitmap.Config.ARGB_8888
                BitmapFactory.decodeFile(bean.cutPath, op)?.copy(Bitmap.Config.ARGB_8888,true)
            } else{
                null
            }
        }


        fun saveFirstImagePath(path: LocalMedia){
            var kv = MMKV.defaultMMKV()
            kv.encode(Constant.FirstImagePathKey, path)
        }

        fun saveSecondImagePath(path: LocalMedia){
            var kv = MMKV.defaultMMKV()
            kv.encode(Constant.SecondImagePathKey, path)
        }

        fun getSettings(){
            val kv = MMKV.defaultMMKV()
            if(kv.decodeBool(Constant.isUserSavedColor, false)){
                isUserSavedColor = true
                userSecondPageColor = kv.decodeInt(Constant.UserColor, 0)
            }
            isFirstNeedProcess = kv.decodeBool("isFirstNeedProcess", true)
            secondPageMode = kv.decodeInt(Constant.SecondPageMode,Constant.HalfGradient)
            isSettingDone = true
        }
    }
}
