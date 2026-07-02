# Parental-Link v2.9.23

Focused Java UI attempt for the parent dashboard child-status card only.

Changes:
- Rebuilt only the parent dashboard child card.
- More mockup-like structure:
  - circular child avatar on the left
  - child name and status pill on top
  - compact battery / Wi-Fi metrics row
  - current app row with subtitle
  - active state row with subtitle
- Current app display now hides package names where possible, e.g. `One UI Home` instead of `One UI Home (com.sec.android.app.launcher)`.
- Pairing repair remains available when mismatch is detected.

No changes:
- no child UI changes
- no protocol changes
- no notification changes
- no new features
- no dashboard action changes

Version:
- versionCode 20923
- versionName 2.9.23
- APP_VERSION 2.9.23

Checks:
- Java static scan passed
- version consistency passed
- real Gradle build not run locally in this environment
