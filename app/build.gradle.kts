import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.android)
}

android {
    namespace = "github.umer0586.sensorserver"
    compileSdk = 36

    defaultConfig {
        applicationId = "github.umer0586.sensorserver"
        minSdk = 23
        targetSdk = 36
        versionCode = 37
        versionName = "7.2.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    hilt {
        enableAggregatingTask = false
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    
    implementation(libs.androidx.activity.compose)
    implementation(project(":ui"))
    implementation(project(":data"))
    implementation(libs.acra.mail)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

}