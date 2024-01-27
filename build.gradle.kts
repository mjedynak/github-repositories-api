plugins {
    java
    id("org.springframework.boot") version "3.2.2"
    id("io.spring.dependency-management") version "1.1.4"
    id("com.diffplug.spotless") version "6.25.0"
}

group = "pl.mjedynak.github.api"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation ("org.springdoc:springdoc-openapi-starter-webflux-ui:2.3.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.16.1")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("com.maciejwalkowiak.spring:wiremock-spring-boot:2.1.0")
    testImplementation("com.tngtech.archunit:archunit:1.2.1")

    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

spotless {
    java {
        palantirJavaFormat()
    }
}
