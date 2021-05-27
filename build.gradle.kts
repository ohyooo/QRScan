import java.lang.Integer.min

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath(Libs.Plugin.AGP)
        classpath(Libs.Plugin.KGP)
        classpath(Libs.Plugin.PGP)
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

abstract class UpdateTask : DefaultTask() {
    private val MAVEN_CENTRAL_URL = "https://repo1.maven.org/maven2/"
    private val GOOGLE_URL = "https://dl.google.com/dl/android/maven2/"

    private val versionRegex = "(?<=<version>)(.*\\n?)(?=</version>)".toRegex()
    private val numberDotRegex = "^(\\d+\\.)?(\\d+\\.)?(\\*|\\d+)\$".toRegex()

    @TaskAction
    fun update() {
        val stable: Boolean = false
        Libs.deps.forEach { dep ->
            val vargs = dep.split(":")

            val url = if (dep.contains("androidx") || dep.contains("google") || dep.contains("com.android.tools.build:gradle")) {
                StringBuffer(GOOGLE_URL)
            } else {
                StringBuffer(MAVEN_CENTRAL_URL)
            }

            for (i in 0 until vargs.size - 1) {
                val w = vargs[i]

                when {
                    i == 0 && w.contains(".") -> w.split(".").forEach { s ->
                        url.append(s).append("/")
                    }
                    else -> url.append(w).append("/")
                }
            }
            url.append("maven-metadata.xml")

            val xml = try {
                java.net.URL(url.toString()).readText()
            } catch (e: Exception) {
                if (url.contains(GOOGLE_URL)) {
                    java.net.URL(url.toString().replace(GOOGLE_URL, MAVEN_CENTRAL_URL))
                } else {
                    java.net.URL(url.toString().replace(MAVEN_CENTRAL_URL, GOOGLE_URL))
                }.readText()
            }

            val latestVersion = mutableListOf<String>()
            xml.split("\n").forEach { l ->
                versionRegex.find(l)?.value?.let {
                    latestVersion.add(it)
                }
            }

            if (latestVersion.isEmpty()) {
                println("latest version=$latestVersion")
                return@forEach
            }

            val lastStable = try {
                if (stable) {
                    latestVersion.last { numberDotRegex.matches(it) }
                } else {
                    latestVersion.last()
                }
            } catch (e: Exception) {
                latestVersion.last()
            }

            if (compare(lastStable, vargs.last()) != 0) {
                println("eeeeee $dep has update for $lastStable")
            }
        }
    }

    // v1 < v2 -> -1
    // v1 == v2 -> 0
    // v1 > v2 -> 1
    fun compare(v1: String, v2: String): Int {
        val v1s = v1.split(".")
        val v2s = v2.split(".")

        val size = min(v1s.size, v2s.size)

        for (i in 0 until size) {
            val v001 = v1s[i].toIntOrNull() ?: 0
            val v002 = v2s[i].toIntOrNull() ?: 0
            val result = when {
                v001 < v002 -> -1
                v001 == v002 -> 0
                v001 > v002 -> 1
                else -> 0
            }

            if (result != 0) {
                return result
            }
        }

        return when {
            v1s.size < v2s.size -> -1
            v1s.size == v2s.size -> 0
            v1s.size > v2s.size -> 1
            else -> 0
        }
    }
}

tasks.register<UpdateTask>("update")



abstract class GreetingTask : DefaultTask() {
    @TaskAction
    fun greet() {
        println("hello from GreetingTask")
    }
}

// Create a task using the task type
tasks.register<GreetingTask>("hello")
