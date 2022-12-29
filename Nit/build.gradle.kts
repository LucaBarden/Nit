import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.21"
    application
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "de.nit.MainKt"
    }

    archiveFileName.set("${project.name}.jar")

    duplicatesStrategy = org.gradle.api.file.DuplicatesStrategy.EXCLUDE

    from(sourceSets.main.get().output)

    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })
}


group = "de.nit"
version = "0.1-ALPHA"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("MainKt")
}