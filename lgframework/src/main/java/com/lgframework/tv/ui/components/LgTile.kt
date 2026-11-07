package com.lgframework.tv.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kyant.backdrop.Backdrop
import com.lgframework.tv.ui.glass.LgCapabilities
import com.lgframework.tv.ui.glass.LgGlassSurface
import com.lgframework.tv.ui.glass.LgTier
import com.lgframework.tv.ui.tokens.LgGlassStyle
import com.lgframework.tv.ui.tokens.LgGlassTokens
import com.lgframework.tv.ui.tokens.LgMetrics
import com.lgframework.tv.ui.tokens.LgShapes

/**
 * A 127x74 glass tile that magnifies 1.22x on focus.
 *
 * The scale is applied in a graphics layer, so it is purely visual and does not reflow
 * neighbours - the focused tile is meant to overlap them, exactly as on tvOS.
 */
@Composable
fun LgTile(
    backdrop: Backdrop,
    app: LgApp,
    modifier: Modifier = Modifier,
    style: LgGlassStyle = LgGlassTokens.Tile,
    tier: LgTier = LgCapabilities.tier,
    onFocusChange: (Boolean) -> Unit = {},
    showLabel: Boolean = false,
    glyph: @Composable () -> Unit = {
        LgAppGlyph(
            tint = app.tint,
            initial = app.initial,
            size = if (showLabel) 30.dp else 34.dp,
        )
    },
) {
    var focused by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (focused) LgMetrics.FOCUS_SCALE else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMediumLow,
        ),
        label = "LgTileScale",
    )

    LgGlassSurface(
        backdrop = backdrop,
        shape = LgShapes.Tile,
        style = style,
        tier = tier,
        shadow = if (focused) LgGlassTokens.TileShadow else null,
        innerShadow = LgGlassTokens.TileInnerShadow,
        modifier = modifier
            .size(LgMetrics.TileWidth, LgMetrics.TileHeight)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .onFocusChanged {
                if (focused != it.isFocused) {
                    focused = it.isFocused
                    onFocusChange(it.isFocused)
                }
            }
            .focusable(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = if (showLabel) 8.dp else 0.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            glyph()

            if (showLabel) {
                Spacer(Modifier.height(6.dp))
                Text(
                    text = app.name,
                    color = Color.White.copy(alpha = 0.92f),
                    fontSize = 10.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}
