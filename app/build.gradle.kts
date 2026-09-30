plugins { id("com.android.application") }

android {
    namespace = "com.personal.directwhatsapp"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.personal.directwhatsapp"
        minSdk = 23
        targetSdk = 35
        versionCode = 13
        versionName = "1.3"
    }
}

dependencies {
    implementation("com.googlecode.libphonenumber:libphonenumber:9.0.39")
}
