# Parental-Link implementation tracker

Version: 3.1.0

## Current architecture

### Kotlin / Compose screens

- `ComposeMainActivity.kt`
  - Parent app lock screen
  - Parent PIN creation screen
  - Parent PIN pad screen
  - Biometric result handling
  - Parent dashboard surface
  - Parent request card
  - Parent quick actions card
  - Parent activity card
  - Parent bottom navigation row

### Java entry / compatibility

- `MainActivity.java`
  - Thin manifest-compatible stub only.
  - No screen UI should be added here.
  - It extends `ComposeMainActivity`.

### Java legacy activity / backend bridge

- `LegacyMainActivity.java`
  - Still contains old Java screens.
  - Still owns most command handlers, dialogs, pairing, menus, child setup, devices, volume, timeout, app list and history screens.
  - Kotlin screens call Java methods for backend actions.

### Java services and backend code

Kept in Java:

- `ChildServerService`
- `ParentMonitorService`
- `BlockAccessibilityService`
- network/protocol classes
- storage classes
- crypto classes
- update checker
- UI helper classes until their screens are migrated

## Migration status

### Migrated to Kotlin/Compose in v3.1.0

- Parent app lock
- Parent PIN creation
- Parent PIN pad
- Biometric unlock result path
- Parent dashboard

### Still Java-screen based

- Start role-selection screen
- Parent pairing screen
- Child home screen
- Devices screen
- Interface/settings screen
- More actions menu
- Volume screen
- Timeout dialog
- Command history screen
- Blocked apps/list apps screens
- Language/profile/settings sub-screens
- Update/version screen if opened through legacy menu

## Rule from v3.1.0 onward

For every migrated screen:

1. Create the screen in Kotlin/Compose.
2. Keep calls to existing Java backend methods where possible.
3. Remove or stop routing to the old Java screen.
4. Record the migration here.
5. Do not add new features until crash source is isolated.

## Crash isolation note

Reported crash: after biometrics.

Mitigation in v3.1.0:

- Biometric lock screen and result handling moved into `ComposeMainActivity.kt`.
- Java lock UI is no longer used for the parent unlock surface.
- Existing Java backend method `unlockParentApp(...)` is still used to reset lock state and open dashboard.

If the crash continues after v3.1.0, the next step is to remove the Java Activity bridge and make Kotlin `MainActivity` the direct Android entry point.
