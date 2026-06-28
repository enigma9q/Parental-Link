Parental-Link v2.1

Changes in v2.1:
- Renamed visible app labels to Parental-Link.
- Added Blocked apps screen with refresh and unblock-by-package support.
- Added one-time unlock code generation from the parent app.
- Child settings can be unlocked with device master password or one-time parent code.
- Child settings screen now shows connection info and required rights checklist.
- Added protected Remove app flow with unlock-method attribution.
- Child pairing QR is smaller.
- Child app only shows Restart server, not Stop server.
- Parent messages, lock timeout, and app blocked events can open a full-screen attention page on the child device.
- Ping remains full screen and cannot be dismissed with Back; Stop ping button is available.
- Version bumped to 2.1 / protocol 9.

Notes:
- Full APK build still needs GitHub Actions or Android Studio.
- Device Owner is not used; this remains Device Administrator level.
