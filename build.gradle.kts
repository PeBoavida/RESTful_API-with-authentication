plugins {
    id("java")
    id("org.springframework.boot") version "3.2.0"
    id("io.spring.dependency-management") version "1.1.3"
    id("jacoco")
    id("org.openapi.generator") version "7.2.0"
}

group = "com.sky.pedroboavida.test"
version = "1.0.0"
java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.liquibase:liquibase-core")

    runtimeOnly("org.postgresql:postgresql")

    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    implementation("io.micrometer:micrometer-registry-prometheus")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.testcontainers:testcontainers")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.mockito:mockito-core")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    
    // OpenAPI Generator
    implementation("org.openapitools:jackson-databind-nullable:0.2.6")
    implementation("jakarta.validation:jakarta.validation-api")
    implementation("io.swagger.core.v3:swagger-annotations:2.2.15")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

// OpenAPI Generator configuration
openApiGenerate {
    generatorName.set("spring")
    inputSpec.set("$projectDir/src/main/resources/openapi.yaml")
    outputDir.set("$buildDir/generated")
    apiPackage.set("com.sky.pedroboavida.test.api")
    modelPackage.set("com.sky.pedroboavida.test.model")
    invokerPackage.set("com.sky.pedroboavida.test.invoker")
    configOptions.set(mapOf(
        "interfaceOnly" to "true",
        "useSpringBoot3" to "true",
        "useJakartaEe" to "true",
        "dateLibrary" to "java8",
        "java8" to "true",
        "hideGenerationTimestamp" to "true",
        "library" to "spring-boot",
        "serializationLibrary" to "jackson",
        "openApiNullable" to "false",
        "useBeanValidation" to "true",
        "performBeanValidation" to "true",
        "useTags" to "true"
    ))
    typeMappings.set(mapOf(
        "DateTime" to "java.time.LocalDateTime"
    ))
    importMappings.set(mapOf(
        "java.time.LocalDateTime" to "java.time.LocalDateTime"
    ))
}

// Make generated code available to main source set
sourceSets {
    main {
        java {
            srcDir("$buildDir/generated/src/main/java")
        }
    }
}

// Ensure OpenAPI generation runs before compilation
tasks.compileJava {
    dependsOn("openApiGenerate")
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
        html.required.set(true)
    }

    classDirectories.setFrom(
        sourceSets.main.get().output.asFileTree.matching {
            exclude("**/dto/**")
            exclude("**/api/**")
            exclude("**/model/**")
        }
    )
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = "0.0".toBigDecimal()
            }
        }
    }
    
    classDirectories.setFrom(
        files(classDirectories.files.map {
            fileTree(it) {
                exclude("**/dto/**")
            }
        })
    )
}
