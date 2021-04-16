buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.0.0-alpha14")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.32")
        classpath("com.google.protobuf:protobuf-gradle-plugin:0.8.15")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://kotlin.bintray.com/kotlinx") }
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}