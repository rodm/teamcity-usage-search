
import com.github.rodm.teamcity.TeamCityEnvironment
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.40"

    id ("org.gradle.jacoco")
    id ("com.github.rodm.teamcity-server") version "1.2.1"
    id ("com.github.rodm.teamcity-environments") version "1.2.1"
    id ("org.sonarqube") version "2.7.1"
}

group = "com.github.rodm"
version = "0.6-SNAPSHOT"

extra["teamcityVersion"] = findProperty("teamcity.api.version") as String? ?: "2019.1"

dependencies {
    implementation (kotlin("stdlib"))

    testImplementation (platform("org.junit:junit-bom:5.4.2"))
    testImplementation ("org.junit.jupiter:junit-jupiter-api")
    testImplementation ("org.mockito:mockito-core:2.7.22")

    testRuntimeOnly ("org.junit.jupiter:junit-jupiter-engine")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

tasks.named<Test>("test") {
    useJUnitPlatform()
    finalizedBy(tasks.named("jacocoTestReport"))
}

tasks.named<JacocoReport>("jacocoTestReport") {
    reports {
        xml.isEnabled = true
    }
}

teamcity {
    version = extra["teamcityVersion"] as String

    server {
        archiveName = "usage-search-${rootProject.version}.zip"

        descriptor {
            name = "usage-search"
            displayName = "Usage Search"
            version = rootProject.version as String?
            description = "Search for build configurations using a configuration parameter"
            vendorName = "Rod MacKenzie"
            vendorUrl = "https://github.com/rodm"
            downloadUrl = "https://github.com/rodm/teamcity-usage-search"
            email = "rod.n.mackenzie@gmail.com"
            useSeparateClassloader = true
            minimumBuild = "65998"
        }

        publish {
            channels = listOf("Beta")
            token = findProperty("jetbrains.token") as String?
        }
    }

    environments {
        operator fun String.invoke(block: TeamCityEnvironment.() -> Unit) = environments.create(this, closureOf(block))

        "teamcity2019.1" {
            version = "2019.1"
//            homeDir = file("<path to>/TeamCity")
            serverOptions ("-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005")
            agentOptions ("-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5006")
        }
    }
}
