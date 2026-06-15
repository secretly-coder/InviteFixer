plugins {
    id("com.android.library")
    id("kotlin-android")
}

android {
    compileSdk = 33
    defaultConfig {
        minSdk = 21
    }
}

dependencies {
    compileOnly("com.aliucord:aliucord:1.0.0")
}