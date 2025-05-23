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
        dataBinding = true // Apakah Anda benar-benar menggunakan Data Binding XML? Jika tidak, bisa di-disable.
        viewBinding = true // Apakah Anda benar-benar menggunakan View Binding XML? Jika tidak, bisa di-disable.
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get() // Ini akan mengambil versi 1.5.14 yang baru
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Dependensi yang sudah ada (View System) - pastikan Anda memang memerlukannya
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material) // Material Components untuk View System
    implementation(libs.androidx.activity)
    implementation(libs.androidx.gridlayout)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.material.v1100) // Duplikat dari libs.material?
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.material.v190) // Duplikat dari libs.material?
    implementation(libs.androidx.drawerlayout)
    implementation(libs.androidx.navigation.fragment)

    // Dependensi Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.runtime.livedata)

    // Ikon Compose
    implementation(libs.androidx.material.icons.core)
    implementation(libs.androidx.material.icons.extended)
    // HAPUS BARIS DUPLIKAT DARI SINI (sudah ada di atas)
    // implementation(libs.androidx.material.icons.extended)
    // implementation(libs.androidx.material.icons.core)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    // Testing Compose
    androidTestImplementation(platform(libs.androidx.compose.bom)) // BOM juga untuk test
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}