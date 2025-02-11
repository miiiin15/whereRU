plugins {
    id("module.android")
}

android{
    namespace = "ted.gun0912.movie.common"
}

dependencies{
    implementation(libs.gson)
    implementation(libs.play.services.location.v2101)
}