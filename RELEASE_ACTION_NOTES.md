# Parental-Link v3.2.14

Manual build workflow and signing backlog.

## Fixed / changed

- Added the Gradle wrapper so local builds can use the same Gradle version as GitHub Actions.
- Added `.gitignore` rules for local Android Studio, Gradle, SDK, build, and signing files.
- GitHub Actions no longer creates tags or GitHub Releases automatically on push.
- GitHub Actions now runs manually and uploads the built APK as an artifact.
- Current release signing is kept temporarily so APKs remain update-compatible on existing devices.
- Added a backlog item to rotate the signing signature before final/public release.
- Removed the Kotlin/Compose dashboard path; current prototype work is Java-native.

## Version

- `versionCode 32014`
- `versionName 3.2.14`
- `APP_VERSION 3.2.14`
