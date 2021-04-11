buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath(com.ohyooo.version.Libs.Plugin.AGP)
        classpath(com.ohyooo.version.Libs.Plugin.KGP)
        classpath(com.ohyooo.version.Libs.Plugin.PGP)
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}
tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}