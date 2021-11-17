plugins {
    kotlin("plugin.serialization") version "1.5.30"
}

group = rootProject.group
version = rootProject.version

kotlin {
    @Suppress("UNUSED_VARIABLE")
    sourceSets {
        all {
            languageSettings {
                useExperimentalAnnotation("kotlin.RequiresOptIn")
            }
        }
        val commonMain by getting {
            dependencies {
                val kotlinxCoroutineVersion: String by project
                val kotlinxSerializationVersion: String by project
                val commonUtilVersion: String by project

                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxCoroutineVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinxSerializationVersion")
                implementation("kr.jadekim:common-util:$commonUtilVersion")

                api(project(":terra-types"))
                implementation(project(":terra-wallet"))
            }
        }
    }
}
