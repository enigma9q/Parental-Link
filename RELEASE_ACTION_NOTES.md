# Parental-Link v2.9.20

Bug-fix build.

- Fixed GitHub Actions compile failure by removing unsupported `IntentIntegrator.QR_CODE_TYPES` usage.
- QR scanner still launches safely; if unavailable, parent can enter the pairing code manually.
- Added Samsung-safe quick-message fallback for child devices:
  - banner mode uses overlay-style screen only when overlay permission is available;
  - otherwise it uses a high-priority child notification with Dismiss and Snooze 5 min actions;
  - full-screen and blocking messages still use the full-screen Activity path.
- Added `ChildQuickActionReceiver` to record notification Dismiss/Snooze responses for parent status refresh.
- Bumped version to 2.9.20 / 20920.

Checks: static Java scan, version consistency. Real Gradle build not run locally.
