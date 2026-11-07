package com.lgframework.tv.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lgframework.tv.ui.tokens.LgShapes

/**
 * App icon glyph: continuous-curvature rounded square with a tinted gradient and the
 * app's initial. Stands in for real artwork.
 */
@Composable
fun LgAppGlyph(
    tint: Color,
    initial: String,
    modifier: Modifier = Modifier,
    size: Dp = 34.dp,
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(LgShapes.glyph(size))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        tint.copy(alpha = 0.95f),
                        tint.copy(alpha = 0.55f),
                    )
                )
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = initial,
            color = Color.White,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

