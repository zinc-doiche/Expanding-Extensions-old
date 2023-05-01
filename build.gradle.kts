plugins {
    id("org.jetbrains.kotlin.jvm") version "1.8.21"
    id ("java")
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "com.github"
version = "1.0-ALPHA"

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
    maven {
        url = uri("https://repo.codemc.io/repository/maven-public/")
    }
}

dependencies {
    implementation("org.mybatis:mybatis:3.5.13")
    implementation("mysql:mysql-connector-java:8.0.33")
    implementation("io.github.monun:invfx-api:3.3.0")
    implementation("io.github.monun:invfx-core:3.3.0")
//    implementation("io.github.monun:kommand-api:3.1.3")
//    implementation("io.github.monun:kommand-core:3.1.3")
    compileOnly("io.papermc.paper:paper-api:1.19.4-R0.1-SNAPSHOT")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
}

tasks.withType(JavaCompile::class.java) {
    options.encoding = "UTF-8"
}

