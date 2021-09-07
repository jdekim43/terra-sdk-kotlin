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
                implementation("kr.jadekim:common-util:1.2.1-rc3")

                implementation("io.ktor:ktor-client-core:1.6.1")
                implementation("io.ktor:ktor-client-json:1.6.1")
                implementation("io.ktor:ktor-client-serialization:1.6.1")
                implementation("io.ktor:ktor-client-logging:1.6.1")

                api(project(":terra-client"))
                implementation(project(":terra-messages"))
                implementation(project(":terra-sdk-transaction"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-cio:1.6.1")
            }
        }
    }
}
