plugins {
    kotlin("plugin.serialization") version "1.5.21"
}

group = rootProject.group
version = rootProject.version

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.1")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.2")

                api("${rootProject.group}:terra-types:${rootProject.version}")
                implementation("${rootProject.group}:terra-sdk-transaction:${rootProject.version}")
            }
        }
    }
}