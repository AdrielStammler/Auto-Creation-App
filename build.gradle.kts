import org.apache.tools.ant.taskdefs.condition.Os


var versionStr = "1.0.0"

plugins {
    java
    application
    id("org.openjfx.javafxplugin") version "0.0.13"
    id("org.beryx.runtime") version "1.13.1"
}

group = "com.cpr3663"
version = versionStr

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
    mainClass.set("com.cpr3663.autocreation.Launcher")
}

javafx {
    version = "21.0.6"
    modules = listOf("javafx.controls", "javafx.fxml", "javafx.swing")
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

val packageManagerType = providers.exec {
    commandLine("sh", "-c", "if command -v rpm >/dev/null; then echo rpm; elif command -v dpkg >/dev/null; then echo deb; else echo unknown; fi")
}.standardOutput.asText.map { it.trim() }

runtime {
    options.set(listOf("--strip-debug", "--compress", "2", "--no-header-files", "--no-man-pages"))
    jpackage {
        imageName = "Auto Creation"
        appVersion = versionStr
        if (Os.isFamily(Os.FAMILY_WINDOWS)) {
            installerType = "msi"
            installerOptions = listOf(
                "--win-dir-chooser",
                "--win-shortcut",
                "--win-menu",
                "--win-upgrade-uuid", "ae1bf218-9767-491e-acb6-3aa92756a5ed",
            )
            imageOptions.addAll(listOf("--icon", "app-icon.ico"))
        } else if (Os.isFamily(Os.FAMILY_UNIX) && !Os.isFamily(Os.FAMILY_MAC)) {
            val type = packageManagerType.get()
            if (type == "unknown") {
                error("Unknown package manager type")
            } else {
                installerType = type
                installerOptions = listOf(
                    "--linux-package-name", "auto-creation",
                    "--linux-shortcut",
                    "--linux-menu-group", "Utility",
                    "--linux-deb-maintainer", "stammler.adriel@gmail.com",
                    "--linux-app-category", "utils",
                    "--linux-package-deps",
                )
                imageOptions.addAll(listOf("--icon", "app-icon.png"))
            }
        } else if (Os.isFamily(Os.FAMILY_MAC)) {
            error("Mac is not supported")
        } else {
            error("Unsupported OS: " + System.getProperty("os.name"))
        }
    }
}
