plugins {
    id("java")
    id("maven-publish")
}

group = "maankoe"
version = "0.1"

repositories {
    mavenCentral()
    maven {
        url = uri("https://maven.pkg.github.com/maankoe/patiently")
        credentials {
            username = "maankoe"
            password = System.getenv("GITHUB_PACKAGE_READ_TOKEN")
        }
    }
}

dependencies {
    implementation("io.projectreactor:reactor-core:3.5.2")
    implementation("org.assertj:assertj-core:3.23.1")
    implementation("org.junit.jupiter:junit-jupiter:5.8.1")
    testImplementation("io.projectreactor:reactor-test:3.5.2")
    testImplementation("maankoe:patiently:0.1")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/maankoe/fluxtail")
            credentials {
                username = project.findProperty("gpr.user") as String? ?: "maankoe"
                password = project.findProperty("gpr.key") as String? ?: System.getenv("GIT_TOKEN")
            }
        }
    }
    publications {
        register<MavenPublication>("gpr") {
            from(components["java"])
        }
    }
}