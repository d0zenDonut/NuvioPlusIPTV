package com.nuvio.tv.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color

@Immutable
data class NuvioSurfaceColors(
    val background: Color,
    val raised: Color,
    val card: Color,
    val default: Color,
    val variant: Color,
    val panel: Color,
    val overlay: Color,
    val field: Color,
    val menu: Color,
    val modal: Color,
    val playerOverlay: Color,
    val divider: Color
)

@Immutable
data class NuvioTextColors(
    val primary: Color,
    val secondary: Color,
    val tertiary: Color,
    val disabled: Color,
    val inverse: Color,
    val onAccent: Color,
    val onOverlay: Color,
    val metadata: Color
)

@Immutable
data class NuvioFocusColors(
    val ring: Color,
    val background: Color,
    val content: Color,
    val accent: Color,
    val scrim: Color
)

@Immutable
data class NuvioSelectionColors(
    val background: Color,
    val foreground: Color,
    val border: Color,
    val mutedBackground: Color,
    val mutedForeground: Color
)

@Immutable
data class NuvioMediaColors(
    val heroScrim: Color,
    val imageScrim: Color,
    val posterFallback: Color,
    val videoControlsScrim: Color,
    val glassPanelTop: Color,
    val glassPanelMiddle: Color,
    val glassPanelBottom: Color,
    val glow: Color
)

@Immutable
data class NuvioStatusColors(
    val rating: Color,
    val error: Color,
    val warning: Color,
    val success: Color,
    val info: Color,
    val watched: Color,
    val unwatched: Color,
    val cached: Color,
    val torrent: Color,
    val premium: Color
)

@Immutable
data class NuvioDisabledColors(
    val container: Color,
    val content: Color,
    val border: Color,
    val overlay: Color
)

@Immutable
data class NuvioSourceColors(
    val trakt: Color,
    val tmdb: Color,
    val imdb: Color,
    val mdblist: Color
)

@Immutable
data class NuvioContrastPair(
    val foreground: Color,
    val background: Color
)

/**
 * Light key colours for the liquid-glass settings shell, measured off
 * `docs/refs/frames/lg_settings_top.png` (1024x576 of a 960x540dp grid, so 1 frame px = 0.9375dp).
 *
 * They live beside [NuvioColorScheme] rather than in the settings package because the scheme
 * consumes them; the settings UI only ever reads the scheme.
 */
private object GlassLightKeys {
    /** Lavender-violet backdrop the reference settings screen sits on. */
    val background = Color(0xFFC8C3D6)
    val elevated = Color(0x26FFFFFF)
    val card = Color(0x33FFFFFF)
    val surface = Color(0x26FFFFFF)
    val surfaceVariant = Color(0x1FFFFFFF)
    val panel = Color(0x33FFFFFF)
    val field = Color(0x1FFFFFFF)
    val menu = Color(0xF2FFFFFF)
    val modal = Color(0xF2FFFFFF)
    val divider = Color(0x1F000000)

    /** Row labels are near-black: the settings screen is a light screen. */
    val textPrimary = Color(0xFF1A1A1A)
    val textSecondary = Color(0xFF6E6A78)
    val textTertiary = Color(0xFF8A8494)
    val textDisabled = Color(0xFFA9A3B4)

    /** Focus is a filled white pill, not a ring. */
    val focusRing = Color(0xFFFFFFFF)
    val focusBackground = Color(0xFFFFFFFF)
    val focusContent = Color(0xFF1A1A1A)
}

