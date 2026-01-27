plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
    id("dagger.hilt.android.plugin")
    id("com.google.devtools.ksp")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
    namespace = "com.orikino.fatty"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.orikino.fatty"
        minSdk = 24
        targetSdk = 36
        versionCode = 31
        versionName = "3.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        multiDexEnabled = true
        resourceConfigurations.addAll(listOf("my", "en","zh"))
    }

    flavorDimensions += "environment"

    productFlavors {
        create("playstore") {
            dimension = "environment"
            applicationId = "com.joy.food.delivery"
            resValue("string", "app_name", "Fatty Playstore")
        }
        create("apkpure") {
            dimension = "environment"
            resValue("string", "app_name", "Fatty Food")
        }
    }

    buildTypes {
        debug {
            isDebuggable = true
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            multiDexEnabled = true
            signingConfig = signingConfigs.getByName("debug")
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
        compose = true
        viewBinding = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    // implementation("com.google.devtools.ksp:symbol-processing-api:1.9.21-1.0.13")

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(platform("androidx.compose:compose-bom:2023.03.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.lifecycle:lifecycle-process:2.8.7")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    implementation("androidx.annotation:annotation:1.7.1")

    // androidx
    implementation("androidx.activity:activity-ktx:1.8.2")
    implementation("androidx.fragment:fragment-ktx:1.6.2")
    // recyclerview
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Dagger - Hilt
    implementation("com.google.dagger:hilt-android:2.48")
    ksp("com.google.dagger:hilt-compiler:2.48")

    // Room
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")

    // Retrofit
    implementation("com.google.code.gson:gson:2.8.6")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:5.0.0-alpha.3")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.0")

    // chucker log
    debugImplementation("com.github.chuckerteam.chucker:library:3.5.2")
    releaseImplementation("com.github.chuckerteam.chucker:library-no-op:3.5.2")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")

    implementation("io.arrow-kt:arrow-core:0.11.0")
    implementation("io.arrow-kt:arrow-syntax:0.11.0")
    ksp("io.arrow-kt:arrow-meta:0.11.0")

    // permissions
    // implementation("com.google.accompanist:accompanist-permissions:0.23.1")

    // Coroutine Lifecycle Scopes
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    // implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")

    // pushy notification
    implementation("me.pushy:sdk:1.0.106")

    // paper db
    implementation("io.github.pilgr:paperdb:2.7.2")

    // data store
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // image load
    implementation("io.coil-kt:coil:2.7.0")
    implementation("io.coil-kt:coil-gif:2.7.0") // Check for the latest version
    implementation ("com.squareup.picasso:picasso:2.8")
    // circle image view
    implementation("de.hdodenhof:circleimageview:3.1.0")
    // rounded image view
    implementation("com.makeramen:roundedimageview:2.3.0")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")

    // implementation("com.google.android.exoplayer:exoplayer:2.19.1")

    // dexter permissions
    implementation("com.karumi:dexter:6.2.3")

    // google play service for firebase
    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:32.7.1"))
    //firebase
    implementation("com.google.firebase:firebase-crashlytics-ktx")
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-messaging-ktx")
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-messaging-directboot:23.0.0")


    // firebase messaging
    implementation("com.google.firebase:firebase-messaging:23.1.2")



    // smart location
    implementation("io.nlopez.smartlocation:library:3.3.3")

    // KBZ
    // implementation files('libs/kbzsdk_1.1.0.aar')
    implementation(files("libs/kbzsdk_1.1.0.aar"))

    // Html
    //implementation("org.sufficientlysecure:html-textview:4.0")
    // Auto Image Slide
    //implementation("com.github.smarteist:autoimageslider:1.4.0")
    // Otp View
    implementation("com.github.aabhasr1:OtpView:v1.1.2-ktx")

    // app data store
    implementation("androidx.datastore:datastore:1.0.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
    implementation("org.jetbrains.kotlinx:kotlinx-collections-immutable:0.3.7")

    // api 34 for app update
    implementation("com.google.android.play:app-update:2.1.0")

    // tabsync
    implementation("io.github.ahmad-hamwi:tabsync:1.0.1")

    // shimmer
    implementation("com.facebook.shimmer:shimmer:0.5.0")

    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.google.android.gms:play-services-location:18.0.0")

    // lottie
    implementation("com.airbnb.android:lottie:3.7.1")

    //implementation("com.github.yalantis:ucrop:2.2.6")

    implementation("androidx.work:work-runtime-ktx:2.7.0")

    //implementation ("org.jetbrains.anko:anko-commons:0.10.8")

    implementation ("com.github.dhaval2404:imagepicker:2.1")

    // mapbox
    //implementation("com.mapbox.maps:android:10.3.0")
    //implementation("com.mapbox.search:mapbox-search-android:1.0.0-beta.26")
    // implementation ("com.mapbox.search:mapbox-search-android:1.0.0-beta.26")
    //implementation("com.mapbox.mapboxsdk:mapbox-android-sdk:9.2.1")
    //implementation("com.mapbox.mapboxsdk:mapbox-android-plugin-places-v9:0.12.0")

    //map box
    //implementation("com.mapbox.maps:android-ndk27:11.7.1")
    //implementation("com.mapbox.maps:android:11.17.1")
    //implementation ("com.mapbox.maps:android:11.10.3")
    //implementation ("com.mapbox.mapboxsdk:mapbox-android-sdk:9.7.1")
    //implementation("com.mapbox.mapboxsdk:mapbox-android-plugin-places-v9:0.12.0")

    implementation("com.squareup.retrofit2:adapter-rxjava2:2.11.0")

    implementation("com.nex3z:flow-layout:1.3.3")

    implementation("com.jsibbold:zoomage:1.3.1")

    implementation("io.coil-kt:coil:2.6.0")
    implementation("io.coil-kt:coil-svg:2.6.0")

    implementation("androidx.browser:browser:1.8.0")

    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("jp.wasabeef:glide-transformations:4.3.0")
    // If you want to use the GPU Filters
    //implementation(libs.gpuimage)
    ksp("com.github.bumptech.glide:compiler:4.16.0")
}
