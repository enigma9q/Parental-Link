# Parental-Link v3.1.2

Recovery build after v3.1.1 failed to open.

## Purpose

v3.1.1 moved the parent lock/biometric screen to Kotlin/Compose. The app reportedly no longer opens.

This build bypasses the lock/biometric route on startup to isolate the crash.

## Changed

- If a parent connection exists, startup now opens the parent dashboard directly.
- Automatic biometric prompt launch is disabled.
- Existing PIN/biometric code remains present, but should not be entered automatically.
- Compose dashboard remains active.

## Not changed

- No command protocol changes.
- No service changes.
- No storage changes.
- No child-side changes.
- No new features.

## Version

- `versionCode 31002`
- `versionName 3.1.2`
- `APP_VERSION 3.1.2`
