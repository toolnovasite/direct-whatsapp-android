# Direct for Android

A native Android app that opens a WhatsApp click-to-chat link. Enter the phone number (country code preselected as Pakistan), optionally add a message, and tap **Open WhatsApp**. WhatsApp opens the conversation; sending remains a manual action in WhatsApp.

## Build

Open this folder in Android Studio, or push to GitHub to run the included Actions workflow. The workflow builds a debug APK and uploads it as a downloadable artifact for 14 days.

## Privacy

The app has no analytics, database, or local storage. It passes the number and optional draft to the `wa.me` URL so Android can open the associated app or browser. WhatsApp services handle the conversation after that handoff. The app does not send messages automatically.