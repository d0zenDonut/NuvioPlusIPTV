package com.lgframework.tv.ui.glass

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.colorControls
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.highlight.Highlight
import com.kyant.backdrop.highlight.HighlightStyle
import com.kyant.backdrop.shadow.InnerShadow
import com.kyant.backdrop.shadow.Shadow
import com.lgframework.tv.ui.tokens.LgGlassStyle
import com.lgframework.tv.ui.tokens.LgGlassTokens

/**
 * Turns any [Modifier] into a liquid-glass surface.
 *
 * Effect order is fixed and load-bearing: **colour -> blur -> lens**. Lens must be last
 * because it displaces the already-blurred sample; blurring after refraction would smear
 * the edge band that gives glass its thickness.
 *
 * The whole layer is clipped to [shape] by Backdrop's own layout layer, so children are
 * clipped to the squircle too.
 */
fun Modifier.lgGlass(
    backdrop: Backdrop,
    shape: Shape,
    style: LgGlassStyle = LgGlassTokens.Standard,
    tier: LgTier = LgCapabilities.tier,
    shadow: Shadow? = null,
    innerShadow: InnerShadow? = null,
): Modifier =
    if (tier == LgTier.Synthetic) {
        // No RenderEffect path: flat translucent plate with a hairline rim.
        this
            .clip(shape)
            .background(style.tint)
            .border(1.dp, Color.White.copy(alpha = 0.14f), shape)
    } else {
        drawBackdrop(
            backdrop = backdrop,
            shape = { shape },
            effects = {
                colorControls(saturation = style.saturation)
                blur(style.blurRadius.toPx())
                if (tier == LgTier.Full) {
                    lens(
                        refractionHeight = style.refractionHeight.toPx(),
                        refractionAmount = style.refractionAmount.toPx(),
                        depthEffect = style.depthEffect,
                        chromaticAberration = style.chromaticAberration,
                    )
                }
            },
            highlight = {
                Highlight(
                    width = style.rimWidth,
                    blurRadius = style.rimBlur,
                    alpha = style.rimAlpha,
                    style = HighlightStyle.Default(falloff = style.rimFalloff),
                )
            },
            shadow = shadow?.let { s -> { s } },
            innerShadow = innerShadow?.let { s -> { s } },
        )
    }

/**
 * Convenience wrapper: a [Box] that is a glass surface.
 *
 * [backdrop] must come from a `rememberLayerBackdrop()` applied to the content that
 * sits *behind* this surface - glass can only refract what someone recorded for it.
 */
@Composable
fun LgGlassSurface(
    backdrop: Backdrop,
    shape: Shape,
    modifier: Modifier = Modifier,
    style: LgGlassStyle = LgGlassTokens.Standard,
    tier: LgTier = LgCapabilities.tier,
    shadow: Shadow? = null,
    innerShadow: InnerShadow? = null,
    content: @Composable BoxScope.() -> Unit = {},
) {
    Box(
        modifier = modifier.lgGlass(
            backdrop = backdrop,
            shape = shape,
            style = style,
            tier = tier,
            shadow = shadow,
            innerShadow = innerShadow,
        ),
        content = content,
    )
}
