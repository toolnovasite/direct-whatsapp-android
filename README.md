# Direct Chat for Android

A native Android utility that opens a WhatsApp or WhatsApp Business chat without requiring you to save the phone number as a contact. Enter an international number, choose a country code, paste a number, or share a phone number/text into Direct Chat from another app. Choose the WhatsApp account and review/send the message inside that app. Direct Chat does not send messages on your behalf.

## Open in Android Studio

Open this folder as a project. It uses Android Gradle Plugin 9.1.1, Gradle 9.3.1, JDK 17, and Android SDK 36. Android Studio will download the Gradle and Android build components if needed. The app uses Google's libphonenumber library and Google Mobile Ads SDK for banner advertisements.

GitHub Actions builds a debug APK for pull requests and pushes to the configured branches. Download it from the workflow run under the artifact name `direct-whatsapp-debug-apk` (ZIP). To build locally in Android Studio, choose **Build > Build APK(s)**; the APK appears under `app/build/outputs/apk/debug/`.

### Keep test APK installs updateable

Android requires an update to use the same application ID and compatible signing certificate as the installed app. GitHub-hosted runners are fresh build environments, so their automatically generated debug signing key can change between runs unless one is supplied. The workflow supports a persistent development-only key using the repository Actions secret `DEBUG_KEYSTORE_BASE64`.

On your Windows development PC, copy the existing Android Studio debug keystore as Base64 to the clipboard in PowerShell:

```powershell
$keystore = Join-Path $env:USERPROFILE ".android\\debug.keystore"
if (-not (Test-Path $keystore)) { throw "Debug keystore not found. Open/run the project in Android Studio first." }
[Convert]::ToBase64String([IO.File]::ReadAllBytes($keystore)) | Set-Clipboard
```

Then open this repository on GitHub and go to **Settings → Secrets and variables → Actions → New repository secret**. Set the name to `DEBUG_KEYSTORE_BASE64` and paste the clipboard value as the secret. Do not put the keystore or its Base64 value in the repository or share it in chat. The workflow expects the standard Android Studio debug keystore alias and password (`androiddebugkey` / `android`). This debug key is for development APKs only, not Google Play release signing.

If the APK currently installed on the phone came from an earlier CI run signed with a different temporary key, Android may require a one-time uninstall before the first stable-key build can install. Uninstalling erases Direct Chat's local settings and saved numbers; preserve anything important first. After that one-time change, builds using the configured key should be able to update one another when the package ID and version code remain compatible.

## Privacy and legal pages

- Privacy Policy source: `docs/privacy-policy.html`
- Terms of Use source: `docs/terms.html`
- Developer contact: Abdul Sami — shining.8090@gmail.com

Optional local history is off by default. When enabled, the app stores up to 30 numbers in private app storage on this device; turning the setting off clears them. Direct Chat does not request or read call-log data. Users can type numbers, paste them after tapping Paste, or share numbers into the app. The Google Mobile Ads SDK requires internet access and may collect or share device/ad identifiers, IP address, interaction, and diagnostic data according to Google's disclosures. The app does not create user accounts or send WhatsApp messages automatically.

### Publish the policy URLs

The app links to:

- https://toolnovasite.github.io/direct-whatsapp-android/privacy-policy.html
- https://toolnovasite.github.io/direct-whatsapp-android/terms.html

After this change is merged, enable GitHub Pages for this repository using **Settings → Pages → Deploy from a branch → main → /docs** (or the GitHub Actions source if configured). Verify both links load publicly before entering the Privacy Policy URL in Play Console. Keep the policy and Play Console Data safety answers aligned with the exact SDK version and app behavior being released.
