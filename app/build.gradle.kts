import com.android.build.gradle.ProguardFiles.getDefaultProguardFile
import org.gradle.internal.impldep.com.amazonaws.PredefinedClientConfigurations.defaultConfig
import org.jetbrains.kotlin.com.intellij.util.lang.JavaVersion
import org.jetbrains.kotlin.js.dce.InputResource.Companion.file
import org.jetbrains.kotlin.js.translate.context.Namer.kotlin

plugins {
    id("com.android.application")
    id("kotlin-android")
}

android {
    signingConfigs {
        create("default") {
            storeFile = file("E:/Dev/Android/keys_store/sshLIB.jks")
            keyAlias = "keyLIB"
            keyPassword = "IfnfkjdCthutqLIB"
            storePassword = "IfnfkjdCthutq"
        }
    }
    sourceSets.getByName("main").java.srcDirs("src/main/kotlin")
    compileSdkVersion(28)
    defaultConfig {
        applicationId = "com.github.ostrovskal.ostrovlib"
        minSdkVersion(19)
        targetSdkVersion(28)
        versionCode = 11
        versionName = "0.8.2"
        resValue("string", "app_name", "ostrovLib")
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isDebuggable = false
            isJniDebuggable = false
            isShrinkResources = true
            isZipAlignEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("default")
        }
    }
    compileOptions {
        sourceCompatibility = org.gradle.api.JavaVersion.VERSION_1_8
        targetCompatibility = org.gradle.api.JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("'*.jar"))))
    implementation(kotlin("stdlib-jdk8", "1.3.11"))
    implementation(project(":sshstd"))
}
