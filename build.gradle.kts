// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.kapt) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.hilt.android) apply false
}

// Keep Kotlin runtime artifacts aligned with the Kotlin Gradle plugin version.
// This prevents "Module was compiled with an incompatible version of Kotlin" errors when a transitive dependency
// pulls a newer `kotlin-stdlib` than the compiler understands.
val kotlinVersion =
    extensions.getByType<org.gradle.api.artifacts.VersionCatalogsExtension>()
        .named("libs")
        .findVersion("kotlin")
        .get()
        .requiredVersion

subprojects {
    configurations.configureEach {
        // NOTE: Do not force Kotlin versions on annotation processor classpaths.
        // Some processors (e.g., Hilt/XProcessing) are built against newer Kotlin runtimes and need to
        // bring their own Kotlin stdlib; forcing them to the project's Kotlin plugin version can cause
        // NoClassDefFoundError at kapt time (e.g., missing `kotlin.coroutines.jvm.internal.SpillingKt`).
        val configName = name.lowercase()
        val isProcessorClasspath =
            configName.startsWith("kapt") ||
                configName.contains("annotationprocessor") ||
                configName.contains("ksp")

        if (!isProcessorClasspath) {
            resolutionStrategy.eachDependency {
                if (
                    requested.group == "org.jetbrains.kotlin" &&
                    requested.name in setOf(
                        "kotlin-stdlib",
                        "kotlin-stdlib-jdk7",
                        "kotlin-stdlib-jdk8",
                        "kotlin-stdlib-common",
                        "kotlin-reflect",
                    )
                ) {
                    useVersion(kotlinVersion)
                    because("Align Kotlin runtime artifacts with Kotlin Gradle plugin ($kotlinVersion)")
                }
            }
        }
    }
}
