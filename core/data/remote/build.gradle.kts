plugins {
    alias(libs.plugins.tvmaniak.kotlinMultiplatform)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.buildConfig)
}

kotlin {
    sourceSets {
        androidMain.dependencies {
            implementation(libs.ktor.client.android)
        }
        commonMain.dependencies {
            implementation(projects.core.model)

            implementation(libs.kotlinx.serialization.json)
            api(libs.bundles.ktor.common)
        }
        jvmMain.dependencies {
            implementation(libs.ktor.client.java)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
    }
}

buildConfig {
    packageName = "com.tizzone.tvmaniak.core.data.remote"
    useKotlinOutput { internalVisibility = true }
    buildConfigField(
        "String",
        "APP_NAME",
        "\"${rootProject.name}\"",
    )
}
