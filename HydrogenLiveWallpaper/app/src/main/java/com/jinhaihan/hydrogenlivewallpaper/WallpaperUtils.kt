package com.jinhaihan.hydrogenlivewallpaper

import android.content.Context
import android.graphics.*
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*


class WallpaperUtils {
    companion object{
        var TAG = "WallpaperUtils"
        var lastPaint : Paint? = null
        var lastBitmap : Bitmap ? = null

        var finalFirstBitmap : Bitmap?=null
        var finalSecondBitmap : Bitmap?=null

        var isFirstNeedProcess : Boolean = true
        var isSecondPageGradient : Boolean = true

        var isUserSavedColor : Boolean = false
        var userSecondPageColor : Int? = null

        //MainActivity和Service都需要读取设置，为防止重复读取，设立此项
        var isSettingDone = false

        //创建线性渐变背景色
        fun createLinearGradientBitmap(mCanvas: Canvas,bgBitmap: Bitmap,darkColor: Int, color: Int) {
            //是否需要绘制渐变色
            if(isSecondPageGradient){
                val bgColors = IntArray(2)
                bgColors[0] = darkColor
                bgColors[1] = color
                lastPaint = Paint()
                mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
                val gradient = LinearGradient(
                    0f,
                    0f,
                    0f,
                    mCanvas.height.toFloat(),
                    bgColors[0],
                    bgColors[1],
                    Shader.TileMode.CLAMP
                )
                lastPaint!!.shader = gradient
                lastPaint!!.style = Paint.Style.FILL
            }
            else{
                lastPaint = Paint()
                if(isUserSavedColor)
                    lastPaint!!.color = userSecondPageColor!!
                else {
                    lastPaint!!.color = color
                }
            }

            val rectF = RectF(0f, 0f, mCanvas.width.toFloat(), mCanvas.height.toFloat())
            // mCanvas.drawRoundRect(rectF,16,16,mPaint); 这个用来绘制圆角的哈
            mCanvas.drawRect(rectF, lastPaint!!)
            if(isFirstNeedProcess){
                lastBitmap = getImageToChange(bgBitmap)!!
            }
            val xScale: Float = (mCanvas.width.toFloat()  ) / lastBitmap!!.width
            lastBitmap = Bitmap.createScaledBitmap(lastBitmap!!,(xScale*(lastBitmap!!.width)).toInt(),(xScale*lastBitmap!!.height).toInt(),true)
            mCanvas.drawBitmap(lastBitmap!!,0f,0f,lastPaint!!)
        }

        fun createPreviewBitmaps(bgBitmap: Bitmap, darkColor: Int, color: Int){
            finalFirstBitmap = Bitmap.createBitmap(bgBitmap.width,bgBitmap.height,Bitmap.Config.ARGB_8888)
            var mCanvas = Canvas(finalFirstBitmap!!)

            if(isSecondPageGradient){
                val bgColors = IntArray(2)
                bgColors[0] = darkColor
                bgColors[1] = color
                lastPaint = Paint()
                mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
                val gradient = LinearGradient(
                    0f,
                    0f,
                    0f,
                    bgBitmap.height.toFloat(),
                    bgColors[0],
                    bgColors[1],
                    Shader.TileMode.CLAMP
                )
                lastPaint!!.shader = gradient
            }
            else{
                lastPaint = Paint()
                if(isUserSavedColor){
                    Log.i(TAG,"userSecondPageColor：null" + color.toString())
                    lastPaint!!.color = userSecondPageColor!!
                }
                else {
                    lastPaint!!.color = color
                    Log.i(TAG, "userSecondPageColor:$color")
                }
            }

            val rectF = RectF(0f, 0f, bgBitmap.width.toFloat(), bgBitmap.height.toFloat())
            mCanvas.drawRect(rectF, lastPaint!!)
            finalSecondBitmap = finalFirstBitmap!!.copy(Bitmap.Config.ARGB_8888,true) //目前first是在canvas中处理过的渐变
            lastBitmap = if(isFirstNeedProcess){
                getImageToChange(bgBitmap)!! //检测是否需要渐变处理

            } else{
                bgBitmap
            }
            mCanvas.drawBitmap(lastBitmap!!,0f,0f,lastPaint!!) //将lastBitmap绘制到first
        }

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

        fun getSavedImage(context: Context):Bitmap?{
            var sharedPreferences = context.getSharedPreferences("data", Context .MODE_PRIVATE)
            var path = sharedPreferences.getString("ImagePath","")
            if(path!= ""){
               return BitmapFactory.decodeFile(path)
            }
            return null
        }


        fun saveImagePath(path:String, context: Context){
            val sharedPreferences =
                context.getSharedPreferences("data", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString("ImagePath", path)
            //步骤4：提交
            editor.apply()
        }

        fun getSettings(context: Context){
            var sharedPreferences = context.getSharedPreferences("data", Context .MODE_PRIVATE)
            //var path = sharedPreferences.getString("ImagePath","")
            if(sharedPreferences.getBoolean("isUserSavedColor", false)){
                isUserSavedColor = true
                userSecondPageColor = sharedPreferences.getInt("UserColor", 0)
            }
            isFirstNeedProcess = sharedPreferences.getBoolean("isFirstNeedProcess", true)
            isSecondPageGradient = sharedPreferences.getBoolean("isSecondPageGradient", true)

            isSettingDone = true
        }

    }
}