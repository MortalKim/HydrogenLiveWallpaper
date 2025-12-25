package com.kim.hydrogenlivewallpaper.main.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * 简单的“悬浮展开”容器（兼容性优先）：使用 AlertDialog 来承载折叠内容。
 *
 * 说明：项目当前依赖版本不包含 Material3 的 ModalBottomSheet API，因此这里选用 AlertDialog。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FloatingSheet(
    visible: Boolean,
    title: String,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    content: @Composable () -> Unit,
) {
    if (!visible) return

    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        title = { Text(title, style = MaterialTheme.typography.titleMedium) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(contentPadding)
            ) {
                content()
            }
        },
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text("完成")
            }
        }
    )
}
