@file:Suppress("UnstableApiUsage")

import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.io.ByteArrayOutputStream
import java.nio.charset.Charset

plugins {
    alias(libs.plugins.agp) apply false
    alias(libs.plugins.kgp) apply false
    alias(libs.plugins.ks) apply false
    alias(libs.plugins.cc) apply false
}

allprojects {
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
            freeCompilerArgs.addAll(
                listOf(
                "-Xcontext-receivers", "-jvm-target=21"
                )
            )
        }
    }
}

abstract class GitVersionValueSource : ValueSource<String, ValueSourceParameters.None> {
    @get:Inject
    abstract val execOperations: ExecOperations

    override fun obtain(): String {
        val output = ByteArrayOutputStream()
        val error = ByteArrayOutputStream()
        execOperations.exec {
            commandLine("git rev-parse --short HEAD".split(" "))
            standardOutput = output
            errorOutput = error
        }

        return if (error.toByteArray().isNotEmpty()) {
            ""
        } else {
            "-" + String(output.toByteArray(), Charset.defaultCharset()).trim()
        }
    }
}

val gitVersion = providers.of(GitVersionValueSource::class.java) {}.get()
extra["gitVersion"] = gitVersion
