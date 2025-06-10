// build.gradle.kts (:app)

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.sealnote"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.sealnote"
        minSdk = 30
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        dataBinding = true
        viewBinding = true
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Android KTX and AppCompat
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)

    // Material Components Library (for your themes.xml and XML views like NavigationView, Toolbar)
    implementation(libs.material)

    // Activity (for both View System and Compose)
    implementation(libs.androidx.activity)

    // ConstraintLayout (if you still use XML layouts with ConstraintLayout)
    implementation(libs.androidx.constraintlayout)

    // Lifecycle components (for both View System and Compose)
    implementation(libs.androidx.lifecycle.viewmodel.ktx) // For ViewModel in View System
    implementation(libs.lifecycle.livedata.ktx) // For LiveData in View System
    implementation(libs.androidx.lifecycle.viewmodel.compose) // For ViewModel in Compose
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.runtime.livedata)


    // DrawerLayout (Used in activity_main.xml)
    implementation(libs.androidx.drawerlayout)

    // Traditional Android Navigation Component (for NavHostFragment in activity_main.xml)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.navigation.runtime.ktx) // Also good to have with fragment/ui ktx

    // FOUNDATION (from Compose, explicit might not be strictly needed as Compose BOM covers it)
    implementation(libs.androidx.foundation.android)
    implementation(libs.androidx.ui.tooling.preview.android)


    // Compose BOM (Bill of Materials) - Ensures compatible Compose versions
    implementation(platform(libs.androidx.compose.bom))

    // Core Compose UI
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)

    // Compose Material Design 3 (for your Compose UI)
    implementation(libs.androidx.material3)

    // Compose Navigation (for your Compose UI)
    implementation(libs.androidx.navigation.compose)

    // Compose Material Icons
    implementation(libs.androidx.material.icons.core)
    implementation(libs.androidx.material.icons.extended)

    // Google Services (if used)
    implementation ("com.google.android.gms:play-services-oss-licenses:17.1.0")
    implementation(libs.androidx.ui.text.google.fonts)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Testing Compose
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}