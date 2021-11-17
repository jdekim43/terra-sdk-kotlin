plugins {
    kotlin("plugin.serialization") version "1.5.30"
}

group = rootProject.group
version = rootProject.version

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                val kotlinxSerializationVersion: String by project
                val commonUtilVersion: String by project
                val ktorVersion: String by project

                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinxSerializationVersion")
                implementation("kr.jadekim:common-util:$commonUtilVersion")

                implementation("io.ktor:ktor-client-core:$ktorVersion")
                implementation("io.ktor:ktor-client-json:$ktorVersion")
                implementation("io.ktor:ktor-client-serialization:$ktorVersion")
                implementation("io.ktor:ktor-client-logging:$ktorVersion")

                api(project(":terra-client"))
                implementation(project(":terra-messages"))
                implementation(project(":terra-sdk-transaction"))
            }
        }
        val jvmMain by getting {
            dependencies {
                val ktorVersion: String by project

                implementation("io.ktor:ktor-client-cio:$ktorVersion")
            }
        }
    }
}
