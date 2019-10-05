
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile

group = "tanvd.itmo.kotlin"
version = "0.1.0-SNAPSHOT"

plugins {
    kotlin("jvm") version "1.3.50" apply true
    id("tanvd.kosogor") version "1.0.7" apply true
}

repositories {
    jcenter()
    mavenCentral()
}

dependencies {
    api(kotlin("stdlib"))
    api("tanvd.kex", "kex", "0.1.1")
    testImplementation("junit:junit:4.12")
}

tasks.withType<KotlinJvmCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
        languageVersion = "1.3"
        apiVersion = "1.3"
    }
}

tasks.withType<Test> {
    useJUnit()

    testLogging {
        events("passed", "skipped", "failed")
    }
}
