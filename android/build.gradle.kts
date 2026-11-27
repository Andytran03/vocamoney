buildscript {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
    dependencies {
        if (providers.gradleProperty("skipAndroid").orNull != "true") {
            classpath(libs.gradle.android)
            classpath(libs.gradle.ksp)
        }
        classpath(libs.gradle.kotlin)
        classpath(libs.gradle.kotlin.compose)
        classpath(libs.gradle.kotlin.serialization)
    }
}
