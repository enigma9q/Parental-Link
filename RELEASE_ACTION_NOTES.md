# Parental-Link v3.2.1

Structural duplicate-class avoidance build.

## Why

v3.2.0 still failed in GitHub Actions because the repository retained stale `MainActivity` files. D8 reported:

- `Type com.enigma.familylinklite.MainActivity is defined multiple times`

## Changed

- Added new Kotlin launcher:
  - `AppEntryActivity.kt`
- Manifest now launches:
  - `.AppEntryActivity`
- The app no longer relies on the class name `MainActivity`.
- Startup still bypasses parent biometric/PIN lock.
- Parent dashboard remains Compose.

## Intention

This build should avoid duplicate-class failures even if stale `MainActivity.java` or `MainActivity.kt` remains in the GitHub repository temporarily.

## Version

- `versionCode 32001`
- `versionName 3.2.1`
- `APP_VERSION 3.2.1`
