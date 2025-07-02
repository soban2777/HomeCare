import com.android.build.gradle.internal.utils.KSP_PLUGIN_ID


plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
    id("kotlin-kapt")



}





android {
    namespace = "com.example.myapplication"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.myapplication"
        minSdk = 23
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures{
        viewBinding=true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.firebase.auth)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.database)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.database.ktx)
    implementation(libs.places)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

   // navigation component
    implementation (libs.androidx.navigation.fragment.ktx.v277)
    implementation (libs.androidx.navigation.ui.ktx.v277)

    //  for using shapeable image
    implementation(libs.material.v1110)

    //splash screen
    implementation(libs.androidx.core.splashscreen)

    //google
    implementation(libs.play.services.auth)

    //For Google Identity Services
    implementation(libs.play.services.identity)

    //For Credential Manager
    implementation(libs.androidx.credentials.v110alpha02)

        // For Location
    implementation (libs.play.services.location)

    // Room
    implementation (libs.androidx.room.runtime.v251)
    implementation (libs.androidx.room.ktx.v251)
    kapt (libs.androidx.room.compiler)
    // Sdp Dimen Library
    implementation ("com.intuit.sdp:sdp-android:1.1.0")
    implementation ("androidx.swiperefreshlayout:swiperefreshlayout:1.2.0-alpha01")

}