plugins {
    java
    jacoco
    id("org.springframework.boot") version "3.4.4"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.sonarqube") version "6.0.1.5171"
}

group = "id.ac.ui.cs.gatherlove"
version = "0.0.1-SNAPSHOT"

val seleniumJavaVersion = "4.14.1"
val seleniumJupiterVersion = "5.0.1"
val webdrivermanagerVersion = "5.6.3"
val junitJupiterVersion = "5.9.1"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
	implementation("org.springframework.data:spring-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.postgresql:postgresql:42.7.2")
    implementation("io.github.cdimascio:dotenv-java:3.0.0")
    implementation("javax.annotation:javax.annotation-api:1.3.2")
    compileOnly("org.projectlombok:lombok")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation("jakarta.validation:jakarta.validation-api:3.0.2")
    implementation("org.hibernate.validator:hibernate-validator:8.0.1.Final")
    testImplementation("com.h2database:h2")
    testImplementation("org.assertj:assertj-core:3.24.2")
}

tasks.test {
    filter {
        excludeTestsMatching("*FunctionalTest")
    }

    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
}

tasks.withType<Test> {
    useJUnitPlatform()
}

sonar {
    properties {
        property("sonar.projectKey", "Kelompok-B03_campaign-donation-wallet")
        property("sonar.organization", "kelompok-b03")
        property("sonar.host.url", "https://sonarcloud.io")
    }
}
