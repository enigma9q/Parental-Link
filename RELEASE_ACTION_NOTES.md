# Parental-Link v3.1.1

Compile fix after v3.1.0.

## Fixed

GitHub Actions failed at Kotlin compile:

- `Unresolved reference: PillButton`
- Location: `ComposeMainActivity.kt`

This version replaces that missing composable call with a standard Material `Button`.

## No functional changes

No new screens, commands, services, protocol, storage or UI behaviour were changed beyond the compile fix.

## Version

- `versionCode 31001`
- `versionName 3.1.1`
- `APP_VERSION 3.1.1`
