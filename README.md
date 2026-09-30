# Direct for Android

A native Android app that opens a WhatsApp click-to-chat link. Enter the phone number (country code preselected as Pakistan), optionally add a message, and tap **Choose WhatsApp account**. Android shows an app picker on every send so you can choose WhatsApp, WhatsApp Business, or a Vivo clone if it appears. Sending remains a manual action in WhatsApp.

## Open in Android Studio

Open this folder as a project. It uses Android Gradle Plugin 9.1.1, Gradle 9.3.1, JDK 17, and Android SDK 36. Android Studio will download the Gradle and Android build components if needed. No third-party app libraries are used.

GitHub Actions builds a debug APK whenever you push to `main` or `master`. Download it from the workflow run under the artifact name `direct-whatsapp-debug-apk` (ZIP). To build locally in Android Studio, choose **Build > Build APK(s)**; the APK appears under `app/build/outputs/apk/debug/`.

## Privacy

The app has no internet permission, analytics, database, or local storage. It passes the number and optional draft to the `wa.me` URL so Android can open the associated app or browser. WhatsApp services handle the conversation after that handoff. The app does not send messages automatically.



