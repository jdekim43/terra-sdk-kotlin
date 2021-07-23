plugins {
    kotlin("plugin.serialization") version "1.5.21"
}

group = rootProject.group
version = rootProject.version

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.2")
                implementation("kr.jadekim:common-util:1.1.16")

                implementation("io.ktor:ktor-client-core:1.6.1")
                implementation("io.ktor:ktor-client-json:1.6.1")
                implementation("io.ktor:ktor-client-serialization:1.6.1")
                implementation("io.ktor:ktor-client-logging:1.6.1")

                api("${rootProject.group}:terra-client:${rootProject.version}")
                implementation("${rootProject.group}:terra-sdk-transaction:${rootProject.version}")
                implementation("${rootProject.group}:terra-messages:${rootProject.version}")
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-cio:1.6.1")
            }
        }
    }
}
