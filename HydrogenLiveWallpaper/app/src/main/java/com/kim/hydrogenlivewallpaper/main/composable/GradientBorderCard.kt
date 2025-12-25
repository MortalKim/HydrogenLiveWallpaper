package com.kim.hydrogenlivewallpaper.main.composable

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * 带竖向渐变描边和阴影的通用 Card 组件。
 *
 * @param modifier 传入父布局的 Modifier，例如宽高、外边距等；尺寸也由此决定（如 fillMaxWidth）。
 * @param contentAlignment 控制 Card 内部内容的对齐方式。
 * @param elevation 阴影高度，可根据需要调整。
 */
@Composable
fun GradientBorderCard(
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.Center,
    elevation: Dp = 10.dp,
    content: @Composable () -> Unit
) {
    val isDark = isSystemInDarkTheme()

    // 亮色：保持“上亮下暗”的高光感；暗色：避免纯白刺眼，边框更收敛
    val borderColors = if (!isDark) {
        listOf(Color.White, Color(0xFFE0E0E0))
    } else {
        // 顶部略亮（模拟高光），底部更深（与深色背景融合）
        listOf(Color(0xFF6C7A86), Color(0xFF2A3138))
    }

    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.elevatedCardElevation(elevation),
        border = BorderStroke(1.dp, Brush.verticalGradient(borderColors))
    ) {
        Box(contentAlignment = contentAlignment) {
            content()
        }
    }
}
