import java.util.Properties

plugins {
    id("module.android")
}
android {
    namespace = "com.miiiin15.whereru.remote"

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        val localProperties = Properties()
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            localProperties.load(localPropertiesFile.inputStream())
        }
        buildConfigField(
            "String",
            "FCM_PROJECT_ID",
            "\"${localProperties.getProperty("FCM_PROJECT_ID") ?: ""}\""
        )
    }
}

dependencies {
    implementation(projects.common)
    implementation(projects.data)

    implementation(libs.okhttp)
    implementation(libs.retrofit.v290)
    implementation(libs.converter.gson.v290)
    implementation(libs.google.auth.library.oauth2.http)

    implementation(platform("com.google.firebase:firebase-bom:32.2.3"))
    implementation(libs.firebase.analytics.ktx)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.database.ktx)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.messaging.ktx)
}
