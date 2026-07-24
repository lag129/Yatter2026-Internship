plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.compose.compiler)
  alias(libs.plugins.kotlin.serialization)
  alias(libs.plugins.openapi.generator)
}

android {
  namespace = "com.dmm.bootcamp.yatter"
  compileSdk = 36

  defaultConfig {
    applicationId = "com.dmm.bootcamp.yatter"
    minSdk = 24
    targetSdk = 36
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    vectorDrawables {
      useSupportLibrary = true
    }
  }

  buildTypes {
    debug {
      buildConfigField("String", "API_URL", "\"https://yatter-backend-255491101186.asia-northeast1.run.app/v1\"")
    }
    release {
      isMinifyEnabled = false
      proguardFiles(
        getDefaultProguardFile("proguard-android-optimize.txt"),
        "proguard-rules.pro",
      )
      buildConfigField("String", "API_URL", "\"https://yatter-backend-255491101186.asia-northeast1.run.app/v1\"")
    }
  }
  buildFeatures {
    compose = true
    buildConfig = true
  }
  composeOptions {
    kotlinCompilerExtensionVersion = "1.5.11"
  }
}

kotlin {
  compilerOptions {
    freeCompilerArgs.add("-Xannotation-default-target=param-property")
  }
}

dependencies {
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.activity.compose)
  implementation(libs.material)
  implementation(platform(libs.androidx.compose.bom))
  implementation(libs.androidx.ui)
  implementation(libs.androidx.ui.graphics)
  implementation(libs.androidx.ui.tooling.preview)
  implementation(libs.androidx.material)
  implementation(libs.androidx.material.icons.extended)
  implementation(libs.androidx.material3)
  implementation(libs.androidx.runtime.livedata)
  implementation(libs.androidx.lifecycle.runtime.compose)
  implementation(libs.androidx.navigation3.runtime)
  implementation(libs.androidx.navigation3.ui)
  implementation(libs.kotlinx.serialization.json)
  implementation(libs.retrofit)
  implementation(libs.converter.moshi)
  implementation(libs.retrofit2.kotlin.coroutines.adapter)
  implementation(libs.logging.interceptor)
  implementation(libs.moshi)
  implementation(libs.moshi.kotlin)
  implementation(platform(libs.koin.bom))
  implementation(libs.koin.core)
  implementation(libs.koin.android)
  implementation(libs.koin.androidx.compose)
  implementation(libs.coil.compose)
  implementation(libs.androidx.core.splashscreen)
  implementation(project(":api"))

  testImplementation(libs.junit)
  testImplementation(libs.mockk)
  testImplementation(libs.truth)
  testImplementation(libs.kotlin.test)
  testImplementation(libs.kotlinx.coroutines.test)
  testImplementation(libs.androidx.core.testing)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.espresso.core)
  androidTestImplementation(platform(libs.androidx.compose.bom))
  androidTestImplementation(libs.androidx.ui.test.junit4)
  debugImplementation(libs.androidx.ui.tooling)
  debugImplementation(libs.androidx.ui.test.manifest)
}

openApiGenerate {
  generatorName = "kotlin"
  inputSpec = "$rootDir/api_reference/openapi.yml"
  outputDir = "$rootDir/api"
  templateDir = "$rootDir/templates"
  packageName = "remote"
  generateApiTests = false
  generateModelTests = false
  generateApiDocumentation = false
  generateModelDocumentation = false
  library = "jvm-retrofit2"
  configOptions = mapOf(
    "serializationLibrary" to "moshi",
    "useSettingsGradle" to "true",
    "omitGradleWrapper" to "true",
    "omitGradlePluginVersions" to "true",
  )
  additionalProperties = mapOf("useCoroutines" to "true", "moshiCodeGen" to "true")
  cleanupOutput = true
}
