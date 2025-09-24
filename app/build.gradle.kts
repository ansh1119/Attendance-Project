plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    kotlin("plugin.serialization") version "2.0.21"
}

android {
    namespace = "com.cpbyte.attendanceapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.cpbyte.attendanceapp"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Ktor Client
    implementation("io.ktor:ktor-client-okhttp:3.1.3")
    implementation ("io.ktor:ktor-client-content-negotiation:3.1.3")
    implementation ("io.ktor:ktor-serialization-kotlinx-json:3.1.3")
    implementation("io.ktor:ktor-client-cio:3.1.3")
    implementation ("io.ktor:ktor-client-android:3.1.3") // for Android client
    implementation ("io.ktor:ktor-client-logging:3.1.3") // for logging plugin
    implementation ("io.ktor:ktor-client-core:3.1.3") // for base functionality
    implementation("io.ktor:ktor-client-auth:3.1.3")


    // Koin for Android + Compose
    implementation ("io.insert-koin:koin-androidx-compose:3.5.3")
    implementation ("io.insert-koin:koin-android:3.5.3")
    implementation("io.insert-koin:koin-androidx-compose:1.1.2")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")

    implementation("androidx.navigation:navigation-compose:2.9.4")

    implementation("com.journeyapps:zxing-android-embedded:4.3.0")


}