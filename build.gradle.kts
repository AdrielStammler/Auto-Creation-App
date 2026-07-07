plugins {
    java
    application
    id("org.javamodularity.moduleplugin") version "1.8.15"
    id("org.openjfx.javafxplugin") version "0.0.13"
    id("org.beryx.jlink") version "2.25.0"
}

group = "com.cpr3663"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven(uri("https://frcmaven.wpi.edu/artifactory/release/"))
}

val junitVersion = "5.12.1"
var wpilibVersion = "2026.2.1"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

application {
    mainModule.set("com.cpr3663.autocreation")
    mainClass.set("com.cpr3663.autocreation.HelloApplication")
}

javafx {
    version = "21.0.6"
    modules = listOf("javafx.controls", "javafx.fxml")
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:${junitVersion}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${junitVersion}")

    implementation("edu.wpi.first.apriltag:apriltag-java:${wpilibVersion}")
    implementation("edu.wpi.first.wpimath:wpimath-java:${wpilibVersion}")
    implementation("edu.wpi.first.wpiutil:wpiutil-java:${wpilibVersion}")
    implementation("edu.wpi.first.wpiunits:wpiunits-java:${wpilibVersion}")
    implementation("us.hebi.quickbuf:quickbuf-runtime:1.4")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.21.4")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

jlink {
    imageZip.set(layout.buildDirectory.file("/distributions/app-${javafx.platform.classifier}.zip"))
    options.set(listOf("--strip-debug", "--compress", "2", "--no-header-files", "--no-man-pages"))
    launcher {
        name = "app"
    }
}
