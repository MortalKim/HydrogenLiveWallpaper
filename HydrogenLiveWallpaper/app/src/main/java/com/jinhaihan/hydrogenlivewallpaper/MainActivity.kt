package com.jinhaihan.hydrogenlivewallpaper

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.WallpaperManager
import android.content.ComponentName
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.palette.graphics.Palette
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener
import com.jinhaihan.hydrogenlivewallpaper.ViewPager.CardPagerAdapter
import com.jinhaihan.hydrogenlivewallpaper.ViewPager.ShadowTransformer
import com.jinhaihan.hydrogenlivewallpaper.WallpaperUtils.Companion.isUserSavedColor
import com.jinhaihan.hydrogenlivewallpaper.WallpaperUtils.Companion.saveImagePath
import com.tencent.mmkv.MMKV
import com.wildma.pictureselector.PictureBean
import com.wildma.pictureselector.PictureSelector
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus
import pub.devrel.easypermissions.EasyPermissions
import razerdp.basepopup.BasePopupWindow
import java.io.ByteArrayOutputStream


class MainActivity : AppCompatActivity(),ColorPickerDialogListener {
    var cardPagerAdapter = CardPagerAdapter()

    var processThread :Thread? = null
    var create = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
        CheckPermission()
    }


    @SuppressLint("WrongConstant")
    private fun init()
    {
        //读取设置
        if(!WallpaperUtils.isSettingDone){
            WallpaperUtils.getSettings()
        }
        //setMainPageTrans_CheckBox.isChecked = WallpaperUtils.isFirstNeedProcess
        //setSecondPageGradient_CheckBox.isChecked = WallpaperUtils.isSecondPageGradient

        setWallPaper_btn.setOnClickListener {
            if(WallpaperUtils.created){
                val intent = Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER)
                intent.putExtra(
                    WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                    ComponentName(baseContext, LiveWallpaperService::class.java)
                )
                startActivity(intent)
            }
            else{
                Toast.makeText(this, "请耐心等待计算完成", Toast.LENGTH_LONG).show()
            }
        }

        setPic_btn.setOnClickListener {
            val outMetrics = DisplayMetrics()
            windowManager.defaultDisplay.getRealMetrics(outMetrics)
            val width = outMetrics.widthPixels
            val height = outMetrics.heightPixels
            PictureSelector
                .create(this@MainActivity, PictureSelector.SELECT_REQUEST_CODE)
                .selectPicture(true, width, height, width,height)
//            PictureSelector.create(this)
//                .openGallery(ofImage())
//                .selectionMode(PictureConfig.SINGLE)
//                .isPageStrategy(false)
//                .isEnableCrop(true)
//                .cropImageWideHigh(width,height)
//                .forResult(object : OnResultCallbackListener<LocalMedia?> {
//                    override fun onResult(result: List<LocalMedia?>?) {
//                        // onResult Callback
//                    }
//
//                    override fun onCancel() {
//                        // onCancel Callback
//                    }
//                })
        }

        mainPagePopup.setOnClickListener {
            val mainpop = MainPageSettingsPopup(this)
            mainpop.onDismissListener = object : BasePopupWindow.OnDismissListener() {
                override fun onDismiss() {
                    refreshViewPager()
                }
            }
            mainpop.showPopupWindow()
        }

        secondPagePopup.setOnClickListener {
            val secPop = SecondPageSettingsPopup(this)
            secPop.onDismissListener = object : BasePopupWindow.OnDismissListener() {
                override fun onDismiss() {
                    refreshViewPager()
                }
            }
            secPop.showPopupWindow()
        }


