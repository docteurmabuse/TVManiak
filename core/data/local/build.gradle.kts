plugins {
    alias(libs.plugins.tvmaniak.kotlinMultiplatform)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.buildConfig)
}

kotlin {
    sourceSets {
        androidMain.dependencies {
            implementation(libs.sqlDelight.driver.android)
        }
        commonMain.dependencies {
            implementation(projects.core.model)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.sqlDelight.extensions)
        }
        jvmMain.dependencies {
            implementation(libs.sqlDelight.driver.sqlite)
        }
        iosMain.dependencies {
            implementation(libs.sqlDelight.driver.native)
        }
    }
}

buildConfig {
    packageName = "com.tizzone.tvmaniak.core.data.local"
    useKotlinOutput { internalVisibility = true }
    buildConfigField(
        "String",
        "APP_NAME",
        "\"${rootProject.name}\"",
    )
}

sqldelight {
    databases {
        create("TvManiakDatabase") {
            packageName.set("com.tizzone.tvmaniak.core.data.local.cache")
        }
    }
}
