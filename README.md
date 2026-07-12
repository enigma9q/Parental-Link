# Parental-Link

Parental-Link is a local Wi-Fi Android parental-control app for parent and child devices on the same network. The active app is Java-native, with the old Kotlin/Compose dashboard path removed.

## Current Version

`1.0.0-rc1`

This is the first v1 release-candidate line. The goal is to stabilise pairing, parent/child notifications, launcher mode, child service survival, and active-time reporting before tagging a final v1.

## Current Focus

- Java-native parent dashboard polish.
- Fixed-size requests area with request count and swipe-friendly request handling.
- Dashboard action buttons and menus using one shared visual style.
- App/device menus split by responsibility.
- Parent-child local Wi-Fi command flow.
- Child-side unlock, timeout, quick message, blocked app, and permission workflows.
- Master/parent password entry using 4 to 8 numeric digits.

## Release Flow

Releases are produced from version tags such as:

```text
v1.0.0-rc1
```

When a version tag is pushed, GitHub Actions builds the release APK and publishes it to the matching GitHub release.

## Notes Before v1

See `BACKLOG.md` before treating this app as final. The signing key is still the temporary tracked key so current test devices can keep receiving installable updates; it must be rotated before a final/public release.
