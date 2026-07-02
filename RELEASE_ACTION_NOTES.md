# Parental-Link v3.2.9

Controlled Compose dashboard reintroduction and child-side removal cleanup.

## Dashboard crash testing

The safe Java dashboard remains the default.

Added button:

- `Open Compose dashboard test`

This manually opens the Compose dashboard. If it crashes, the app should reopen to the safe Java dashboard and the crash can be retrieved through:

- Start screen → Show crash log
- Safe Java dashboard → Show crash log

## Child app removal cleanup

Removed/neutralised direct child-side “stop monitoring / remove Parental-Link” UI wording.

Child removal should now go through the authorised removal flow:

1. Parent sends removal request.
2. Child receives removal pending state.
3. Child opens removal authorisation screen.
4. Child authorises with master password / parent password if set.
5. Or child chooses Keep monitoring.

## Preserved

- Safe Java dashboard
- Crash log exporter
- Start screen Show crash log
- Child pairing state fixes
- Child removal pending authorisation
- Reinitialise pairing

## Version

- `versionCode 32009`
- `versionName 3.2.9`
- `APP_VERSION 3.2.9`
