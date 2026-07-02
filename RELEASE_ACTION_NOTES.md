# Parental-Link v3.2.3

Backlog-only update.

## Added backlog item

- Lost connection / possible reset recovery

## Summary

When one side loses connection but the IP still appears to host a Parental-Link server, the app should treat this as a possible reset/reinstall/data-clear/pairing mismatch, not simply offline.

Proposed recovery:

- detect “server reachable but pairing/auth mismatch”
- show a clear warning
- offer retry, QR repair, or local reset after master password/PIN
- never auto-reset silently
- log the event

## No functional code changes

This package only records the backlog item and bumps version metadata.

## Version

- `versionCode 32003`
- `versionName 3.2.3`
- `APP_VERSION 3.2.3`
