import java.util.Properties

plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.composeHotReload) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.mavenPublish) apply false
}

// vanniktech Sonatype resolves mavenCentralUsername/Password like gradle.properties on the ROOT
// project. Loading only in :shimmer is not enough (#866). This applies the same file to all projects.
// If it still fails, use ./publishToMaven/publish-central.sh (exports ORG_GRADLE_PROJECT_*) or put the
// two mavenCentral* lines in ~/.gradle/gradle.properties.
allprojects {
    val secretsFile = rootProject.file("publishToMaven/secrets.properties")
    if (secretsFile.exists()) {
        secretsFile.reader().use { reader ->
            val props = Properties()
            props.load(reader)
            props.forEach { k, v ->
                extensions.extraProperties.set(k.toString(), v.toString())
            }
        }
    }
}