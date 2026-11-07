package com.lgframework.tv.ui.tokens

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.kyant.backdrop.shadow.InnerShadow
import com.kyant.backdrop.shadow.Shadow

/**
 * Geometry measured from the 4K reference, expressed on the 960x540dp TV grid.
 * These are LOCKED - they came from pixel measurement, not eyeballing.
 */
object LgMetrics {
    /** Home grid is 6 columns wide at 960dp. */
    const val GRID_COLUMNS: Int = 6

    /** Focus magnification. Locked at 1.22x from the reference. */
    const val FOCUS_SCALE: Float = 1.22f

    val TileWidth: Dp = 127.dp
    val TileHeight: Dp = 74.dp

    val DockWidth: Dp = 922.dp
    val DockHeight: Dp = 114.dp

    val ScreenPadding: Dp = 40.dp
    val RowSpacing: Dp = 40.dp
    val TitleToRowSpacing: Dp = 20.dp
}

/**
 * Everything that makes a surface read as "liquid glass".
 *
 * All values are in Dp and converted with the current density inside the effect scope,
 * so the look is identical at 1080p and 4K.
 */
@Immutable
data class LgGlassStyle(
    /** Flat tint laid over the refracted backdrop. */
    val tint: Color,
    /** Backdrop saturation boost - glass should look "juicier" than the raw wallpaper. */
    val saturation: Float,
    /** Gaussian blur applied to the sampled backdrop. */
    val blurRadius: Dp,
    /** Depth of the refraction band hugging the edge. */
    val refractionHeight: Dp,
    /** How far the edge band pulls the backdrop inwards. */
    val refractionAmount: Dp,
    /**
     * Rim stroke width. NOTE: Backdrop's highlight pass doubles this into the actual
     * stroke width, so 0.5dp here yields the ~1dp rim measured in the reference.
     */
    val rimWidth: Dp,
    val rimBlur: Dp,
    /** Gradient falloff of the rim shader (shader-space, not Dp). */
    val rimFalloff: Float,
    val rimAlpha: Float,
    val depthEffect: Boolean,
    val chromaticAberration: Boolean,
)

/**
 * Glass parameter sets.
 *
 * PROVISIONAL: [LgMetrics] is measured, but blur radius, tint, rim alpha/colour and
 * refraction strength are still first-pass estimates from `k4_libdock.jpg` /
 * `k4_dockgrid.jpg`. They are deliberately isolated here so the pixel-diff harness can
 * tune them without touching component code.
 */
object LgGlassTokens {

    val Standard: LgGlassStyle = LgGlassStyle(
        tint = Color.White.copy(alpha = 0.06f),
        saturation = 1.4f,
        blurRadius = 24.dp,
        refractionHeight = 12.dp,
        refractionAmount = 24.dp,
        rimWidth = 0.5.dp,
        rimBlur = 0.dp,
        rimFalloff = 11f,
        rimAlpha = 1f,
        depthEffect = true,
        chromaticAberration = false,
    )

    /** The 922x114 dock: thicker bar, stronger edge pull. */
    val Dock: LgGlassStyle = Standard.copy(
        blurRadius = 28.dp,
        refractionHeight = 16.dp,
        refractionAmount = 30.dp,
    )

    /** 127x74 tile: small surface, gentler refraction. */
    val Tile: LgGlassStyle = Standard.copy(
        blurRadius = 18.dp,
        refractionHeight = 8.dp,
        refractionAmount = 16.dp,
    )

    /** Floating dock shadow - soft, wide, downward. */
    val DockShadow: Shadow = Shadow(
        radius = 32.dp,
        offset = DpOffset(0.dp, 12.dp),
        color = Color.Black.copy(alpha = 0.45f),
    )

    /** Focused tile lifts off the surface. */
    val TileShadow: Shadow = Shadow(
        radius = 28.dp,
        offset = DpOffset(0.dp, 10.dp),
        color = Color.Black.copy(alpha = 0.55f),
    )

    /** Subtle top-edge inner shadow that stops the glass looking like a flat white plate. */
    val TileInnerShadow: InnerShadow = InnerShadow(
        radius = 12.dp,
        offset = DpOffset(0.dp, 4.dp),
        color = Color.Black.copy(alpha = 0.20f),
    )
}
