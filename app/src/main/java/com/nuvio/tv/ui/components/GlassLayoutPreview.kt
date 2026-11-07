package com.nuvio.tv.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Static preview for the tvOS-27 glass layout: hero block, a row of glass tiles and the
 * floating dock bar.
 *
 * Purely illustrative - the real screen is [com.nuvio.tv.ui.screens.home.GlassHomeContent].
 * It lives here, next to the Classic/Grid/Modern previews, so both pickers (onboarding
 * selection and Settings) can share the same drawing.
 */
@Composable
internal fun GlassLayoutPreview(
    modifier: Modifier = Modifier,
    animated: Boolean = false
) {
    // The dock breathes a little when the caller animates, the way the real dock's glass
    // reacts to the live wallpaper.
    val dockAlpha = if (animated) {
        val transition = rememberInfiniteTransition(label = "glassPreviewDock")
        val pulse by transition.animateFloat(
            initialValue = 0.20f,
            targetValue = 0.32f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1600),
                repeatMode = RepeatMode.Reverse
            ),
            label = "glassPreviewDockAlpha"
        )
        pulse
    } else {
        0.26f
    }

    Box(
        modifier = modifier
            .background(Color(0xFF0B1020), RoundedCornerShape(10.dp))
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Hero
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.55f)
                    .height(46.dp)
                    .background(Color.White.copy(alpha = 0.12f), RoundedCornerShape(14.dp))
            )
            // Glass tiles
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(4) {
                    Box(
                        modifier = Modifier
                            .width(50.dp)
                            .height(28.dp)
                            .background(
                                Color.White.copy(alpha = 0.20f),
                                RoundedCornerShape(8.dp)
                            )
                    )
                }
            }
            // Floating dock
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(150.dp)
                    .height(30.dp)
                    .background(Color.White.copy(alpha = dockAlpha), RoundedCornerShape(15.dp))
            )
        }
    }
}
