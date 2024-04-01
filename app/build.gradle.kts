import com.android.build.api.dsl.Packaging

plugins {
    id("com.android.application")
}


android {
    packaging {
        resources {
            excludes += "META-INF/INDEX.LIST"
        }
    }

    namespace = "com.example.nocash"
    compileSdk = 34
//
//    packagingOptions {
//        exclude("META-INF/INDEX.LIST")
//        exclude("META-INF/io.netty.versions.properties")
//        exclude("META-INF/DEPENDENCIES")
//
//        exclude("codegen-resources/customization.config")
//        exclude("codegen-resources/examples-1.json")
//        exclude("codegen-resources/waiters-2.json")
//        exclude("codegen-resources/service-2.json")
//        exclude("codegen-resources/paginators-1.json")
//    }

    defaultConfig {
        applicationId = "com.example.nocash"
        minSdk = 24
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
//    val sqlite_version = "2.6.1"
//
//    // Java language implementation
//    implementation("androidx.sqlite:sqlite:$sqlite_version")
//
//    // Kotlin
//    implementation("androidx.sqlite:sqlite-ktx:$sqlite_version")
//
//    // Implementation of the AndroidX SQLite interfaces via the Android framework APIs.
//    implementation("androidx.sqlite:sqlite-framework:$sqlite_version")
//
    implementation("androidx.room:room-rxjava2:2.6.1")
    implementation("org.xerial:sqlite-jdbc:3.45.2.0")
    implementation("org.hibernate:hibernate-core:5.5.7.Final") {
        exclude(group = "javax.naming", module = "javax.naming-api")
    }

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.navigation:navigation-fragment:2.7.7")
    implementation("androidx.navigation:navigation-ui:2.7.7")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}