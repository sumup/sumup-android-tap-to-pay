buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.9.1")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.2.21")
    }
}

allprojects {
    repositories {
        maven {
            url = uri("https://maven.sumup.com/releases")
        }
        maven {
            url = uri("https://tap-to-pay-sdk.fleet.live.sumup.net/")
            credentials {
                username = System.getenv("TAP_TO_PAY_MAVEN_USER") ?: "your_username_provided_by_sumup"
                password = System.getenv("TAP_TO_PAY_MAVEN_PASSWORD") ?: "your_password_provided_by_sumup"
            }
        }
        google()
        mavenCentral()
    }
}

tasks.named<Wrapper>("wrapper") {
    distributionType = Wrapper.DistributionType.ALL
    gradleVersion = "8.11.1"
}

tasks.register<Delete>("clean") {
    delete(layout.buildDirectory)
}
