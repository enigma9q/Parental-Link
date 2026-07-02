# Parental-Link implementation tracker

Version: 3.2.1

## Structural decision

The launcher activity is now a new unique Kotlin class:

- `app/src/main/kotlin/com/enigma/familylinklite/AppEntryActivity.kt`

The Android manifest points to:

- `.AppEntryActivity`

## Why

GitHub still compiled stale `MainActivity` files after v3.2.0:

- stale Java `MainActivity.java`
- Kotlin `MainActivity.kt`
- ComposeMainActivity.kt

This caused duplicate `com.enigma.familylinklite.MainActivity` at D8.

## v3.2.1 rule

Do not use the class name `MainActivity` as the launcher anymore.

Old files may still exist temporarily, but they are no longer the launcher entry point.

## Current retained code

- `LegacyMainActivity.java` remains as backend/legacy method holder.
- `ComposeMainActivity.kt` may remain for compatibility with old Java `MainActivity.java`.
- `AppEntryActivity.kt` is the real launcher.
- Parent biometric/PIN lock is bypassed for now.
- Parent dashboard remains Compose.

## Next cleanup after build succeeds

1. Delete old Java `MainActivity.java`.
2. Delete or merge old `ComposeMainActivity.kt`.
3. Continue migrating Java screens one by one to Kotlin/Compose.
