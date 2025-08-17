plugins {
    alias(libs.plugins.tvmaniak.kotlinMultiplatform)
    alias(libs.plugins.tvmaniak.composeMultiplatform)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.core.common)
            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor)
            implementation(libs.ksoup)
        }
    }
}
