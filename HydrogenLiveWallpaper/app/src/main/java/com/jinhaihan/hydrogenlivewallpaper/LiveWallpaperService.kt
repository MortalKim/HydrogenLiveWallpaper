package com.jinhaihan.hydrogenlivewallpaper

import android.app.WallpaperManager
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.service.wallpaper.WallpaperService
import android.util.Log
import android.view.SurfaceHolder
import androidx.palette.graphics.Palette
import com.jinhaihan.hydrogenlivewallpaper.WallpaperUtils.Companion.createLinearGradientBitmap
import com.jinhaihan.hydrogenlivewallpaper.WallpaperUtils.Companion.lastBitmap
import com.jinhaihan.hydrogenlivewallpaper.WallpaperUtils.Companion.lastPaint
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class LiveWallpaperService : WallpaperService() {
    var Tag = "WallpaperService"
    companion object{
        var needReDarw = false
    }
    var mEngine : MyEngine? = null

    override fun onCreateEngine(): Engine {
        mEngine = MyEngine()
        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this)
        }
        return mEngine as MyEngine
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault() .unregister(this)
    }

    inner class MyEngine : Engine() {
        var xOffset : Float = 0f
        var xOffsetStep : Float = 0f
        var paint : Paint? = null
        var bitmap : Bitmap ? = null
        var created = false
        //var color : Color = Color.BLACK

        override fun getSurfaceHolder(): SurfaceHolder? {
            return super.getSurfaceHolder()
        }

        override fun onCreate(surfaceHolder: SurfaceHolder?) {
            super.onCreate(surfaceHolder)
        }

        override fun onDestroy() {
            super.onDestroy()
        }

        override fun onSurfaceChanged(
            holder: SurfaceHolder?,
            format: Int,
            width: Int,
            height: Int
        ) {
            super.onSurfaceChanged(holder, format, width, height)
            Log.e(Tag,"onSurfaceChanged")

        }

        override fun onSurfaceCreated(holder: SurfaceHolder?) {
            super.onSurfaceCreated(holder)
            Log.e(Tag,"onSurfaceCreated")
            created = false
            ReadAndDarwFirst(holder!!)
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder?) {
            super.onSurfaceDestroyed(holder)
            Log.e(Tag,"onSurfaceDestroyed")
        }


        override fun onOffsetsChanged(
            xOffset: Float,
            yOffset: Float,
            xOffsetStep: Float,
            yOffsetStep: Float,
            xPixelOffset: Int,
            yPixelOffset: Int
        ) {
            super.onOffsetsChanged(
                xOffset,
                yOffset,
                xOffsetStep,
                yOffsetStep,
                xPixelOffset,
                yPixelOffset
            )
            this.xOffset = xOffset
            this.xOffsetStep = xOffsetStep
            Log.e(Tag,"onOffsetsChanged")
            DarwNewView()
        }

        fun DarwNewView(){
            if(created){
                //如果大于步长则直接显示背景
                if(needReDarw){
                    needReDarw = false
                    created = false
                    ReadAndDarwFirst(mEngine!!.surfaceHolder!!)
                    return
                }
                if(xOffset <= xOffsetStep && xOffsetStep <= 1){

                    Log.e("aaa","0")
                    var mCanvas = surfaceHolder!!.lockCanvas()
                    val rectF = RectF(0f, 0f, mCanvas.width.toFloat(), mCanvas.height.toFloat())
                    mCanvas?.drawRect(rectF, paint!!)
                    var newPaint = Paint()
                    newPaint.style = Paint.Style.FILL
                    newPaint.alpha = (((xOffsetStep - xOffset) / xOffsetStep) * 255).toInt()
                    mCanvas.drawBitmap(bitmap!!,0f,0f,newPaint)
                    surfaceHolder!!.unlockCanvasAndPost(mCanvas)
                }
//                else{
//                    Log.e("aaa","1")
//                    var mCanvas = surfaceHolder!!.lockCanvas()
//                    mCanvas.drawBitmap(bitmap!!,0f,0f, lastPaint)
//                    surfaceHolder!!.unlockCanvasAndPost(mCanvas)
//                }
            }

        }

        fun ReadAndDarwFirst(holder: SurfaceHolder){
            val canvas: Canvas = holder.lockCanvas()
            WallpaperUtils.getSettings(applicationContext)
            var bm = WallpaperUtils.getSavedImage(baseContext)
            var wallpaperManager = WallpaperManager.getInstance(applicationContext)
            // 获取当前壁纸
            var wallpaperDrawable = wallpaperManager.drawable
            if(bm == null){
                // 将Drawable,转成Bitmap
                bm = (wallpaperDrawable as BitmapDrawable).bitmap
            }

            //canvas.drawBitmap(bm,0f,0f, Paint())

            Palette.from(bm!!).generate(object : Palette.PaletteAsyncListener{
                override fun onGenerated(palette: Palette?) {
                    if (palette == null) {
                        holder.unlockCanvasAndPost(canvas)
                        Log.e("Palette","palette == null")
                        return
                    }
                    //palette取色不一定取得到某些特定的颜色，这里通过取多种颜色来避免取不到颜色的情况
                    if (palette.getDarkVibrantColor(Color.TRANSPARENT) !== Color.TRANSPARENT) {
                        createLinearGradientBitmap(canvas,bm,
                            palette.getDarkVibrantColor(Color.TRANSPARENT),
                            palette.getVibrantColor(Color.TRANSPARENT)
                        )
                    } else if (palette.getDarkMutedColor(Color.TRANSPARENT) !== Color.TRANSPARENT) {
                        createLinearGradientBitmap(canvas,bm,
                            palette.getDarkMutedColor(Color.TRANSPARENT),
                            palette.getMutedColor(Color.TRANSPARENT)
                        )
                    } else {
                        createLinearGradientBitmap(canvas,bm,
                            palette.getLightMutedColor(Color.TRANSPARENT),
                            palette.getLightVibrantColor(Color.TRANSPARENT)
                        )
                    }
                    //color = palette.
                    holder.unlockCanvasAndPost(canvas)

                    paint = lastPaint
                    bitmap = lastBitmap
                    created = true
                }
            })
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun getEvent(event:EventMessage){
        //mEngine!!.ReadAndDarwFirst(mEngine!!.surfaceHolder!!)
        //mEngine!!.DarwNewView()
    }
}
