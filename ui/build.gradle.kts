plugins {
    id("module.android")
    id("kotlin-parcelize")
    id("androidx.navigation.safeargs")
}

android {
    namespace = "com.miiiin15.whereru.ui"
}

dependencies {
    implementation(projects.common)
    implementation(projects.dataResource)
    implementation(projects.presentation)

    implementation(libs.androidx.core)
    implementation(libs.androidx.fragment)
    implementation(libs.androidx.runtime)
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui)
    implementation(libs.androidx.constraintlayout)

    implementation(libs.glide)
    kapt(libs.glide.compiler)

    implementation("kr.co.prnd:readmore-textview:1.0.0")
}
