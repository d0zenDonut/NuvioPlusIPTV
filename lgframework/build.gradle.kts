// LGFramework - tvOS-27 liquid-glass UI engine.
//
// This module is presentation-only: it owns shapes, glass tokens, the glass surface
// modifier and the TV surface primitives (tile / dock / row / glyph). It deliberately
// knows nothing about catalogs, playback or networking.
//
// Plugin IDs carry no version on purpose: the root build declares them with
// `apply false`, exactly like :vpn-android and :ffmpeg-decoder-downmix, so they resolve
// from the build classpath instead of being requested a second time.
plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.lgframework.tv"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))

    // Backdrop + shapes appear in this module's public API (Backdrop params, Shape
    // return types), so consumers get them transitively.
    api(libs.kyant.backdrop)
    api(libs.kyant.shapes)

    api("androidx.compose.ui:ui")
    api("androidx.compose.ui:ui-graphics")
    api("androidx.compose.foundation:foundation")
    implementation("androidx.compose.animation:animation-core")
    implementation("androidx.compose.material3:material3")
}

