plugins {
    alias(libs.plugins.tvmaniak.kotlinMultiplatform)
    alias(libs.plugins.tvmaniak.composeMultiplatform)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.core.common)
            implementation(projects.core.model)
            api(projects.core.domain)
            implementation(projects.core.designsystem)

            implementation(libs.koin.composeVM)
            implementation(libs.navigation.compose)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.navigation.compose)
            implementation(libs.coil.compose)
            implementation(libs.compose.material3.windowSize)
            implementation(libs.paging.compose.common)
        }
    }
}
