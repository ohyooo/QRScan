repositories {
    mavenCentral()
}

plugins {
    `kotlin-dsl`
}

gradlePlugin {
    plugins.register("class-loader-plugin") {
        id = "deps"
        implementationClass = "Deps"
    }
}