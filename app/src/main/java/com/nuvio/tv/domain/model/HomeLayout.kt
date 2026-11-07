package com.nuvio.tv.domain.model

enum class HomeLayout(val displayName: String) {
    CLASSIC("Classic View"),
    GRID("Grid View"),
    MODERN("Modern View"),

    /**
     * tvOS-27 liquid-glass launcher (LGFramework). Renders real catalog rows inside
     * Backdrop glass surfaces over a live hero wallpaper.
     */
    GLASS("Glass View")
}