class NuvioColorScheme(
    palette: ThemeColorPalette,
    amoledMode: Boolean = false,
    amoledSurfacesMode: Boolean = false,
    /**
     * Light variant, used by the liquid-glass settings shell.
     *
     * tvOS setting screens are LIGHT: a lavender-violet backdrop with near-black labels. No
     * dark palette can express that, and the scheme's text colours are white by construction,
     * so the whole scheme can be flipped for that one subtree. It stays opt-in: nothing outside
     * the settings shell passes this flag, so the rest of the app is untouched.
     */
    lightMode: Boolean = false
) {
    private val pureBlack = NuvioPrimitives.black
    private val pureBlackSurfaces = amoledMode && amoledSurfacesMode

    val Background = when {
        lightMode -> GlassLightKeys.background
        amoledMode -> pureBlack
        else -> palette.background
    }
    val BackgroundElevated =
        if (lightMode) GlassLightKeys.elevated
        else if (pureBlackSurfaces) pureBlack
        else palette.backgroundElevated
    val BackgroundCard =
        if (lightMode) GlassLightKeys.card
        else if (pureBlackSurfaces) pureBlack
        else palette.backgroundCard
    val Surface =
        if (lightMode) GlassLightKeys.surface
        else if (pureBlackSurfaces) pureBlack
        else palette.surface
    val SurfaceVariant =
        if (lightMode) GlassLightKeys.surfaceVariant
        else if (pureBlackSurfaces) pureBlack
        else palette.surfaceVariant
    val Panel =
        if (lightMode) GlassLightKeys.panel
        else if (pureBlackSurfaces) pureBlack
        else palette.panel
    val Overlay = palette.overlay
    val Field =
        if (lightMode) GlassLightKeys.field
        else if (pureBlackSurfaces) pureBlack
        else palette.field
    val Menu =
        if (lightMode) GlassLightKeys.menu
        else if (pureBlackSurfaces) pureBlack
        else palette.menu
    val Modal =
        if (lightMode) GlassLightKeys.modal
        else if (pureBlackSurfaces) pureBlack
        else palette.modal
    val PlayerOverlay = palette.playerOverlay
    val Divider =
        if (lightMode) GlassLightKeys.divider else NuvioPrimitives.neutral750

    val Primary = NuvioPrimitives.neutral500
    val PrimaryVariant = NuvioPrimitives.neutral650
    val OnPrimary = NuvioPrimitives.white
    val Secondary = palette.secondary
    val SecondaryVariant = palette.secondaryVariant
    val OnSecondary = palette.onSecondary
    val OnSecondaryVariant = palette.onSecondaryVariant

    val TextPrimary = if (lightMode) GlassLightKeys.textPrimary else NuvioPrimitives.white
    val TextSecondary =
        if (lightMode) GlassLightKeys.textSecondary else NuvioPrimitives.neutral400
    val TextTertiary =
        if (lightMode) GlassLightKeys.textTertiary else NuvioPrimitives.neutral600
    val TextDisabled =
        if (lightMode) GlassLightKeys.textDisabled else NuvioPrimitives.neutral700
    val TextInverse =
        if (lightMode) NuvioPrimitives.white else NuvioPrimitives.neutral925

    val FocusRing = if (lightMode) GlassLightKeys.focusRing else palette.focusRing
    val FocusBackground =
        if (lightMode) GlassLightKeys.focusBackground else palette.focusBackground
    val FocusContent = if (lightMode) GlassLightKeys.focusContent else NuvioPrimitives.white
    val FocusScrim = NuvioPrimitives.black.copy(alpha = 0.32f)

    val Rating = NuvioPrimitives.rating
    val Error = NuvioPrimitives.error
    val Warning = NuvioPrimitives.warning
    val Success = NuvioPrimitives.success
    val Info = NuvioPrimitives.info
    val Watched = NuvioPrimitives.success
    val Unwatched = NuvioPrimitives.neutral600
    val Cached = NuvioPrimitives.blue300
    val Torrent = NuvioPrimitives.torrent
    val Premium = NuvioPrimitives.premium

    val Border = if (lightMode) GlassLightKeys.divider else NuvioPrimitives.neutral750
    val BorderFocused = FocusRing
    val BorderMuted = if (lightMode) GlassLightKeys.divider else
        NuvioPrimitives.neutral750.copy(alpha = 0.58f)

    val Scrim = NuvioPrimitives.black.copy(alpha = 0.62f)
    val ImageScrim = NuvioPrimitives.black.copy(alpha = 0.58f)
    val VideoControlsScrim = NuvioPrimitives.black.copy(alpha = 0.72f)
    val PosterFallback = BackgroundCard

    val DisabledContainer = SurfaceVariant.copy(alpha = 0.42f)
    val DisabledContent = TextDisabled
    val DisabledBorder = Border.copy(alpha = 0.48f)
    val DisabledOverlay = NuvioPrimitives.black.copy(alpha = 0.42f)

    val surfaces = NuvioSurfaceColors(
        background = Background,
        raised = BackgroundElevated,
        card = BackgroundCard,
        default = Surface,
        variant = SurfaceVariant,
        panel = Panel,
        overlay = Overlay,
        field = Field,
        menu = Menu,
        modal = Modal,
        playerOverlay = PlayerOverlay,
        divider = Divider
    )

    val text = NuvioTextColors(
        primary = TextPrimary,
        secondary = TextSecondary,
        tertiary = TextTertiary,
        disabled = TextDisabled,
        inverse = TextInverse,
        onAccent = OnSecondary,
        onOverlay = NuvioPrimitives.white,
        metadata = TextSecondary
    )

    val focus = NuvioFocusColors(
        ring = FocusRing,
        background = FocusBackground,
        content = FocusContent,
        accent = Secondary,
        scrim = FocusScrim
    )

    val selection = NuvioSelectionColors(
        background = Secondary,
        foreground = OnSecondary,
        border = SecondaryVariant,
        mutedBackground = FocusBackground,
        mutedForeground = TextPrimary
    )

    val media = NuvioMediaColors(
        heroScrim = Scrim,
        imageScrim = ImageScrim,
        posterFallback = PosterFallback,
        videoControlsScrim = VideoControlsScrim,
        glassPanelTop = Color(0xD64A4F59),
        glassPanelMiddle = Color(0xCC3F454F),
        glassPanelBottom = Color(0xC640474F),
        glow = FocusRing.copy(alpha = 0.32f)
    )

    val status = NuvioStatusColors(
        rating = Rating,
        error = Error,
        warning = Warning,
        success = Success,
        info = Info,
        watched = Watched,
        unwatched = Unwatched,
        cached = Cached,
        torrent = Torrent,
        premium = Premium
    )

    val disabled = NuvioDisabledColors(
        container = DisabledContainer,
        content = DisabledContent,
        border = DisabledBorder,
        overlay = DisabledOverlay
    )

    val source = NuvioSourceColors(
        trakt = NuvioPrimitives.trakt,
        tmdb = NuvioPrimitives.tmdb,
        imdb = NuvioPrimitives.imdb,
        mdblist = NuvioPrimitives.mdblist
    )

    val contrastPairs = listOf(
        NuvioContrastPair(TextPrimary, Background),
        NuvioContrastPair(TextPrimary, BackgroundCard),
        NuvioContrastPair(TextSecondary, Background),
        NuvioContrastPair(OnSecondary, Secondary),
        NuvioContrastPair(FocusContent, FocusBackground),
        NuvioContrastPair(NuvioPrimitives.white, PlayerOverlay)
    )
}

