import com.github.davidmc24.gradle.plugin.avro.GenerateAvroJavaTask

plugins {
	java
	id("org.springframework.boot") version "3.5.4"
	id("io.spring.dependency-management") version "1.1.7"
	id("com.github.davidmc24.gradle.plugin.avro") version "1.9.1"
	checkstyle
}

group = "school.faang"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
	maven("https://packages.confluent.io/maven/")
}

extra["springCloudVersion"] = "2025.0.0"

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-elasticsearch")
	implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-data-redis")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.liquibase:liquibase-core")
	implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
	implementation("org.springframework.kafka:spring-kafka")
	compileOnly("org.projectlombok:lombok")
	runtimeOnly("org.postgresql:postgresql")
	annotationProcessor("org.projectlombok:lombok")
	implementation("org.mapstruct:mapstruct:1.5.3.Final")
	annotationProcessor("org.mapstruct:mapstruct-processor:1.5.3.Final")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.kafka:spring-kafka-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	implementation("io.confluent:kafka-avro-serializer:7.6.0")
	implementation("org.apache.avro:avro:1.12.0")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.9")
	implementation("io.swagger.core.v3:swagger-core-jakarta:2.2.15")
}

tasks.named<GenerateAvroJavaTask>("generateAvroJava") {
	setSource("src/main/resources/avro")
}

dependencyManagement {
	imports {
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.bootJar {
	archiveFileName.set("service.jar")
}

checkstyle {
	toolVersion = "10.17.0"
	configFile = file("${project.rootDir}/config/checkstyle/checkstyle.xml")
	checkstyle.enableExternalDtdLoad.set(true)
}

tasks.checkstyleMain {
	source = fileTree("${project.rootDir}/src/main/java")
	include("**/*.java")
	exclude("**/resources/**")

	classpath = files()
}

tasks.checkstyleTest {
	source = fileTree("${project.rootDir}/src/test")
	include("**/*.java")

	classpath = files()
}