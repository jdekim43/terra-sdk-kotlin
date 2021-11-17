plugins {
    kotlin("multiplatform") version "1.5.30"
    id("maven-publish")
}

group = "kr.jadekim"
version = "0.12.1"

allprojects {
    apply {
        plugin("kotlin-multiplatform")
        plugin("maven-publish")
    }

    repositories {
        mavenCentral()
        maven("https://jadekim.jfrog.io/artifactory/maven/")
    }

    kotlin {
        jvm {
            compilations.all {
                kotlinOptions.jvmTarget = "1.8"
            }
            testRuns["test"].executionTask.configure {
                useJUnitPlatform()
            }
        }

        @Suppress("UNUSED_VARIABLE")
        sourceSets {
            val commonMain by getting {
                dependencies {
                    implementation(kotlin("stdlib-common"))
                }
            }
            val commonTest by getting {
                dependencies {
                    implementation(kotlin("test-common"))
                    implementation(kotlin("test-annotations-common"))
                }
            }
            val jvmMain by getting {
                dependencies {
                    implementation(kotlin("stdlib-jdk8"))
                }
            }
            val jvmTest by getting {
                dependencies {
                    val junitVersion: String by project

                    implementation(kotlin("test-junit5"))

                    runtimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
                    compileOnly("org.junit.jupiter:junit-jupiter-api:$junitVersion")
                    compileOnly("org.junit.jupiter:junit-jupiter-params:$junitVersion")
                }
            }
        }
    }

    publishing {
        repositories {
            maven {
                val jfrogUsername: String by project
                val jfrogPassword: String by project

                setUrl("https://jadekim.jfrog.io/artifactory/maven/")

                credentials {
                    username = jfrogUsername
                    password = jfrogPassword
                }
            }
        }
    }
}

kotlin {
    @Suppress("UNUSED_VARIABLE")
    sourceSets {
        val commonMain by getting {
            dependencies {
                val kotlinxCoroutineVersion: String by project
                val kotlinxSerializationVersion: String by project
                val commonUtilVersion: String by project

                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxCoroutineVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinxSerializationVersion")
                implementation("kr.jadekim:common-util:$commonUtilVersion")

                api(project(":terra-wallet"))
                api(project(":terra-sdk-transaction"))
                api(project(":terra-client"))
                api(project(":terra-messages"))

                compileOnly(project(":terra-client-rest"))
            }
        }
        val commonTest by getting {
            dependencies {
                val ktorVersion: String by project

                implementation("io.ktor:ktor-client-logging:$ktorVersion")

                implementation(project(":terra-client-rest"))
            }
        }
    }
}

tasks.named("publish") {
    subprojects.forEach {
        dependsOn("${it.name}:publish")
    }
}
