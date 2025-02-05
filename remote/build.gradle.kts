plugins {
    id("module.android")
}
android {
    namespace = "com.miiiin15.whereru.remote"
}

dependencies {
    implementation(projects.common)
    implementation(projects.data)

    // TODO : 벡엔드가 생긴다면 retrofit 추가

    implementation(platform("com.google.firebase:firebase-bom:32.2.3"))
    implementation(libs.firebase.analytics.ktx)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.database.ktx)
}
