package com.kim.hydrogenlivewallpaper.main

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import com.kim.hydrogenlivewallpaper.WallpaperUtils2
import com.kim.hydrogenlivewallpaper.main.composable.TransparentSystemBars
import com.kim.hydrogenlivewallpaper.main.ui.theme.HydrogenLiveWallpaperTheme
import pub.devrel.easypermissions.EasyPermissions

/**
 * @ClassName: MainActivity
 * @Description: MainActivity
 * @Author: kim
 * @Date: 7/10/23 9:00 PM
 */
class MainActivity : ComponentActivity() {
    private lateinit var viewModel: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = MainViewModel()
        viewModel.init()
        CheckPermission()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            HydrogenLiveWallpaperTheme {
                TransparentSystemBars()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(viewModel)

                    if (viewModel.showLoadingDialog.value) {
                        BoxLoadingDialog(msg = viewModel.loadingMsg.value)
                    }
                    if (viewModel.showColorPicker.value) {
                        val configuration = androidx.compose.ui.platform.LocalConfiguration.current
                        val screenHeight = configuration.screenHeightDp
                        val screenWidth = configuration.screenWidthDp
                        ColorPicker {
                            viewModel.showColorPicker.value = false
                            viewModel.makeColorBitmap(
                                it.toArgb(),
                                screenWidth,
                                screenHeight
                            )
                        }
                    }
                    if (viewModel.showAbout.value) {
                        About {
                            viewModel.showAbout.value = false
                        }
                    }
                }
            }
        }
    }

    fun CheckPermission() {
        val permissions = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        val hasPermissions =
            EasyPermissions.hasPermissions(this, *permissions)
        if (hasPermissions) {
            WallpaperUtils2.getWallpaperSettings(this) {
                viewModel.readBitmap()
            }
        } else {
            EasyPermissions.requestPermissions(this, "程序运行需要存储权限", 0, *permissions)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    HydrogenLiveWallpaperTheme {
        MainScreen(MainViewModel())
    }
}
