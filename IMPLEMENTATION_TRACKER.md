# Parental-Link implementation tracker

Version: 3.2.0

## Structural change

`MainActivity` is now Kotlin:

- `app/src/main/kotlin/com/enigma/familylinklite/MainActivity.kt`

Removed:

- `app/src/main/java/com/enigma/familylinklite/MainActivity.java`
- `app/src/main/kotlin/com/enigma/familylinklite/ComposeMainActivity.kt`

## Current architecture

### Kotlin / Compose

- `MainActivity.kt`
  - Real Android manifest entry point.
  - Parent dashboard surface.
  - Parent dashboard action routing into existing Java backend methods.

### Java retained as backend/legacy holder

- `LegacyMainActivity.java`
  - Still extends `Activity`.
  - Still contains old Java screens and backend methods.
  - Kotlin `MainActivity` extends it temporarily to reuse tested protocol/storage/command code.
  - No Java `MainActivity` stub remains.

### Java retained services/backend

- `ChildServerService`
- `ParentMonitorService`
- `BlockAccessibilityService`
- network/protocol
- storage
- crypto
- updater
- command/client logic

## Startup lock status

Parent biometric/PIN lock is bypassed in v3.2.0.

Reason:

- v3.1.1 and v3.1.2 failed to open after changing the lock route.
- This build focuses on making the app open with a Kotlin manifest entry point first.

## Migration rule from here

For each Java screen:

1. Create Kotlin/Compose screen.
2. Route to Kotlin screen.
3. Keep backend calls into Java classes/methods.
4. Remove or stop using the old Java screen.
5. Record the migration here.

## Next screens to migrate after app opens

1. Start role-selection screen.
2. Parent pairing screen.
3. Devices screen.
4. Interface/settings screen.
5. More actions menu.
6. Volume screen.
7. Timeout dialog.
8. Command history screen.
