plugins {
    kotlin("plugin.serialization") version "1.5.30"
}

group = rootProject.group
version = rootProject.version

kotlin {
    sourceSets {
        @Suppress("UNUSED_VARIABLE")
        val commonMain by getting {
            dependencies {
                val kotlinxSerializationVersion: String by project
                val kotlinxDatetimeVersion: String by project
                val commonUtilVersion: String by project

                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinxSerializationVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:$kotlinxDatetimeVersion")
                implementation("kr.jadekim:common-util:$commonUtilVersion")

                api(project(":terra-types"))
            }
        }
    }
}
