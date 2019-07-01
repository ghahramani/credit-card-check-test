import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

object Version {
    const val RABBITMQ_MOCK = "1.0.10"
    const val CUCUMBER = "1.2.5"
    const val JAVAX = "1"
    const val JJWT = "0.9.0"
    const val LOGSTASH = "5.0"
    const val MONGOBEE = "0.13"
    const val MAPSTRUCT = "1.3.0.Final"
    const val CSV = "3.11.5"
}

plugins {
    id("org.springframework.boot") version "2.1.6.RELEASE"
    id("io.spring.dependency-management") version "1.0.7.RELEASE"
    kotlin("jvm") version "1.3.40"
    kotlin("plugin.spring") version "1.3.40"
    kotlin("kapt") version "1.3.40"
}

group = "com.navid.test"
version = "0.0.1"
java.sourceCompatibility = JavaVersion.VERSION_1_8

val developmentOnly by configurations.creating
configurations {
    runtimeClasspath {
        extendsFrom(developmentOnly)
    }
    implementation {
        exclude(module = "spring-boot-starter-tomcat")
        exclude(module = "log4j-to-slf4j")
        exclude(group = "com.vaadin.external.google", module = "android-json")
        exclude(group = "org.springframework.cloud", module = "spring-cloud-starter-netflix-ribbon")
    }
}

repositories {
    mavenCentral()
}

extra["springCloudVersion"] = "Greenwich.SR1"

dependencies {
    // Metrics, Pull strategy
    implementation("io.micrometer:micrometer-registry-jmx")
    implementation("io.micrometer:micrometer-registry-prometheus")

    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-logging")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.fasterxml.jackson.module:jackson-module-afterburner")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-json-org")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-hppc")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    implementation("com.fasterxml.jackson.core:jackson-annotations")
    implementation("com.fasterxml.jackson.core:jackson-databind")

    implementation("org.apache.commons:commons-lang3")

    implementation("com.jayway.jsonpath:json-path")

    implementation("com.hazelcast:hazelcast")
    implementation("com.hazelcast:hazelcast-spring")

    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation("org.springframework.cloud:spring-cloud-stream-reactive")
    implementation("org.springframework.cloud:spring-cloud-stream-binder-rabbit")
    implementation("org.springframework.retry:spring-retry")
    implementation("org.springframework:spring-context-support")

    // Loads service discovery
    implementation("org.springframework.cloud:spring-cloud-starter-consul-discovery")

    // Loads sleuth and zipkin
    implementation("org.springframework.cloud:spring-cloud-starter-zipkin")

    // Only to use for zipkin as we do not have any use for rabbitmq in this project
    implementation("org.springframework.boot:spring-boot-starter-amqp")

    developmentOnly("org.springframework.boot:spring-boot-devtools")

    implementation("javax.inject:javax.inject:${Version.JAVAX}")
    implementation("io.jsonwebtoken:jjwt:${Version.JJWT}")
    implementation("net.logstash.logback:logstash-logback-encoder:${Version.LOGSTASH}")

    implementation("com.github.mongobee:mongobee:${Version.MONGOBEE}")

    implementation("org.simpleflatmapper:sfm-csv:${Version.CSV}")

    implementation("org.mapstruct:mapstruct-jdk8:${Version.MAPSTRUCT}")
    kapt("org.mapstruct:mapstruct-processor:${Version.MAPSTRUCT}")

    testImplementation("info.cukes:cucumber-junit:${Version.CUCUMBER}")
    testImplementation("info.cukes:cucumber-spring:${Version.CUCUMBER}")

    testImplementation("com.github.fridujo:rabbitmq-mock:${Version.RABBITMQ_MOCK}")
    testImplementation("org.assertj:assertj-core")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("de.flapdoodle.embed:de.flapdoodle.embed.mongo")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.springframework.cloud:spring-cloud-stream-test-support")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
        languageVersion = "1.3"
    }
}
