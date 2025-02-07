pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
        jcenter()
    }
}
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "whereru"
include(":app")
include(":ui")
include(":domain")
include(":data")
include(":data-resource")
include(":presentation")
include(":common")
include(":remote")
include(":local")
