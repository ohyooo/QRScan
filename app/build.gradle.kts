import com.google.protobuf.gradle.builtins
import com.ohyooo.version.Libs
import com.ohyooo.version.Ext
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.google.protobuf.gradle.generateProtoTasks
import com.google.protobuf.gradle.protobuf
import com.google.protobuf.gradle.protoc
import com.google.protobuf.gradle.*

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("kotlin-parcelize")
    id("com.google.protobuf")
}

android {
    signingConfigs {
        getByName("debug") {
            storeFile = file("..\\signkey.jks")
            storePassword = "123456"
            keyPassword = "123456"
            keyAlias = "demo"

            enableV3Signing = true
            enableV4Signing = true
        }
    }
    compileSdkVersion(Ext.compileSdkVersion)
    buildToolsVersion(Ext.buildToolsVersion)
    defaultConfig {
        minSdkVersion(Ext.minSdkVersion)
        targetSdkVersion(Ext.targetSdkVersion)
        versionCode(Ext.versionCode)
        versionName(Ext.versionName)
        consumerProguardFiles("consumer-rules.pro")
        signingConfig = signingConfigs.getByName("debug")
    }
    buildTypes {
        debug {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "consumer-rules.pro")
        }
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "consumer-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
        useIR = true
    }
    buildFeatures {
        viewBinding = true
        dataBinding = true
    }
    lint {
        isCheckReleaseBuilds = false
    }
}

dependencies {

    implementation(Libs.AndroidX.coreKtx)
    implementation(Libs.AndroidX.appcompat)
    implementation(Libs.AndroidX.constraintLayout)
    implementation(Libs.AndroidX.fragmentKtx)
    api(Libs.AndroidX.recyclerview)
    implementation(Libs.Google.material)

    implementation(Libs.Camera.camera2)
    implementation(Libs.Camera.lifecycle)
    implementation(Libs.Camera.view)

    implementation(Libs.AndroidX.lifecycle)

    implementation(Libs.Kotlin.stdlib)
    implementation(Libs.Kotlin.coroutines)

    implementation(Libs.Google.barcode)

    // Proto DataStore
    implementation(Libs.AndroidX.datastore)

    // Protobuf
    implementation(Libs.Protobuf.protobuf)
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "11"
        useIR = true
    }
}

protobuf {
    protoc {
        artifact = Libs.Protobuf.protoc
    }
    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                id("java") {
                    option("lite")
                }
            }
        }
    }
}