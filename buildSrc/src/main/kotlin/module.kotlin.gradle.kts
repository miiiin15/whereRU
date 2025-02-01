plugins {
    id("kotlin")
    id("kotlin-kapt")
}

val libs: VersionCatalog = extensions.getByType<VersionCatalogsExtension>().named("libs")

dependencies {
    implementation(libs.getLibrary("hilt.core"))
    kapt(libs.getLibrary("hilt.compiler"))
    implementation(libs.getLibrary("coroutines.core"))
}
