package com.lgframework.tv.ui.glass

import android.os.Build
import androidx.compose.runtime.Immutable

/**
 * Rendering capability tier.
 *
 * Only [Full] is judged on 1:1 visual parity with the reference. Everything else must
 * degrade gracefully rather than pretend.
 */
enum class LgTier {
    /** AGSL runtime shaders + RenderEffect blur: real lens refraction. API 33+. */
    Full,

    /** RenderEffect blur only, no AGSL. API 31-32. No lens. */
    BlurOnly,

    /** No GPU effect path. API 25-30. Flat translucent fallback. */
    Synthetic,
}

@Immutable
data class LgCapability(
    val tier: LgTier,
    val sdkInt: Int,
) {
    val hasLens: Boolean get() = tier == LgTier.Full
    val hasBlur: Boolean get() = tier != LgTier.Synthetic
}

/**
 * Resolved once per process.
 *
 * The thresholds mirror Backdrop 1.0.6's own internal gating exactly - its `blur()` no-ops
 * below API 31 (S) and its `lens()` no-ops below API 33 (TIRAMISU). Asking for an effect the
 * engine will silently drop would leave a flat plate that still pays the cost of the
 * draw-backdrop layer, so the tier is derived from the same constants.
 */
val LgCapabilities: LgCapability by lazy {
    val tier = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> LgTier.Full
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> LgTier.BlurOnly
        else -> LgTier.Synthetic
    }
    LgCapability(tier = tier, sdkInt = Build.VERSION.SDK_INT)
}
