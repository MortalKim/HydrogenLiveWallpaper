package com.kim.hydrogenlivewallpaper.main

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.kim.hydrogenlivewallpaper.R
import java.net.URISyntaxException


/**
 * @ClassName: ColorPicker
 * @Description: java类作用描述
 * @Author: kim
 * @Date: 7/28/23 6:56 PM
 */
@Composable
fun About (callback: ()->Unit){
    Dialog(
        onDismissRequest = {
        }
    ) {
        Surface(
            shadowElevation = 4.dp,
//            elevation = 4.dp,
            shape = RoundedCornerShape(16.dp),
        ) {
            val context = LocalContext.current
            val packageManager = context.packageManager
            val packageInfo = packageManager.getPackageInfo(context.packageName, 0)
            val versionName = packageInfo.versionName
            Column(
                modifier = Modifier.padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "关于")
                Text(text = versionName)
                Text(text = stringResource(id = R.string.about_Text))
                Row {
//                    Button(onClick = {
//                        val intentFullUrl = "intent://platformapi/startapp?saId=10000007&" +
//                                "clientVersion=3.7.0.0718&qrcode=https%3A%2F%2Fqr.alipay.com%2Ffkx075954pd27u8cqiwvg68%3F_s" +
//                                "%3Dweb-other&_t=1472443966571#Intent;" +
//                                "scheme=alipayqr;package=com.eg.android.AlipayGphone;end"
//                        try {
//                            val intent = Intent.parseUri(intentFullUrl, Intent.URI_INTENT_SCHEME)
//                            context.startActivity(intent)
//                        } catch (e: URISyntaxException) {
//                            e.printStackTrace()
//                        }
//                    }) {
//                        Text(text = stringResource(R.string.privacy_policy))
//                    }
                    Button(onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.jinhaihan.top/WordPress/%e9%80%9a%e7%94%a8%e6%b0%a2%e5%a3%81%e7%ba%b8%e9%9a%90%e7%a7%81%e5%8d%8f%e8%ae%ae/"))
                        context.startActivity(intent)
                    }) {
                        Text(text = stringResource(R.string.privacy_policy))
                    }
                    Button(modifier = Modifier.padding(10.dp, 0.dp), onClick = { callback() }) {
                        Text(text = stringResource(R.string.OK))
                    }
                }

            }
        }
    }

}
