package com.kim.hydrogenlivewallpaper.main

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions

/**
 * @ClassName: ImagePickerWithCrop
 * @Description: java类作用描述
 * @Author: kim
 * @Date: 7/10/23 10:25 PM
 */
@Composable
fun ImageSelectorAndCropper(text:String = "", modifier: Modifier = Modifier,height:Int, width:Int, imageCallback: (Uri)->Unit){
    var imageUri by remember {
        mutableStateOf<Uri?>(null)
    }
    val context = LocalContext.current
    val bitmap =  remember {
        mutableStateOf<Bitmap?>(null)
    }

    val imageCropLauncher = rememberLauncherForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            // use the cropped image
            imageUri = result.uriContent
            imageCallback(imageUri!!)
        } else {
            // an error occurred cropping
            val exception = result.error
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
        if(uri == null) return@rememberLauncherForActivityResult
        val cropOptions = CropImageContractOptions(uri, CropImageOptions())
        cropOptions.setAspectRatio(width,height)
        imageCropLauncher.launch(cropOptions)
    }

    if (imageUri != null) {
        if (Build.VERSION.SDK_INT < 28) {
            bitmap.value = MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)
        } else {
            val source = ImageDecoder.createSource(context.contentResolver, imageUri!!)
            bitmap.value = ImageDecoder.decodeBitmap(source)
        }

    }
    Button(modifier = modifier, onClick = { imagePickerLauncher.launch("image/*") }) {
        Text(text)
    }
}
