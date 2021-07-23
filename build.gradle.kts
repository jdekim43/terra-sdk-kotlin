plugins {
    kotlin("multiplatform") version "1.5.21"
    id("maven-publish")
}

group = "kr.jadekim"
version = "0.10.1"

allprojects {
    apply {
        plugin("kotlin-multiplatform")
        plugin("maven-publish")
    }

    repositories {
        mavenCentral()
        maven("https://jadekim.jfrog.io/artifactory/maven/")
    }

    configurations.all {
        resolutionStrategy.dependencySubstitution.all {
            requested.let {
                if (it is ModuleComponentSelector && it.group == rootProject.group && it.version == rootProject.version) {
                    val targetProject = findProject(":${it.module}")
                    if (targetProject != null) {
                        useTarget(targetProject)
                    }
                }
            }
        }
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
                    implementation(kotlin("test-junit5"))
                    runtimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.2")
                    compileOnly("org.junit.jupiter:junit-jupiter-api:5.7.2")
                    compileOnly("org.junit.jupiter:junit-jupiter-params:5.7.2")
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
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.1")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.2")
                implementation("kr.jadekim:common-util:1.1.16")

                api("${rootProject.group}:terra-wallet:${rootProject.version}")
                api("${rootProject.group}:terra-sdk-transaction:${rootProject.version}")
                api("${rootProject.group}:terra-client:${rootProject.version}")
                api("${rootProject.group}:terra-messages:${rootProject.version}")

                compileOnly("${rootProject.group}:terra-client-rest:$version")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation("io.ktor:ktor-client-logging:1.6.1")

                implementation("${rootProject.group}:terra-client-rest:${rootProject.version}")
            }
        }
    }
}

tasks.named("publish") {
    subprojects.forEach {
        dependsOn("${it.name}:publish")
    }
}
