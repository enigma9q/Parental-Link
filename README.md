# Parental-Link

Parental-Link is a local Wi-Fi Android parental-control prototype. The app is currently in the `v0.9` rebuild phase: the Kotlin/Compose dashboard path has been removed, and the active UI is Java-native.

## Current Version

`0.9.1-prototype`

This is not a v1 release candidate yet. The goal of the current line is to rebuild the parent dashboard and supporting screens step by step while keeping the existing local-device control framework working.

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
v0.9.1-prototype
```

When a version tag is pushed, GitHub Actions builds the release APK and publishes it to the matching GitHub release.

## Notes Before v1

See `BACKLOG.md` before treating this app as final. The signing key is still the temporary tracked key so current test devices can keep receiving installable updates; it must be rotated before a final/public release.
