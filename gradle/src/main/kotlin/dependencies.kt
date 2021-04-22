import org.gradle.api.Plugin
import org.gradle.api.Project

private const val kotlin_version = "1.4.32"


class Deps : Plugin<Project> {
    override fun apply(target: Project) {
    }
}

object Ext {
    const val applicationId = "com.ohyooo.qrscan"
    const val compileSdkVersion = 30
    const val buildToolsVersion = "30.0.3"
    const val minSdkVersion = 21
    const val targetSdkVersion = 30
    const val versionCode = 3
    const val versionName = "2.1"
}

object Libs {

    object Plugin {
        const val AGP = "com.android.tools.build:gradle:7.0.0-alpha14"
        const val KGP = "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version"
        const val PGP = "com.google.protobuf:protobuf-gradle-plugin:0.8.15"
    }

    object Kotlin {
        const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlin_version"
        const val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.4.3"
    }

    object Google {
        const val material = "com.google.android.material:material:1.4.0-alpha02"
        const val barcode = "com.google.mlkit:barcode-scanning:16.1.1"
    }

    object AndroidX {
        const val appcompat = "androidx.appcompat:appcompat:1.3.0-rc01"
        const val coreKtx = "androidx.core:core-ktx:1.6.0-alpha01"
        const val fragmentKtx = "androidx.fragment:fragment-ktx:1.3.2"
        const val constraintLayout = "androidx.constraintlayout:constraintlayout:2.1.0-beta01"
        const val recyclerview = "androidx.recyclerview:recyclerview:1.2.0"
        const val datastore = "androidx.datastore:datastore:1.0.0-beta01"
        const val lifecycle = "androidx.lifecycle:lifecycle-viewmodel-ktx:2.3.1"
    }

    object Camera {
        private const val camerax_version = "1.1.0-alpha02"
        const val camera2 = "androidx.camera:camera-camera2:${camerax_version}"
        const val lifecycle = "androidx.camera:camera-lifecycle:${camerax_version}"
        const val view = "androidx.camera:camera-view:1.0.0-alpha22"
    }

    object Protobuf {
        const val protobuf = "com.google.protobuf:protobuf-javalite:4.0.0-rc-2"
        const val protoc = "com.google.protobuf:protoc:4.0.0-rc-2"
        const val java = "io.grpc:protoc-gen-grpc-kotlin:1.37.0"
        const val kotlin = "io.grpc:protoc-gen-grpc-kotlin:1.0.0"
    }

    val apis = listOf(AndroidX.recyclerview)

    val implementations = listOf(
        AndroidX.appcompat,
        AndroidX.constraintLayout,
        AndroidX.coreKtx,
        AndroidX.datastore,
        AndroidX.fragmentKtx,
        AndroidX.lifecycle,
        Camera.camera2,
        Camera.lifecycle,
        Camera.view,
        Google.barcode,
        Google.material,
        Kotlin.coroutines,
        Kotlin.stdlib,
        Protobuf.protobuf,
    )
}
