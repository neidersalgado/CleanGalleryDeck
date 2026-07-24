plugins {
    kotlin("jvm") version "2.2.21"
}

group = "io.github.neidersalgado"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}