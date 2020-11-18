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
import com.tencent.mmkv.MMKV
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.lang.Exception


class LiveWallpaperService : WallpaperService() {
    var Tag = "WallpaperService"
    companion object{
        var needReDarw = false
    }
    var mEngine : MyEngine? = null
    var firstBitMap:Bitmap?=null
    var secondBitMap:Bitmap?=null

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
            readBitMap()
            super.onCreate(surfaceHolder)
            surfaceHolder!!.setFormat(android.graphics.PixelFormat.RGBA_8888);
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

            //created = false
            //ReadAndDarwFirst(holder!!)
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
            if(WallpaperUtils.created && created){
                //如果大于步长则直接显示背景
//                if(needReDarw){
//                    needReDarw = false
//                    created = false
//                    ReadAndDarwFirst(mEngine!!.surfaceHolder!!)
//                    return
//                }
                if(xOffset <= xOffsetStep && xOffsetStep <= 1){

                    Log.e("aaa","0")
                    var mCanvas = surfaceHolder!!.lockCanvas()
                    mCanvas?.drawBitmap(secondBitMap!!,0f,0f,paint)
                    var newPaint = Paint()
                    newPaint.style = Paint.Style.FILL
                    newPaint.alpha = (((xOffsetStep - xOffset) / xOffsetStep) * 255).toInt()
                    mCanvas.drawBitmap(firstBitMap!!,0f,0f,newPaint)
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
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun getEvent(event:EventMessage){
        //mEngine!!.ReadAndDarwFirst(mEngine!!.surfaceHolder!!)
        //mEngine!!.DarwNewView()
        readBitMap()
    }

    fun readBitMap(){
        var kv = MMKV.defaultMMKV()
        val firstBitMapBytes = kv.decodeBytes("firstView")
        val secondBitMapBytes = kv.decodeBytes("secondView")

        if(firstBitMapBytes != null && secondBitMapBytes != null){
            var op = BitmapFactory.Options()
            op.inPreferredConfig = Bitmap.Config.ARGB_8888;

            firstBitMap = BitmapFactory.decodeByteArray(firstBitMapBytes , 0, firstBitMapBytes.size,op);
            secondBitMap = BitmapFactory.decodeByteArray(secondBitMapBytes , 0, secondBitMapBytes.size,op);
            //firstBitMap = kv.decodeBytes("firstView", ByteArray(1))
            //secondBitMap = kv.decodeParcelable("secondView",Bitmap::class.java)
            mEngine?.created = true
        }
    }
}
