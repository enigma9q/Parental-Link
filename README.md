# FamilyLink Lite v0.4

Private Android sideload app for a parent phone and child tablet on the same Wi-Fi.

## v0.4 changes
- Separate first-run function selection from saved connected screen.
- If a parent connection is saved, the app opens directly to the connected screen.
- Connected/disconnected header with light green/red status.
- Refresh button next to disconnected/connected status.
- `+` button in the connected screen to add/replace a child device.
- Child UDP presence broadcast: immediate, after 5 seconds, after 15 seconds, then every 60 seconds.
- Parent listens for child broadcasts and reconnects automatically when back on the same Wi-Fi.
- Bad encrypted handshake from an IP blocks that IP for 60 seconds.
- Screen time section shows today's total as HH:MM and top 3 apps.
- Commands section contains volume up/down/mute, message, ping, stop ping, refresh screen time.

## Build
Use GitHub Actions or Android Studio. Build task: `gradle assembleDebug`.
