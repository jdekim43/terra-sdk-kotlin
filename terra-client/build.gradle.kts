plugins {
    kotlin("plugin.serialization") version "1.5.21"
}

group = rootProject.group
version = rootProject.version

kotlin {
    sourceSets {
        all {
            languageSettings {
                useExperimentalAnnotation("kotlin.RequiresOptIn")
            }
        }
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.1")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.2")

                api(project(":terra-types"))
                implementation(project(":terra-sdk-transaction"))
            }
        }
    }
}
