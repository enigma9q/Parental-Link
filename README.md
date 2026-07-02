# Parental-Link v2.9.19

Parental-Link is a local Wi-Fi parental control app. This package is a UI correction build.

## Current focus

- Parent and child app dark interface alignment.
- Cleaner parent dashboard action cards.
- Simplified child interface.
- Correct `PL` mark.

See `RELEASE_ACTION_NOTES.md` for details.

## v2.9.23
Focused parent child-card polish only. See RELEASE_ACTION_NOTES.md.


## v2.9.24 migration note

The app now starts from a Kotlin `MainActivity` while the existing Java implementation is kept as `LegacyMainActivity`. This is a bridge for the upcoming Kotlin/Compose UI migration. Core services and command handling remain Java.

## v3.0.0 note

This version starts the Kotlin/Compose UI migration. The parent dashboard is now a Compose surface; the existing Java legacy screens and services remain in place.
