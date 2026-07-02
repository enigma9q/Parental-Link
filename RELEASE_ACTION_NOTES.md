# Parental-Link v3.2.10

Foreground service timeout crash fix.

## Crash diagnosis

The v3.2.9 crash log points to:

- `ParentMonitorService`
- `ForegroundServiceDidNotStopInTimeException`
- foreground service type: `dataSync`

This means Android 16 killed the app because the foreground service did not stop within the platform timeout.

## Fixed / changed

- `ParentMonitorService` now returns `START_NOT_STICKY`.
- `ParentMonitorService` stops foreground mode and calls `stopSelf(startId)` after a short diagnostic notification window.
- Parent monitor notification is no longer ongoing.
- Automatic parent monitor starts from diagnostic/dashboard paths are disabled where present.

## Preserved

- Safe Java dashboard remains default.
- `Open Compose dashboard test` remains available.
- Crash log exporter remains available from start screen and safe dashboard.
- Child pairing/link state fixes remain.
- Child removal authorisation flow remains.
- Reinitialise pairing remains.

## Version

- `versionCode 32010`
- `versionName 3.2.10`
- `APP_VERSION 3.2.10`
