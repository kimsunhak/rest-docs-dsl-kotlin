import groovy.lang.Closure
import io.swagger.v3.oas.models.servers.Server

plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.4.5"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.asciidoctor.jvm.convert") version "3.3.2"
    id("com.epages.restdocs-api-spec") version "0.17.1"
    id("org.hidetake.swagger.generator") version "2.18.2"
}

group = "me.shower"
version = "0.0.1"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
    create("asciidoctorExtension")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    api("org.springframework.restdocs:spring-restdocs-mockmvc")
    api("org.springframework.restdocs:spring-restdocs-restassured")
    api("io.rest-assured:spring-mock-mvc")
    api("com.epages:restdocs-api-spec-mockmvc:0.17.1")
    api("com.epages:restdocs-api-spec-restassured:0.17.1")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    compileOnly("jakarta.servlet:jakarta.servlet-api")
    compileOnly("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.asciidoctor {
    configurations("asciidoctorExtension")
    baseDirFollowsSourceFile()
    dependsOn("restDocsTest")
}

tasks.register<Test>("restDocsTest") {
    group = "verification"
    useJUnitPlatform {
        includeTags("restdocs")
    }
}

tasks.register<Copy>("copyOasSwagger") {
    dependsOn("openapi3")
    doFirst {
        delete("${project.property("openapi3IntoDirectory")}/${project.property("openapi3JsonName")}.yaml")

        val jwtSchemes = "  securitySchemes:\n" +
                "    Authorization:\n" +
                "      type: http\n" +
                "      scheme: bearer\n" +
                "      bearerFormat: JWT\n" +
                "security:\n" +
                "  - Authorization: []"
        file("${project.property("openapi3OutDirectory")}/${project.property("openapi3JsonName")}.yaml")
            .appendText(jwtSchemes)
    }
    from("${project.property("openapi3OutDirectory")}/${project.property("openapi3JsonName")}.yaml")
    into("${project.property("openapi3IntoDirectory")}")
}

tasks.getByName("asciidoctor") {
    dependsOn("restDocsTest")
}

openapi3 {
    fun toServer(url: String): Closure<Server> = closureOf<Server> { this.url = url } as Closure<Server>
    val serverUrls = (project.property("openapi3ServerUrls") as String).split(",")
    setServers(serverUrls.map { toServer(it.trim()) })
    title = "${project.property("openapi3Title")}"
    description = "${project.property("openapi3Description")}"
    version = "${project.property("openapi3DocsVersion")}"
    format = "yaml"
    outputFileNamePrefix = "${project.property("openapi3JsonName")}"
    outputDirectory = "${project.property("openapi3OutDirectory")}"
}
