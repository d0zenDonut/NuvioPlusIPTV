package com.lgframework.tv.ui.components

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * Minimal app descriptor. Deliberately not tied to `android.content.pm` so the demo
 * screen renders without needing a package manager.
 */
@Immutable
data class LgApp(
    val id: String,
    val name: String,
    val initial: String,
    val tint: Color,
)

/** Placeholder catalogue until real launcher data is wired in. */
val LgDemoApps: List<LgApp> = listOf(
    LgApp("tv", "TV", "TV", Color(0xFF3B6CFF)),
    LgApp("movies", "Movies", "MV", Color(0xFFFF5A5F)),
    LgApp("music", "Music", "MU", Color(0xFFFA2D48)),
    LgApp("arcade", "Arcade", "AR", Color(0xFF8B5CF6)),
    LgApp("photos", "Photos", "PH", Color(0xFF34C759)),
    LgApp("podcasts", "Podcasts", "PC", Color(0xFFB45AF5)),
    LgApp("fitness", "Fitness", "FT", Color(0xFF00D4A0)),
    LgApp("settings", "Settings", "ST", Color(0xFF8E8E93)),
)