object NuvioColors {
    val Background: Color
        @Composable
        @ReadOnlyComposable
        get() = NuvioTheme.colors.Background

    val BackgroundElevated: Color
        @Composable
        @ReadOnlyComposable
        get() = NuvioTheme.colors.BackgroundElevated

    val BackgroundCard: Color
        @Composable
        @ReadOnlyComposable
        get() = NuvioTheme.colors.BackgroundCard

    val Surface: Color
        @Composable
        @ReadOnlyComposable
        get() = NuvioTheme.colors.Surface

    val SurfaceVariant: Color
        @Composable
        @ReadOnlyComposable
        get() = NuvioTheme.colors.SurfaceVariant

    val Primary = NuvioPrimitives.neutral500
    val PrimaryVariant = NuvioPrimitives.neutral650
    val OnPrimary = NuvioPrimitives.white
    val TextPrimary = NuvioPrimitives.white
    val TextSecondary = NuvioPrimitives.neutral400
    val TextTertiary = NuvioPrimitives.neutral600
    val TextDisabled = NuvioPrimitives.neutral700
    val Rating = NuvioPrimitives.rating
    val Error = NuvioPrimitives.error
    val Success = NuvioPrimitives.success
    val Warning = NuvioPrimitives.warning
    val Info = NuvioPrimitives.info
    val Border = NuvioPrimitives.neutral750

    val Secondary: Color
        @Composable
        @ReadOnlyComposable
        get() = NuvioTheme.colors.Secondary

    val SecondaryVariant: Color
        @Composable
        @ReadOnlyComposable
        get() = NuvioTheme.colors.SecondaryVariant

    val OnSecondary: Color
        @Composable
        @ReadOnlyComposable
        get() = NuvioTheme.colors.OnSecondary

    val OnSecondaryVariant: Color
        @Composable
        @ReadOnlyComposable
        get() = NuvioTheme.colors.OnSecondaryVariant

    val FocusRing: Color
        @Composable
        @ReadOnlyComposable
        get() = NuvioTheme.colors.FocusRing

    val FocusBackground: Color
        @Composable
        @ReadOnlyComposable
        get() = NuvioTheme.colors.FocusBackground

    val BorderFocused: Color
        @Composable
        @ReadOnlyComposable
        get() = NuvioTheme.colors.BorderFocused
}
