plugins {
    kotlin("plugin.serialization") version "1.5.21"
}

group = rootProject.group
version = rootProject.version

kotlin {
    jvm {
        withJava()
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.1")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.2")
                implementation("kr.jadekim:common-util:1.1.16")

                api("${rootProject.group}:terra-types:${rootProject.version}")
                implementation("${rootProject.group}:terra-messages:${rootProject.version}")
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation("org.web3j:crypto:4.5.17")
            }
        }
    }
}