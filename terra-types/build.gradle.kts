plugins {
    kotlin("plugin.serialization") version "1.5.30"
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
                val kotlinxSerializationVersion: String by project
                val commonUtilVersion: String by project

                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinxSerializationVersion")
                implementation("kr.jadekim:common-util:$commonUtilVersion")
            }
        }
    }
}
