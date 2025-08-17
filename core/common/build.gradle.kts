plugins {
    alias(libs.plugins.tvmaniak.kotlinMultiplatform)
    alias(libs.plugins.tvmaniak.composeMultiplatform)
}

compose.resources {
    publicResClass = true
    packageOfResClass = "com.tizzone.tvmaniak.resources"
    generateResClass = always
}
