// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
        jcenter()
    }
    dependencies {
        classpath(libs.kotlin)
        classpath(libs.hilt.gradle)
        classpath(libs.androidx.navigation.args.gradle)
        classpath(libs.gradle)
    }
}

plugins {
    id("com.google.gms.google-services") version "4.4.2" apply false
}

allprojects {
    repositories {
        google()
        mavenCentral()
        jcenter()
        maven(url = "https://www.jitpack.io")
    }
}
