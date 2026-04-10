plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")  // ← on revient à kapt, plus simple
}

android {
    namespace = "com.example.todoapp"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.todoapp"
        minSdk = 26
        targetSdk = 36
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

val roomVersion = "2.6.1"  // ← version stable, zéro problème

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    // Activity KTX — nécessaire pour "by viewModels()"
    implementation("androidx.activity:activity-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Room
    implementation("androidx.room:room-runtime:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")  // ← kapt()
    implementation("androidx.room:room-ktx:$roomVersion")

    // ViewModel + LiveData
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // Tests
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.room:room-testing:$roomVersion")
}