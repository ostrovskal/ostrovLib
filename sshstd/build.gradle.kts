import com.jfrog.bintray.gradle.BintrayExtension
import org.jetbrains.dokka.gradle.DokkaTask

plugins {
    id("com.android.library")
    id("kotlin-android")
    id("com.jfrog.bintray") version "1.8.4"
    id("org.jetbrains.dokka-android") version "0.9.17"
    `maven-publish`
}
apply {
    plugin("kotlin-android")
    plugin("kotlin-android-extensions")
}

// версия библиотеки
val libVersion = "0.8.5"

// ссылка на сайт размещения проекта
val siteUrl = "https://github.com/ostrovskal/sshSTD"

// ссылка на сайт контроля версий
val gitUrl = "https://github.com/ostrovskal/sshSTD.git"

android {
    sourceSets.getByName("main").java.srcDirs("src/main/kotlin")
    compileSdkVersion(28)
    defaultConfig {
        minSdkVersion(19)
        targetSdkVersion(28)
        versionCode = 14
        versionName = libVersion
        resValue("string", "app_name", "sshSTD")
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
	        isDebuggable = false
	        isJniDebuggable = false
	        isZipAlignEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
/*
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
*/
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("'*.jar"))))
    implementation(kotlin("stdlib-jdk8", rootProject.properties["kotlinVers"].toString()))
}

tasks.named("dokka", DokkaTask::class) {
    outputFormat = "html"
    outputDirectory = "$buildDir/javadoc"
    noStdlibLink = true
}

task<Jar>("sourcesJar") {
    from(android.sourceSets["main"].java.srcDirs)
    classifier = "sources"
}

task<Jar>("dokkaJar") {
    classifier = "javadoc"
    from(tasks["dokka"])
}

task<Jar>("assembleJar") {
    classifier = ""
    val arr = rootProject.tasks["assembleRelease"]
    println(arr.name)
    from(arr)
}

group = "com.github.ostrovskal"
version = libVersion

publishing {
    publications {
        create<MavenPublication>("sergey") {
//            val arr = tasks["assembleJar"]
            artifact(tasks["sourcesJar"])
            artifact(tasks["dokkaJar"])
            artifact("$buildDir/outputs/aar/${project.name}-release.aar")
             pom {
                packaging = "aar"
                name.set("com.github.ostrovskal:sshSTD")
                description.set("A small library containing a wrapper for the classes of Android framework")
                url.set(siteUrl)
                licenses {
                    license {
                        name.set("The Apache Software License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("serg")
                        name.set("Shatalov Sergey")
                        email.set("ostrov_skal@mail.ru")
                    }
                }
                scm {
                    connection.set(gitUrl)
                    developerConnection.set(gitUrl)
                    url.set(siteUrl)
                }
            }
        }
    }
}

bintray {
    user = project.properties["bintray.user"].toString()
    key = project.properties["bintray.apikey"].toString()
    setPublications("sergey")
    pkg(delegateClosureOf<BintrayExtension.PackageConfig> {
        repo = "maven"
        name = "sshSTD"
        userOrg = "ostrov"
        websiteUrl = siteUrl
        vcsUrl = gitUrl
        publish = true
        setLicenses("Apache-2.0")
        version(delegateClosureOf<BintrayExtension.VersionConfig> {
            name = libVersion
            desc = "A small library containing a wrapper for the classes of Android framework"
            vcsTag = libVersion
            gpg(delegateClosureOf<BintrayExtension.GpgConfig> {
                sign = true
                passphrase = project.properties["bintray.password"].toString()
            })
        })
    })
}
repositories {
    mavenCentral()
}