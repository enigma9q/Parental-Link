# Parental-Link v2.9.19

Bug-fix build after v2.9.18 UI correction.

## Changes

- Back button behaviour changed:
  - from child/parent sub-screens, Back returns to the relevant dashboard instead of closing the app.
  - on parent dashboard or child dashboard, Back requires a second press within a short window to leave the app.
- Tapping the blue `PL` mark in the top bar returns to the dashboard.
- Swipe panel behaviour corrected:
  - right-to-left swipe opens Device actions from the right.
  - left-to-right swipe opens the main menu from the left.
- Child settings are now protected:
  - the child cannot unlock settings directly.
  - settings require either a valid one-time unlock code or the child master password.
  - a successful one-time code is consumed immediately.
- Parent QR scan is safer:
  - requests camera permission before starting scan.
  - QR scanner start is wrapped with a fallback error message.
  - parent can still enter the pairing code manually if scanner is unavailable.

## Version

- versionCode: 20919
- versionName: 2.9.19
- APP_VERSION: 2.9.19

## Checks

- Static Java brace/string scan passed.
- Version consistency passed.
- Gradle build not run locally because Gradle is not installed in this environment.
