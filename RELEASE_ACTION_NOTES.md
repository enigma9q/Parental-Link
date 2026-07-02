# Parental-Link v3.2.12

Java dashboard restoration and Compose crash guard.

## Crash diagnosis

The v3.2.11 crash log shows `ViewTreeLifecycleOwner not found` when opening the manual Compose dashboard test from the safe Java dashboard. The parent monitor crash is no longer the active failure; the remaining crash is the ComposeView lifecycle bridge.

## Fixed / changed

- Parent launch now opens the styled Java dashboard again instead of the diagnostic safe shell.
- `Open Compose dashboard test` no longer attaches a ComposeView in this repair build.
- Java dashboard action grid now uses the target eight-slot shape: enable/block, timeout, two custom action slots, media volume, ring device, chat, and more.
- Version bumped to 3.2.12 for a distinct GitHub Action APK.

## Preserved

- ParentMonitorService remains non-foreground and short-lived.
- Crash log access remains available from the start screen.
- Pairing, removal authorisation, and command handling are unchanged.
- Kotlin/Compose files remain in the project for a later proper lifecycle migration.

## Version

- `versionCode 32012`
- `versionName 3.2.12`
- `APP_VERSION 3.2.12`