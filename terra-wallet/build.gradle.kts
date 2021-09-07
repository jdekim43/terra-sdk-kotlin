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
                implementation("kr.jadekim:common-util:1.2.1-rc3")

                api(project(":terra-types"))
                implementation(project(":terra-messages"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation("org.web3j:crypto:4.5.17")
            }
        }
    }
}
