# Parental-Link v3.2.6

Final rescue/diagnostic build before possible rewrite.

## Changed

- Dashboard no longer uses Compose.
- `AppEntryActivity.showConnectedParent()` now routes to Java safe diagnostic dashboard.
- Java safe dashboard includes:
  - Test connection
  - Devices
  - Repair pairing
  - Menu
  - Show crash log
  - Clear crash log

## Crash log export

Added internal crash logging:

- `files/parental-link-crash-log.txt`

Crash log can be opened/copied from:

- Start screen
- Java safe dashboard

## Child pairing recovery

Added near the child QR/pairing screen:

- `Reinitialise pairing`

Use this if the first parent connection crashed and the child cannot be paired again.

## Next steps after installing

1. Open app.
2. If it crashes, reopen.
3. Tap `Show crash log` on the start screen.
4. Copy crash log.
5. Send it back.
6. If crash log is empty, test pairing, master password, Java safe dashboard, Test connection, Devices.
7. If any step crashes, reopen and send the crash log.

## Rewrite preparation

Added:

- `ABILITY_INVENTORY_BEFORE_REWRITE.md`

## Version

- `versionCode 32006`
- `versionName 3.2.6`
- `APP_VERSION 3.2.6`
