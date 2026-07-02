# Parental-Link v3.2.11

Parent monitor diagnostic hardening.

## Audit result

- Safe Java dashboard remains the default parent entry.
- Compose dashboard still opens only through `Open Compose dashboard test`.
- Dashboard preparation does not auto-start `ParentMonitorService`.
- `AppLog.add()` was still indirectly starting `ParentMonitorService` after every log write.
- Parent notification action handling was still indirectly starting `ParentMonitorService`.

## Fixed / changed

- `ParentMonitorService` now posts a short normal diagnostic notification instead of entering foreground mode.
- Parent monitor manifest entry no longer declares `foregroundServiceType="dataSync"`.
- Log writes no longer auto-start the parent monitor.
- Parent notification action callbacks no longer auto-start the parent monitor.
- Manual troubleshooting restart still starts the short-lived diagnostic service.

## Preserved

- Safe Java dashboard remains default.
- `Open Compose dashboard test` remains available.
- Crash log exporter remains available from start screen and safe dashboard.
- Child pairing/link state fixes remain.
- Child removal authorisation flow remains.
- Reinitialise pairing remains.

## Version

- `versionCode 32011`
- `versionName 3.2.11`
- `APP_VERSION 3.2.11`