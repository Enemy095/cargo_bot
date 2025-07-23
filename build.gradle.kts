plugins {
    java
    id("org.springframework.boot") version "3.4.5"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "org.cargobot.cargobotservice"
version = "1.0-SNAPSHOT"


java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    /**
    * Spring boot starters
    */
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")

//    implementation 'org.telegram:telegrambots-springboot-longpolling-starter:9.0.0'
//implementation 'org.telegram:telegrambots-client:9.0.0'

    implementation("org.telegram:telegrambots-springboot-longpolling-starter:9.0.0")
    implementation("org.telegram:telegrambots-client:9.0.0")

    implementation("com.vdurmont:emoji-java:5.1.1")


//    /**
//    * Database
//    */
    implementation("org.liquibase:liquibase-core")
    runtimeOnly("org.postgresql:postgresql")

    /**
    * Utils & Logging
    */
    implementation("org.projectlombok:lombok")
    compileOnly ("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    implementation("org.mapstruct:mapstruct:1.5.5.Final")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")


//    /**
//     * Test containers
//     */
//    testImplementation(platform("org.junit:junit-bom:5.10.0"))
//    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.bootJar {
//    archiveFileName.set("cargo-bot.jar")
    archiveBaseName.set("cargo-bot")
    archiveVersion.set("1.0.0")
    // archiveClassifier.set("")
}