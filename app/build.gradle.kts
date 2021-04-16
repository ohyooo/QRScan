import com.google.protobuf.gradle.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("com.google.protobuf")
    id("deps")
}

android {
    signingConfigs {
        create("debugKey") {
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
        applicationId(Ext.applicationId)
        minSdkVersion(Ext.minSdkVersion)
        targetSdkVersion(Ext.targetSdkVersion)
        versionCode(Ext.versionCode)
        versionName(Ext.versionName)
        consumerProguardFiles("consumer-rules.pro")
        signingConfig = signingConfigs.getByName("debugKey")
    }
    buildTypes {
        debug {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "consumer-rules.pro")
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
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
        compose = true
        dataBinding = true
        viewBinding = true
    }
    composeOptions {
        kotlinCompilerVersion = Libs.kotlin_version
        kotlinCompilerExtensionVersion = Libs.Compose.compose_version
    }
    lint {
        isCheckReleaseBuilds = false
    }
}

dependencies {
    Libs.implementations.forEach(::implementation)
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