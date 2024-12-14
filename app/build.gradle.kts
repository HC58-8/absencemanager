plugins {
    id("com.android.application")
    id("com.google.gms.google-services")  // Appliquer le plugin Google services pour Firebase
}

android {
    namespace = "com.example.mobile"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.mobile"
        minSdk = 32
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        viewBinding = true  // Activer ViewBinding pour un accès plus facile aux vues
    }
}

dependencies {
    // Dépendances de base AndroidX
    implementation("androidx.appcompat:appcompat:1.6.1")

    // Firebase - Authentification et Firestore
    implementation(platform("com.google.firebase:firebase-bom:33.5.1"))  // Utilisation de Firebase BOM pour gérer les versions
    implementation("com.google.firebase:firebase-auth")  // Authentification Firebase
    implementation("com.google.firebase:firebase-firestore")  // Firestore
    implementation("com.google.firebase:firebase-analytics")  // Analytics Firebase

    // Dépendances pour Material Design
    implementation("com.google.android.material:material:1.9.0")

    // Dépendances pour Activity et ViewModel
    implementation("androidx.activity:activity-ktx:1.7.0") // Assurez-vous de ne pas avoir de doublons
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1") // Ou la version la plus récente disponible
    implementation("com.google.android.gms:play-services-auth:20.7.0")
    implementation("com.google.firebase:firebase-auth:21.0.1")

    // Google Sign-In
    implementation ("com.google.android.gms:play-services-auth:20.0.1")
    implementation ("androidx.recyclerview:recyclerview:1.2.1") // Assurez-vous d'avoir cette dépendance
    implementation ("androidx.recyclerview:recyclerview:1.2.1") // ou la dernière version stable

    // Firebase
    implementation ("com.google.firebase:firebase-core:21.1.0")
    // Dépendances pour Firebase et Google Services
    implementation("com.google.firebase:firebase-messaging") // Si vous utilisez Firebase Cloud Messaging
    implementation("com.google.firebase:firebase-database")
    implementation(libs.recyclerview)  // Si vous utilisez Firebase Realtime Database
    implementation ("androidx.recyclerview:recyclerview:1.2.1") // Assurez-vous que cette ligne est présente
    implementation ("androidx.recyclerview:recyclerview:1.2.1") // Assurez-vous que cette ligne est présente


}

