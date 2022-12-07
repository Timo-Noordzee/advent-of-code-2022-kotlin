plugins {
    kotlin("jvm") version "1.7.22"
    id("org.jetbrains.kotlinx.benchmark") version "0.4.6"
    id("org.jetbrains.kotlin.plugin.allopen") version "1.7.22"
}

allOpen {
    annotation("org.openjdk.jmh.annotations.State")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-benchmark-runtime:0.4.6")
}

tasks {
    sourceSets {
        main {
            java.srcDirs("src")
        }
    }

    wrapper {
        gradleVersion = "7.6"
    }
}

benchmark {
    configurations {
        register("day03") {
            include("Day03")
        }
        register("day04") {
            include("Day04")
        }
        register("day05") {
            include("Day05")
        }
        register("day06") {
            include("Day06")
        }
        register("day07") {
            include("Day07")
        }
    }
    targets {
        register("main")
    }
}