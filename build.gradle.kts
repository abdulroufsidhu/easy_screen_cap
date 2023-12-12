plugins {
	id("com.android.library")
	id("org.jetbrains.kotlin.android")
	id("maven-publish")
}

android {
	namespace = "io.github.abdulroufsidhu.easy_screen_capture"
	compileSdk = 34
	
	defaultConfig {
		arrMetadata {
			minCompileSdk = 21
		}
		minSdk = 21
		
		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
		consumerProguardFiles("consumer-rules.pro")
	}
	
	buildTypes {
		release {
			isMinifyEnabled = false
			proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
		}
	}
	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_1_8
		targetCompatibility = JavaVersion.VERSION_1_8
	}
	kotlinOptions {
		jvmTarget = "1.8"
	}
	testFixtures {
		enable = true
	}
	publishing {
		singleVariant("release") {
			withSourcesJar()
		}
	}
}

dependencies {
	
	implementation("androidx.core:core-ktx:1.12.0")
	implementation("androidx.appcompat:appcompat:1.6.1")
}

afterEvaluate {
	publishing {
		publications {
			release
		}
	}
}