plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.sumup.taptopay.sampleapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.sumup.taptopay.sampleapp"
        minSdk = 30
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        debug {
            isDebuggable = true
        }
        release {
            /*
                NOTE: This line is important to ensure that the Tap to Pay SDK works and passes attestation.
                Making the build debuggable will cause the attestation to fail and the payments will not work.

                To test the SDK with a debuggable build, you have to use an extdev account
             */
            isDebuggable = false
        }
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        viewBinding = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
}

dependencies {

    implementation("com.sumup.tap-to-pay:utopia-sdk:1.1.1") // Production dependency

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.core:core-ktx:1.8.0")
    implementation("androidx.activity:activity-ktx:1.7.2")
    implementation("androidx.activity:activity-compose:1.7.2")

    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.5")

    // Compose
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.1")
    implementation("androidx.compose.material3:material3:1.2.0")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")
}
