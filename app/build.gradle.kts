plugins { id("com.android.application") }

android {
    namespace = "com.personal.directwhatsapp"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.personal.directwhatsapp"
        minSdk = 23
        targetSdk = 35
        versionCode = 14
        versionName = "1.3"
    }
}

dependencies {
    implementation("com.googlecode.libphonenumber:libphonenumber:9.0.39")
    implementation("com.google.android.gms:play-services-ads:24.7.0")
    testImplementation("junit:junit:4.13.2")
}
