plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
	alias(libs.plugins.ksp)
	alias(libs.plugins.hilt)
	alias(libs.plugins.jetbrains.kotlin.serialization)
	alias(libs.plugins.androidx.room)
}

android {
    namespace = "org.atmatto.tasks"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "org.atmatto.tasks"
        minSdk = 29
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
	implementation(libs.androidx.room.runtime)
	implementation(libs.androidx.lifecycle.viewmodel.compose)
	implementation(libs.androidx.navigation3.ui)
	implementation(libs.androidx.navigation3.runtime)
	implementation(libs.androidx.lifecycle.viewmodel.navigation3)
	implementation(libs.androidx.material3.adaptive.navigation3)
	implementation(libs.kotlinx.serialization.core)
	implementation(libs.hilt)
	implementation(libs.hilt.navigation.compose)
	implementation(libs.material.components.android)
	implementation(libs.datastore.preferences)
	ksp(libs.hilt.compiler)
	ksp(libs.androidx.room.compiler)
	testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}

room {
	schemaDirectory("$projectDir/schemas")
}
