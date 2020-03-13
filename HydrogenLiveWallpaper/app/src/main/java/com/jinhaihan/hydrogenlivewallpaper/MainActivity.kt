package com.jinhaihan.hydrogenlivewallpaper

import android.Manifest
import android.app.AlertDialog
import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.palette.graphics.Palette
import com.jinhaihan.hydrogenlivewallpaper.ViewPager.CardPagerAdapter
import com.jinhaihan.hydrogenlivewallpaper.ViewPager.ShadowTransformer
import com.wildma.pictureselector.PictureSelector
import kotlinx.android.synthetic.main.activity_main.*
import pub.devrel.easypermissions.EasyPermissions


class MainActivity : AppCompatActivity() {
    var cardPagerAdapter = CardPagerAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        CheckPermission()
        var savedImage = WallpaperUtils.getSavedImage(this)
        //previewImageview.setImageBitmap(savedImage)

        setWallPaper_btn.setOnClickListener {
            val intent = Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER)
            intent.putExtra(
                WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                ComponentName(baseContext, LiveWallpaperService::class.java)
            )
            startActivity(intent)
        }

        setPic_btn.setOnClickListener {
            val outMetrics = DisplayMetrics()
            windowManager.defaultDisplay.getRealMetrics(outMetrics)
            val width = outMetrics.widthPixels
            val height = outMetrics.heightPixels
            PictureSelector
                .create(this@MainActivity, PictureSelector.SELECT_REQUEST_CODE)
                .selectPicture(true, width, height, width,height)
        }

        //init cardviewPager
        previewViewPager.adapter = cardPagerAdapter
        var mCardShadowTransformer =
            ShadowTransformer(
                previewViewPager,
                cardPagerAdapter
            )
        previewViewPager.setPageTransformer(false, mCardShadowTransformer)
        mCardShadowTransformer.enableScaling(true)

        refreshViewPager()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PictureSelector.SELECT_REQUEST_CODE) {
            if (data != null) {
                val picturePath: String =
                    data.getStringExtra(PictureSelector.PICTURE_PATH)!!
                saveImagePath(picturePath)
                //EventBus.getDefault().post(EventMessage())
                LiveWallpaperService.needReDarw = true
                refreshViewPager()
            }
        }
    }

    fun saveImagePath(path:String){
        val sharedPreferences =
            getSharedPreferences("data", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("ImagePath", path)
        //步骤4：提交
        editor.apply()
    }

    private fun refreshViewPager(){
        cardPagerAdapter.Loading()
        var bm = WallpaperUtils.getSavedImage(baseContext)
        if(bm == null){
            var wallpaperManager = WallpaperManager.getInstance(applicationContext)
            // 获取当前壁纸
            var wallpaperDrawable = wallpaperManager.drawable
            bm = (wallpaperDrawable as BitmapDrawable).bitmap
        }
        Palette.from(bm!!).generate(object : Palette.PaletteAsyncListener{
            override fun onGenerated(palette: Palette?) {
                if (palette == null) {
                    return
                }
                //palette取色不一定取得到某些特定的颜色，这里通过取多种颜色来避免取不到颜色的情况
                if (palette.getDarkVibrantColor(Color.TRANSPARENT) !== Color.TRANSPARENT) {
                    WallpaperUtils.createPreviewBitmaps(
                        bm,
                        palette.getDarkVibrantColor(Color.TRANSPARENT),
                        palette.getVibrantColor(Color.TRANSPARENT)
                    )
                } else if (palette.getDarkMutedColor(Color.TRANSPARENT) !== Color.TRANSPARENT) {
                    WallpaperUtils.createPreviewBitmaps(
                        bm,
                        palette.getDarkMutedColor(Color.TRANSPARENT),
                        palette.getMutedColor(Color.TRANSPARENT)
                    )
                } else {
                    WallpaperUtils.createPreviewBitmaps(
                        bm,
                        palette.getLightMutedColor(Color.TRANSPARENT),
                        palette.getLightVibrantColor(Color.TRANSPARENT)
                    )
                }
                cardPagerAdapter.SetBitmaps(WallpaperUtils.finalFirstBitmap,WallpaperUtils.finalSecondBitmap)
            }
        })
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

        } else { //没有权限
            EasyPermissions.requestPermissions(this, "程序运行需要存储权限和相机权限", 0, *permissions)
        }
    }
}
