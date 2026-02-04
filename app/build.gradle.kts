plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)

    // Apply the Google Services plugin here
    id("com.google.gms.google-services")
}

android { // The 'android' block starts here
    namespace = "com.example.csipv1"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.csipv1"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    // Move the buildTypes block inside the android block
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        dataBinding = true
    }
} // The 'android' block ends here

dependencies {
    // Firebase Bill of Materials (BOM)
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))

    // FIX: Let the BOM manage the version of firebase-auth. Use the -ktx version.
    implementation("com.google.firebase:firebase-auth-ktx")

    // Other Firebase and Google dependencies
    implementation("com.google.firebase:firebase-storage-ktx")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.0")

    // Your other existing dependencies
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.preference)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)

    // Testing dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
