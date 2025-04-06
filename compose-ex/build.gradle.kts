plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    `maven-publish`
}

android {
    namespace = "zdz.libs.compose.ex"
    compileSdk = 35

    defaultConfig {
        minSdk = 24

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
    buildFeatures.compose = true
    kotlinOptions.jvmTarget = "1.8"
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.github.ZIDOUZI"
            artifactId = project.name
            version = System.getenv("RELEASE_VERSION")?.takeIf { it.isNotEmpty() } ?: "1.0.0"

            afterEvaluate {
                from(components["release"])
            }

            // 可选：添加 POM 信息
            pom {
                name.set("Compose Ex")
                description.set("Android Compose Extensions Library")
                url.set("https://github.com/ZIDOUZI/compose-ex")

                licenses {
                    license {
                        name.set("GNU Lesser General Public License v3.0")
                        url.set("http://www.gnu.org/licenses/lgpl-3.0.html")
                    }
                }

                developers {
                    developer {
                        id.set("ZIDOUZI")
                        name.set("ZIDOUZI")
                        email.set("53157536+ZIDOUZI@users.noreply.github.com")
                    }
                }
            }
        }
    }

    repositories {
        mavenLocal {
            name = "local"
        }

        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/ZIDOUZI/compose-ex")
            credentials {
                username = project.findProperty("gpr.user") as String? ?: System.getenv("GITHUB_ACTOR")
                password = project.findProperty("gpr.key") as String? ?: System.getenv("GITHUB_TOKEN")
            }
        }
    }
}

// 添加本地测试发布任务
tasks.register("publishToLocalTest") {
    dependsOn("publishMavenPublicationToLocalRepository")
    group = "publishing"
    description = "发布到本地测试仓库 (build/local-repo)"
}

dependencies {

    implementation(libs.core.ktx)
    implementation(libs.material)
    implementation(platform(libs.compose.bom))
    implementation(libs.bundles.compose)
    implementation(libs.lifecycle.viewmodel)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}