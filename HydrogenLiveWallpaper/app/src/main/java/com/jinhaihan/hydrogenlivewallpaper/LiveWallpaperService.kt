package com.jinhaihan.hydrogenlivewallpaper

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.service.wallpaper.WallpaperService
import android.util.DisplayMetrics
import android.util.Log
import android.view.SurfaceHolder
import com.tencent.mmkv.MMKV
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

        var firstBitMap:Bitmap?=null
        var secondBitMap:Bitmap?=null

        var xOffset : Float = 0f
        var xOffsetStep : Float = 0f
        var paint : Paint? = null
        var newPaint = Paint()

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
            newPaint.style = Paint.Style.FILL
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
            var c = holder!!.lockCanvas()
            scaleBitmap(c.height)
            holder.unlockCanvasAndPost(c)
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
            if(created){
                if(xOffsetStep in xOffset..1.0F){
                    var mCanvas = surfaceHolder!!.lockCanvas()
                    mCanvas?.drawBitmap(secondBitMap!!,0f,0f,paint)
                    newPaint.alpha = (((xOffsetStep - xOffset) / xOffsetStep) * 255).toInt()
                    mCanvas?.drawBitmap(firstBitMap!!,0f,0f,newPaint)
                    surfaceHolder!!.unlockCanvasAndPost(mCanvas)
                }
            }
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

                mEngine?.created = true
            }
        }

        fun scaleBitmap(height:Int){
            var xScale: Float = height.toFloat()/ firstBitMap!!.height
            firstBitMap = Bitmap.createScaledBitmap(firstBitMap!!,(xScale*(firstBitMap!!.width)).toInt(),(xScale* firstBitMap!!.height).toInt(), true)
            xScale = height.toFloat()/ secondBitMap!!.height
            secondBitMap = Bitmap.createScaledBitmap(secondBitMap!!,(xScale*(secondBitMap!!.width)).toInt(),(xScale* secondBitMap!!.height).toInt(), true)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun getEvent(event:EventMessage){
        //mEngine!!.ReadAndDarwFirst(mEngine!!.surfaceHolder!!)
        //mEngine!!.DarwNewView()
        mEngine?.readBitMap()
    }


}
