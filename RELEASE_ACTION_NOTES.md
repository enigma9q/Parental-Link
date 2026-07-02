# Parental-Link v3.2.0

Structural recovery build.

## Why

v3.1.2 still did not open. The Java Activity bridge branch is stopped.

## Changed

- Kotlin `MainActivity` is now the real manifest entry point.
- Removed Java `MainActivity.java` stub.
- Removed `ComposeMainActivity.kt`.
- Parent startup bypasses biometric/PIN lock for now.
- Parent dashboard remains Compose.
- Java services, protocol, storage, crypto and backend methods remain.

## Expected entry structure

Present:

- `app/src/main/kotlin/com/enigma/familylinklite/MainActivity.kt`
- `app/src/main/java/com/enigma/familylinklite/LegacyMainActivity.java`

Absent:

- `app/src/main/java/com/enigma/familylinklite/MainActivity.java`
- `app/src/main/kotlin/com/enigma/familylinklite/ComposeMainActivity.kt`

## Version

- `versionCode 32000`
- `versionName 3.2.0`
- `APP_VERSION 3.2.0`
