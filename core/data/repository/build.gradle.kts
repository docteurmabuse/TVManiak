plugins {
    alias(libs.plugins.tvmaniak.kotlinMultiplatform)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.core.common)
            api(projects.core.data.local)
            api(projects.core.data.remote)
            implementation(projects.core.model)

            implementation(libs.paging.common)
            implementation(libs.paging.compose.common)
            implementation(libs.sqlDelight.paging)
            implementation(libs.sqlDelight.extensions)
            implementation(libs.kermit)
        }
    }
}
