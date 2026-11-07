package com.lgframework.tv.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kyant.backdrop.Backdrop
import com.lgframework.tv.ui.glass.LgCapabilities
import com.lgframework.tv.ui.glass.LgTier
import com.lgframework.tv.ui.glass.lgGlass
import com.lgframework.tv.ui.tokens.LgGlassTokens
import com.lgframework.tv.ui.tokens.LgMetrics
import com.lgframework.tv.ui.tokens.LgShapes

/**
 * The app dock: a 922x114dp continuous-curvature glass bar holding app icons.
 *
 * Two structural decisions, both forced by the reference and by Backdrop's behaviour:
 *
 * 1. The glass bar is drawn as a *sibling behind* the icon row, not as its parent.
 *    Backdrop clips a surface's children to its shape, so nesting the icons inside the
 *    bar would clip the 1.22x focused magnification at the bar edge.
 *
 * 2. Dock items are **bare icons on the bar**, not individual glass plates. In the
 *    reference the bar is the only glass in the dock; wrapping each icon in its own
 *    glass tile double-glazes it and reads as a row of plates. The 127x74 R=11 tile is
 *    the *home grid* tile (see [LgTile]), not the dock item.
 */
@Composable
fun LgDock(
    backdrop: Backdrop,
    apps: List<LgApp>,
    modifier: Modifier = Modifier,
    tier: LgTier = LgCapabilities.tier,
    itemSpacing: Dp = 26.dp,
    iconSize: Dp = 64.dp,
    onAppClick: (LgApp) -> Unit = {},
) {
    Box(
        modifier = modifier.size(LgMetrics.DockWidth, LgMetrics.DockHeight),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .lgGlass(
                    backdrop = backdrop,
                    shape = LgShapes.Dock,
                    style = LgGlassTokens.Dock,
                    tier = tier,
                    shadow = LgGlassTokens.DockShadow,
                )
        )

        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(itemSpacing, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            apps.forEach { app ->
                LgDockItem(
                    app = app,
                    iconSize = iconSize,
                    onClick = { onAppClick(app) },
                )
            }
        }
    }
}

/**
 * A focusable dock icon. Sits directly on the dock bar with no surface of its own.
 */
@Composable
private fun LgDockItem(
    app: LgApp,
    modifier: Modifier = Modifier,
    iconSize: Dp = 64.dp,
    onClick: () -> Unit = {},
    onFocusChange: (Boolean) -> Unit = {},
) {
    var focused by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (focused) LgMetrics.FOCUS_SCALE else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMediumLow,
        ),
        label = "LgDockItemScale",
    )

    Box(
        modifier = modifier
            .size(iconSize)
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
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        LgAppGlyph(
            tint = app.tint,
            initial = app.initial,
            size = iconSize,
        )
    }
}

