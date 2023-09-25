plugins {
    id("org.jetbrains.kotlin.jvm") version "1.9.10"
    id("io.papermc.paperweight.userdev") version "1.5.5"
    id ("xyz.jpenilla.run-paper") version "2.1.0" // Adds runServer task for testing
}

group = "com.github"
version = "1.0.0"

repositories {
    mavenCentral()
    mavenLocal()
    maven {
        name = "papermc-repo"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
    maven {
        name = "sonatype"
        url = uri("https://oss.sonatype.org/content/groups/public/")
    }
    maven("https://repo.codemc.io/repository/maven-public/")
}

dependencies {
    paperweight.paperDevBundle("1.20.1-R0.1-SNAPSHOT")
    compileOnly("com.google.code.gson:gson:2.10.1")
    implementation("org.mongodb:mongodb-driver-sync:4.9.0")
    implementation("io.github.monun:kommand-api:3.1.7")
}

tasks.withType(JavaCompile::class.java) {
    options.encoding = "UTF-8"
}

tasks {
    compileJava {
        options.encoding = "UTF-8" // We want UTF-8 for everything
        options.release.set(17)
    }
    javadoc {
        options.encoding = "UTF-8" // We want UTF-8 for everything
    }
    processResources {
        filteringCharset = "UTF-8" // We want UTF-8 for everything
        val props = mapOf(
            "name" to project.name,
            "version" to  project.version,
            "description" to project.description,
            "apiVersion" to "1.19"
        )

        inputs.properties(props)

        filesMatching("plugin.yml") {
            expand(props)
        }
    }
}

