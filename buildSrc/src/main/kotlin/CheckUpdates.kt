import java.net.URL

fun main(args: Array<String>) {
    CheckUpdate.checkUpdates()
}

object CheckUpdate {
    private const val MAVEN_CENTRAL_URL = "https://repo.maven.apache.org/maven2/"
    private const val GOOGLE_URL = "https://dl.google.com/dl/android/maven2/"

    fun checkUpdates() {
        Libs.implementations.forEach {
            parseDependencyToUrl(it)
        }
    }

    private fun parseDependencyToUrl(deps: String) {
        val vargs = deps.split(":")

        val url = if (deps.contains("androidx") || deps.contains("google")) {
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
            URL(url.toString()).readText()
        } catch (e: Exception) {
            if (url.contains(GOOGLE_URL)) {
                URL(url.toString().replace(GOOGLE_URL, MAVEN_CENTRAL_URL)).readText()
            } else {
                URL(url.toString().replace(MAVEN_CENTRAL_URL, GOOGLE_URL)).readText()
            }
        }

        val latestVersion = latestRegex.find(xml)?.value

        if (latestVersion == null) {
            println("latest version=$latestVersion")
            return
        }

        if (latestVersion != vargs.last()) {
            println("eeeeeeeeeeeeeeeeee $deps has update for $latestVersion")
        }
        // else {
        //     println("no updates")
        // }

    }
}

// <latest>1.2.0</latest>
val latestRegex = "(?<=<latest>)(.*\\n?)(?=</latest>)".toRegex()
