plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    kotlin("kapt")
}

android {
    namespace = "com.example.gymmanagement"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.gymmanagement"
        minSdk = 24
        targetSdk = 35
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures{
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    dependencies {
//        implementation("com.github.florent37:runtime-permission:1.1.2") // Runtime permission
        implementation("com.github.bumptech.glide:glide:4.10.0") // Show images
//        debugImplementation("com.github.amitshekhariitbhu.Android-Debug-Database:debug-db:1.0.7") // View database
        implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.3.1") // ViewModelScope
        implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.3.1") // LifecycleScope


        // Runtime Permissions using Activity Result API
        implementation("androidx.activity:activity-ktx:1.8.2")

    }


}