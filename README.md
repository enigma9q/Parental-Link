# FamilyLink Lite v1.8

Safe architecture refactor step.

Changes:
- Added `updates/UpdateChecker` and moved GitHub release checking out of `MainActivity`.
- Added `devices/DeviceProfile` for child nickname/icon/IP and parent identity access.
- Kept UI and behaviour unchanged.
- Bumped version to 1.8 / versionCode 18.


## v1.9
- Moved pairing code, pairing ID, and AES-GCM helpers into `security/CryptoUtils`.
- Child service now reads version/protocol/capabilities from `core/AppConfig`.
- Updated app version to 1.9 / versionCode 19.
