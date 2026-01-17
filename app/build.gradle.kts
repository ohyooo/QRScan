plugins {
    alias(libs.plugins.agp)
    alias(libs.plugins.ks)
    alias(libs.plugins.cc)
}

android {
    signingConfigs {
        getByName("debug") {
            storeFile = file("signkey.jks")
            storePassword = "123456"
            keyPassword = "123456"
            keyAlias = "demo"

            enableV3Signing = true
            enableV4Signing = true
        }
    }
    namespace = "com.ohyooo.qrscan"
	compileSdk = libs.versions.compile.sdk.get().toInt()
    defaultConfig {
        applicationId = "com.ohyooo.qrscan"
        minSdk = libs.versions.min.sdk.get().toInt()
        targetSdk = libs.versions.target.sdk.get().toInt()
        versionCode = 10
        versionName = "2.10" + rootProject.extra["gitVersion"]
        proguardFile("proguard-rules.pro")
        signingConfig = signingConfigs.getByName("debug")
    }
    buildTypes {
        debug {
            isMinifyEnabled = false
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
        }
        create("benchmark") {
            initWith(getByName("release"))
            signingConfig = signingConfigs.getByName("debug")
            matchingFallbacks += listOf("release")
            isDebuggable = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    buildFeatures {
        compose = true
        // Disable unused AGP features
        buildConfig = false
        aidl = false
        resValues = false
        shaders = false
    }
}

dependencies {
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.google.material)
    implementation(libs.google.barcode)
    implementation(libs.google.accompanist.pager)
    implementation(libs.google.accompanist.pager.indicators)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.androidx.profileinstaller)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)
    implementation(libs.compose.material.icons.extended)
    implementation(libs.compose.runtime)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.ui.tooling)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)
}
