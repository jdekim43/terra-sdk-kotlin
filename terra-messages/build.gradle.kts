plugins {
    kotlin("plugin.serialization") version "1.5.21"
}

group = rootProject.group
version = rootProject.version

kotlin {
    sourceSets {
        @Suppress("UNUSED_VARIABLE")
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.2")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.2.1")
                implementation("kr.jadekim:common-util:1.2.1-rc3")

                api(project(":terra-types"))
            }
        }
    }
}
