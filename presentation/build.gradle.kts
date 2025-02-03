plugins {
    id("module.android")
    id("kotlin-parcelize")
}
android {
    namespace = "com.miiiin15.whereru.presentation"
}
dependencies {
//    implementation(projects.common)
//    implementation(projects.component)
    implementation(projects.domain)
    implementation(projects.dataResource)

    implementation(libs.androidx.viewmodel)
}
