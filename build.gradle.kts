
import com.github.rodm.teamcity.TeamCityEnvironment
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.31"

    id ("com.github.rodm.teamcity-server") version "1.2.1"
    id ("com.github.rodm.teamcity-environments") version "1.2.1"
}

group = "com.github.rodm"
version = "0.1-SNAPSHOT"

extra["teamcityVersion"] = findProperty("teamcity.api.version") as String? ?: "2019.1"

dependencies {
    implementation (kotlin("stdlib"))

    testImplementation (group = "org.mockito", name = "mockito-core", version = "2.7.22")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
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
    }

    environments {
        operator fun String.invoke(block: TeamCityEnvironment.() -> Unit) = environments.create(this, closureOf(block))

        "teamcity2019.1" {
            version = "2019.1"
            serverOptions ("-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005")
            agentOptions ("-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5006")
        }
    }
}