//        setColor_btn.setOnClickListener {
//            val dialog = ColorPickerDialog.newBuilder()
//                .setDialogType(ColorPickerDialog.TYPE_PRESETS)
//                .setDialogTitle(R.string.PickColorTitle)
//                .setColorShape(ColorShape.CIRCLE)
//                .setPresets(resources.getIntArray(R.array.demo_colors))
//                .setAllowPresets(true)
//                .setAllowCustom(true)
//                .setShowAlphaSlider(true)
//                .setShowColorShades(true)
//                .setColor(Color.BLACK)
//                .create()
//            dialog.setColorPickerDialogListener(this)
//            supportFragmentManager
//                .beginTransaction()
//                .add(dialog, "ColorDialog")
//                .commitAllowingStateLoss() }

        //init cardviewPager
        previewViewPager.adapter = cardPagerAdapter
        var mCardShadowTransformer =
            ShadowTransformer(
                previewViewPager,
                cardPagerAdapter
            )
        previewViewPager.setPageTransformer(false, mCardShadowTransformer)
        mCardShadowTransformer.enableScaling(true)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PictureSelector.SELECT_REQUEST_CODE) {
            if (data != null) {
                val picturePath: PictureBean =
                    data.getParcelableExtra(PictureSelector.PICTURE_RESULT)!!
                saveImagePath(picturePath)
                //EventBus.getDefault().post(EventMessage())
                refreshViewPager()
            }
        }
    }



    private fun refreshViewPager(){
        WallpaperUtils.created = false
        cardPagerAdapter.Loading()
        WallpaperUtils.getSettings()
        LiveWallpaperService.needReDarw = true
        var bm = WallpaperUtils.getSavedImage(baseContext)
        if(bm == null){
            var wallpaperManager = WallpaperManager.getInstance(applicationContext)
            // 获取当前壁纸
            var wallpaperDrawable = wallpaperManager.drawable
            bm = (wallpaperDrawable as BitmapDrawable).bitmap.copy(Bitmap.Config.ARGB_8888,true)
        }
        Log.e("aaaa", "开始取色计算" + (Thread.currentThread() == Looper.getMainLooper().getThread()))
        Palette.from(bm!!).generate(object : Palette.PaletteAsyncListener {
            override fun onGenerated(palette: Palette?) {
                outputBitmap(palette, bm)
            }
        })
    }

    fun outputBitmap(palette: Palette?, bm: Bitmap){
        processThread?.interrupt()
        Toast.makeText(this, "计算完成后生效", Toast.LENGTH_LONG).show()

        processThread = Thread(
            object : Runnable {
                override fun run() {
                    if (palette == null) {
                        return
                    }
                    //palette取色不一定取得到某些特定的颜色，这里通过取多种颜色来避免取不到颜色的情况
                    if (palette.getDarkVibrantColor(Color.TRANSPARENT) !== Color.TRANSPARENT) {
                        WallpaperUtils.makeTwoBitmap(
                            bm,
                            palette.getDarkVibrantColor(Color.TRANSPARENT),
                            palette.getVibrantColor(Color.TRANSPARENT)
                        )
                    } else if (palette.getDarkMutedColor(Color.TRANSPARENT) !== Color.TRANSPARENT) {
                        WallpaperUtils.makeTwoBitmap(
                            bm,
                            palette.getDarkMutedColor(Color.TRANSPARENT),
                            palette.getMutedColor(Color.TRANSPARENT)
                        )
                    } else {
                        WallpaperUtils.makeTwoBitmap(
                            bm,
                            palette.getLightMutedColor(Color.TRANSPARENT),
                            palette.getLightVibrantColor(Color.TRANSPARENT)
                        )
                    }
                    Log.e("aaaa", "取色计算完毕")
                    saveTwoBitmap()
                    runOnUiThread {
                        cardPagerAdapter.SetBitmaps(
                            WallpaperUtils.finalFirstBitmap,
                            WallpaperUtils.finalSecondBitmap
                        )
                        Toast.makeText(this@MainActivity, "计算完成", Toast.LENGTH_LONG).show()
                        WallpaperUtils.created = true
                    }
                }

            }
        )
        processThread!!.start()

    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //Log.e("onOptionsItemSelected", item.itemId.toString() + "  " + R.id.about.toString())
        when (item.itemId) {
            R.id.about -> {
                showAlterDialog()
            }
        }
        return false
    }

    private fun showAlterDialog() {
        val alterDiaglog: AlertDialog.Builder = AlertDialog.Builder(this@MainActivity)
        alterDiaglog.setTitle("关于") //文字
        alterDiaglog.setMessage(R.string.about_Text) //提示消息
        //积极的选择
        alterDiaglog.setPositiveButton("确认",
            DialogInterface.OnClickListener { dialog, which ->

            })

        alterDiaglog.show()
    }


    fun CheckPermission(){
        val permissions = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
        )
        val hasPermissions =
            EasyPermissions.hasPermissions(this, *permissions)
        if (hasPermissions) { //拥有权限
            refreshViewPager()
        } else { //没有权限
            EasyPermissions.requestPermissions(this, "程序运行需要存储权限和相机权限", 0, *permissions)
        }
    }

    override fun onDialogDismissed(dialogId: Int) {

    }

    override fun onColorSelected(dialogId: Int, color: Int) {
        WallpaperUtils.userSecondPageColor = color
        saveUserColor(color)
        refreshViewPager()
    }



    private fun saveSettings(){
        var kv = MMKV.defaultMMKV()
        kv.encode("isFirstNeedProcess", WallpaperUtils.isFirstNeedProcess)
        kv.encode("isSecondPageGradient", WallpaperUtils.isSecondPageGradient)
        kv.encode("isSecondPageBitmapGradient", WallpaperUtils.isSecondPageBitmapGradient)
    }

    private fun saveUserColor(color: Int){
        var kv = MMKV.defaultMMKV()
        kv.encode("UserColor", color)
        kv.encode("isUserSavedColor", true)
        isUserSavedColor = true
    }

    fun saveTwoBitmap(){
        var kv = MMKV.defaultMMKV()
        if(WallpaperUtils.finalFirstBitmap != null){
            var stream = ByteArrayOutputStream()
            WallpaperUtils.finalFirstBitmap!!.compress(Bitmap.CompressFormat.PNG, 100, stream);
            var bitmapByte = stream.toByteArray()
            kv.encode("firstView", bitmapByte)
        }
        if(WallpaperUtils.finalSecondBitmap != null){
            var stream = ByteArrayOutputStream()
            WallpaperUtils.finalSecondBitmap!!.compress(Bitmap.CompressFormat.PNG, 100, stream);
            var bitmapByte = stream.toByteArray()
            kv.encode("secondView", bitmapByte)
        }
        //kv.encode("firstView",WallpaperUtils.finalFirstBitmap)
        //kv.encode("secondView",WallpaperUtils.finalSecondBitmap)
        EventBus.getDefault().post(EventMessage())
    }
}
