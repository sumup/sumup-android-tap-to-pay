buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.4.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.22")
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
                username = "your_username_provided_by_sumup"
                password = "your_password_provided_by_sumup"
            }
        }
        google()
        mavenCentral()
    }
}

tasks.named<Wrapper>("wrapper") {
    distributionType = Wrapper.DistributionType.ALL
    gradleVersion = "7.5.1"
}

tasks.register<Delete>("clean") {
    delete(layout.buildDirectory)
}
