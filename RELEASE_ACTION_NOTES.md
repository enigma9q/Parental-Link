# Parental-Link v3.1.0

Kotlin-screen migration start and biometric crash isolation build.

## Purpose

The app reportedly crashes after biometric unlock. This build stops using the old Java parent lock screen and moves the parent lock flow into Kotlin/Compose.

## Migrated to Kotlin/Compose

- Parent app lock screen.
- Parent PIN creation screen.
- Parent PIN pad screen.
- Biometric unlock result handling.
- Parent dashboard remains Compose.

## Still Java-backed

The Kotlin screens still call existing Java backend methods for:

- biometric launch
- unlock state reset
- wrong PIN attempt registration
- dashboard commands
- monitor refresh
- device actions

## Implementation tracking

Added:

- `IMPLEMENTATION_TRACKER.md`

This file records which screens are Kotlin and which are still Java-screen based.

## Build settings kept

- AndroidX enabled.
- Java/Kotlin JVM target aligned to 17.
- Gradle/D8 heap increased.
- Worker pressure reduced.
- Release workflow uses clean no-daemon build.

## Version

- `versionCode 31000`
- `versionName 3.1.0`
- `APP_VERSION 3.1.0`
