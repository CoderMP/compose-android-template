import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.detekt)
    alias(libs.plugins.junit5)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
}

android {
    namespace = libs.versions.project.application.id.get()
    compileSdk = libs.versions.project.compile.sdk.version.get().toInt()

    buildFeatures {
        buildConfig = true
        compose = true
    }
    buildTypes {
        debug {
            isMinifyEnabled = false
            isShrinkResources = false
            isDebuggable = true
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"

            proguardFiles(
                getDefaultProguardFile(name = "proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = false

            proguardFiles(
                getDefaultProguardFile(name = "proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    defaultConfig {
        applicationId = libs.versions.project.application.id.get()
        minSdk = libs.versions.project.min.sdk.version.get().toInt()
        targetSdk = libs.versions.project.target.sdk.version.get().toInt()
        versionCode = libs.versions.project.version.code.get().toInt()
        versionName = libs.versions.project.version.name.get()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    detekt {
        toolVersion = libs.versions.detekt.get()
        config.setFrom(file("../config/detekt/detekt.yml"))
        buildUponDefaultConfig = true
        autoCorrect = true
    }
    kotlin {
        compilerOptions {
            optIn.add("kotlin.RequiresOptIn")
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }
    room {
        schemaDirectory(path = "$projectDir/schemas")
    }
    tasks.withType<Test> {
        useJUnitPlatform()
    }
}

dependencies {
    //region AndroidX Libraries
    implementation(libs.annotation)
    implementation(libs.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.material)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.security.crypto.ktx)
    implementation(libs.androidx.work)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)
    //endregion

    //region Jetpack Compose Libraries
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.bundles.compose)
    //endregion

    //region 3rd Party Libraries
    coreLibraryDesugaring(libs.desugar.jdk.libs)
    detektPlugins(libs.detekt.compose)
    implementation(libs.coil.compose)
    implementation(libs.bundles.koin)
    implementation(libs.bundles.koin.compose)
    implementation(libs.bundles.ktor)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.org.mongodb.bson)
    implementation(libs.timber)
    //endregion

    //region Debug Libraries
    debugImplementation(libs.bundles.compose.debug)
    //endregion

    //region Testing Libraries
    testImplementation(libs.assertk)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.junit)
    testImplementation(libs.junit5.api)
    testImplementation(libs.junit5.params)
    testRuntimeOnly(libs.junit5.engine)
    testImplementation(libs.mockk)
    testImplementation(libs.turbine)
    //endregion

    //region AndroidTest Libraries
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.assertk)
    androidTestImplementation(libs.bundles.ktor)
    androidTestImplementation(libs.coroutines.test)
    androidTestImplementation(libs.junit5.api)
    androidTestImplementation(libs.junit5.params)
    androidTestImplementation(libs.junit5.android.test.compose)
    androidTestRuntimeOnly(libs.junit5.engine)
    androidTestImplementation(libs.ktor.client.mock)
    androidTestImplementation(libs.mockk.android)
    androidTestImplementation(libs.turbine)
    //endregion
}