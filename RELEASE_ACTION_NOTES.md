# Parental-Link v3.0.0

First Kotlin/Compose parent dashboard build.

## Scope

- Keeps Java services, protocol, storage, child service, parent monitor and legacy screens.
- Replaces the parent dashboard surface with a Kotlin/Compose dashboard.
- Other screens still use LegacyMainActivity Java screens.
- Core controls only: Block/Enable, Timeout/Stop, Sound, Ring, Requests, Activity, Devices, Interface.

## Changes

- Enabled Jetpack Compose in Gradle.
- Added Compose dependencies through the Compose BOM.
- MainActivity.kt now overrides the parent dashboard only.
- LegacyMainActivity remains as the core Java implementation and exposes safe bridge methods for Compose.
- Version bumped to 3.0.0 / versionCode 30000.

## Notes

This is the first Compose migration step. If it builds, parent dashboard UI iteration should continue in Kotlin/Compose instead of Java.
