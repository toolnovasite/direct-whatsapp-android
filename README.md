# Direct for Android

A native Android app that opens a WhatsApp click-to-chat link. Enter the phone number (country code preselected as Pakistan), optionally add a message, and tap **Choose WhatsApp account**. Android shows an app picker on every send so you can choose WhatsApp, WhatsApp Business, or a Vivo clone if it appears. Sending remains a manual action in WhatsApp.

## Open in Android Studio

Open this folder as a project. It uses Android Gradle Plugin 9.1.1, Gradle 9.3.1, JDK 17, and Android SDK 36. Android Studio will download the Gradle and Android build components if needed. The app uses Google's libphonenumber library and Google Mobile Ads SDK for banner advertisements.

GitHub Actions builds a debug APK whenever you push to `main` or `master`. Download it from the workflow run under the artifact name `direct-whatsapp-debug-apk` (ZIP). To build locally in Android Studio, choose **Build > Build APK(s)**; the APK appears under `app/build/outputs/apk/debug/`.

## Privacy and legal pages

- Privacy Policy source: `docs/privacy-policy.html`
- Terms of Use source: `docs/terms.html`
- Developer contact: Abdul Sami — shining.8090@gmail.com

The app can save up to 30 recent numbers locally only when the optional Save recent numbers switch is enabled. It reads call-log entries only when the user opens Recent calls and grants permission; the app does not upload the call log. The Google Mobile Ads SDK requires internet access and may collect or share device/ad identifiers, IP address, interaction, and diagnostic data according to Google's disclosures. The app does not create user accounts or send WhatsApp messages automatically.

### Publish the policy URLs

The app links to:

- https://toolnovasite.github.io/direct-whatsapp-android/privacy-policy.html
- https://toolnovasite.github.io/direct-whatsapp-android/terms.html

After this change is merged, enable GitHub Pages for this repository using **Settings → Pages → Deploy from a branch → main → /docs** (or the GitHub Actions source if configured). Verify both links load publicly before entering the Privacy Policy URL in Play Console. Keep the policy and Play Console Data safety answers aligned with the exact SDK version and app behavior being released.
