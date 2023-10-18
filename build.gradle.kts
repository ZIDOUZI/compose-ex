// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    `maven-publish`
}
task<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}

true // Needed to make the Suppress annotation work for the plugins block