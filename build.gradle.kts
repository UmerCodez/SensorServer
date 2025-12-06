// This block defines repositories and dependencies required to resolve build script components (like plugins).
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        // Add the classpath dependency for the plugin
        classpath("com.yanzhenjie.andserver:plugin:2.1.12")
    }
}

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.android.library) apply false
}