plugins {
    id("org.jetbrains.kotlin.jvm") version "1.9.10"
    id("io.papermc.paperweight.userdev") version "1.5.5"
    id ("xyz.jpenilla.run-paper") version "2.1.0" // Adds runServer task for testing
}

group = "com.github"
version = "1.0.0"
description = "wild-RPG"

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    paperweight.paperDevBundle("1.20.1-R0.1-SNAPSHOT")
    implementation("io.github.monun:kommand-api:3.1.7")
    implementation("org.mongodb:mongodb-driver-sync:4.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")
    implementation("io.github.monun:heartbeat-coroutines:0.0.5")
    compileOnly("com.google.code.gson:gson:2.10.1")
}

//의존성 탐색하도록 설정(duplicatesStrategy 설정시 필요)
configurations.implementation.configure {
    isCanBeResolved = true
}

tasks {
    jar {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        //destinationDirectory = file(env.JAR_DIR.value)
        from(configurations.implementation.get().map { if (it.isDirectory) it else zipTree(it) })
    }
    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(17))
    }
    compileJava {
        options.encoding = "UTF-8" // We want UTF-8 for everything
        options.release.set(17)
    }
    javadoc {
        options.encoding = "UTF-8" // We want UTF-8 for everything
    }
    assemble {
        dependsOn(reobfJar)
    }
    processResources {
        filteringCharset = "UTF-8" // We want UTF-8 for everything
        val props = mapOf(
            "name" to project.name,
            "version" to project.version,
            "apiVersion" to "1.20"
        )
        inputs.properties(props)
        filesMatching("plugin.yml") {
            expand(props)
        }
    }
}

