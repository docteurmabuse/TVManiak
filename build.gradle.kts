plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.kotlin.parcelize) apply false
    alias(libs.plugins.kotlinSerialization) apply false
    alias(libs.plugins.buildConfig) apply false
    alias(libs.plugins.composeHotReload) apply false
    alias(libs.plugins.sqldelight) apply false
    alias(libs.plugins.modulegraph)
    alias(libs.plugins.ktLint)
    alias(libs.plugins.detekt)
}

subprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
    ktlint {
        debug.set(true)
        verbose.set(true)
        android.set(false)
        outputToConsole.set(true)
        outputColorName.set("RED")
        filter {
            enableExperimentalRules.set(true)
            exclude {
                projectDir
                    .toURI()
                    .relativize(it.file.toURI())
                    .path
                    .contains("/generated/")
            }
            include("**/kotlin/**")
        }
    }

    apply(plugin = "io.gitlab.arturbosch.detekt")
    detekt {
        parallel = true
        val files: ConfigurableFileCollection = project.files()
        files.from("${project.rootDir}/config/detekt/detekt.yml")
    }
}
