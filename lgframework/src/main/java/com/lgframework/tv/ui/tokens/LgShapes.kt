package com.lgframework.tv.ui.tokens

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kyant.shapes.RoundedCornerStyle
import com.kyant.shapes.RoundedRectangle

/**
 * Locked continuous-curvature shape tokens.
 *
 * Do NOT use Compose's `RoundedCornerShape` for any tvOS-27 surface. Apple's corners are
 * *squircles* (continuous curvature); a circular arc of the same nominal radius reads
 * visibly boxier because curvature jumps discontinuously where the arc meets the edge.
 * Continuous curvature also extends the visual curve ~1.67x the nominal radius along
 * each edge, which is the single most recognisable part of the tvOS silhouette.
 *
 * Radii were measured from the 4K reference frames on a 960x540dp grid.
 */
object LgShapes {

    /** App dock bar. 922x114dp, R=44. */
    val Dock: RoundedRectangle = RoundedRectangle(44.dp, RoundedCornerStyle.Continuous)

    /** Home-row app tile. 127x74dp, R=11. */
    val Tile: RoundedRectangle = RoundedRectangle(11.dp, RoundedCornerStyle.Continuous)

    /** Modal / sheet / settings panel. R=28. */
    val Panel: RoundedRectangle = RoundedRectangle(28.dp, RoundedCornerStyle.Continuous)

    /** Horizontal content row / focusable strip. R=14. */
    val Row: RoundedRectangle = RoundedRectangle(14.dp, RoundedCornerStyle.Continuous)

    /**
     * Proportional app-icon shape. The radius scales with [size] so a 30dp row glyph and a
     * 64dp dock icon keep the same silhouette (~22.3% radius, matching Apple's icon grid).
     */
    fun glyph(size: Dp): RoundedRectangle =
        RoundedRectangle(size * 0.223f, RoundedCornerStyle.Continuous)

    /**
     * Fully rounded capsule. The over-large radius is coerced to `minDimension / 2`
     * by [RoundedRectangle], so this is a true capsule at any size.
     */
    val Capsule: RoundedRectangle = RoundedRectangle(1000.dp, RoundedCornerStyle.Continuous)
}
