import java.util.Properties
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.detekt)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
}

android {
    namespace = "com.iberdrola.practicas2026.davidsc"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.iberdrola.practicas2026.davidsc"
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

    buildFeatures {
        buildFeatures {
            viewBinding = true
            compose = true
        }
    }
}

dependencies {
    // Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)

    // Navigation
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui)

    // Lifecycle - ViewModel + LiveData
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.lifecycle.livedata)

    // Hilt
    implementation(libs.hilt.android)
    implementation(libs.androidx.runtime)
    implementation(libs.androidx.foundation)
    implementation(libs.androidx.ui.text)

    ksp(libs.hilt.compiler)

    // Retrofit
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp.logging)

    // Retromock
    implementation(libs.retromock)

    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Compose
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.activity.compose)
    debugImplementation(libs.compose.ui.tooling)

    // Hilt + Compose
    implementation(libs.hilt.navigation.compose)

    implementation(libs.compose.material.icons.extended)

    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("io.mockk:mockk:1.14.6")

    //Detekt
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.23.6")

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.config)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
}


detekt {
    buildUponDefaultConfig = true
    allRules = false

    reports {
        html.required.set(true)
        sarif.required.set(true)
        txt.required.set(false)
    }
    ignoreFailures = true
}


val sdkDir: String = run {
    val propsFile = rootProject.file("local.properties")
    if (propsFile.exists()) {
        val props = Properties()
        propsFile.reader().use { props.load(it) }
        props.getProperty("sdk.dir") ?: ""
    } else ""
}

tasks.register("adbFirebaseDebug") {
    val isWindows = System.getProperty("os.name").lowercase().contains("win")
    val adb = "$sdkDir/platform-tools/${if (isWindows) "adb.exe" else "adb"}"

    doFirst {
        val devices = Runtime.getRuntime()
            .exec(arrayOf(adb, "devices"))
            .inputStream
            .bufferedReader()
            .readLines()
            .drop(1)
            .filter { it.contains("\tdevice") && !it.startsWith("emulator-") }
            .map { it.split("\t").first() }

        if (devices.isEmpty()) {
            println("adbFirebaseDebug: no hay dispositivos fisicos conectados, omitiendo")
            return@doFirst
        }

        devices.forEach { serial ->
            println("adbFirebaseDebug: activando Firebase DebugView en $serial")
            Runtime.getRuntime()
                .exec(arrayOf(adb, "-s", serial, "shell", "setprop",
                    "debug.firebase.analytics.app",
                    "com.iberdrola.practicas2026.davidsc"))
                .waitFor()
        }
    }
}

tasks.register("adbReverse") {
    val isWindows = System.getProperty("os.name").lowercase().contains("win")
    val adb = "$sdkDir/platform-tools/${if (isWindows) "adb.exe" else "adb"}"

    doFirst {
        val devices = Runtime.getRuntime()
            .exec(arrayOf(adb, "devices"))
            .inputStream
            .bufferedReader()
            .readLines()
            .drop(1)
            .filter { it.contains("\tdevice") && !it.startsWith("emulator-") }
            .map { it.split("\t").first() }

        if (devices.isEmpty()) {
            println("adbReverse: no hay dispositivos fisicos conectados, omitiendo redireccion")
            return@doFirst
        }

        devices.forEach { serial ->
            println("adbReverse: redirigiendo puerto 3001 en dispositivo $serial")
            Runtime.getRuntime()
                .exec(arrayOf(adb, "-s", serial, "reverse", "tcp:3001", "tcp:3001"))
                .waitFor()
        }
    }
}

tasks.whenTaskAdded {
    if (name == "assembleDebug") {
        dependsOn("adbReverse")
        dependsOn("adbFirebaseDebug")
    }
}