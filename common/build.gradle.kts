plugins {
    id("module.android")
}

android{
    namespace = "com.miiiin15.whereru.common"
}

dependencies{
    implementation(libs.gson)
    implementation(libs.play.services.location.v2101)
}