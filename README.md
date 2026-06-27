# FamilyLink Lite v0.1

Private Android sideload prototype for parent phone + child tablet.

## Current features
- One APK with two modes: Parent phone / Child tablet
- Local Wi-Fi TCP connection only
- Encrypted commands using AES-GCM and a random pairing key
- Pairing code generated on child tablet
- Parent can send a message
- Parent can ping the tablet until acknowledged/stopped
- Parent can mute media volume or set media volume to 30% / 70%
- Parent can request today's app usage report
- Device Admin receiver scaffold included

## Not yet implemented
- Camera QR scanning. The pairing code is shown as text for v0.1; QR generation/scanning is the next step.
- Full Device Owner setup and strict app blocking.
- Timers per app.
- Foreground service for always-on operation after reboot.
- Better UI.

## Build
Open this folder in Android Studio and let Gradle sync. Build APK from:
Build > Build App Bundle(s) / APK(s) > Build APK(s)

## Permissions to enable on child tablet
1. Open Child tablet mode.
2. Tap "Open Usage Access settings" and allow FamilyLink Lite.
3. Optional: tap "Enable device admin".

## Use
1. Install the same APK on both devices.
2. On tablet, choose Child tablet.
3. Copy the pairing code.
4. On phone, choose Parent phone and paste the pairing code.
5. Use the buttons to send commands.

## Security model
- Commands are encrypted with AES-GCM.
- The pairing key is generated on the tablet.
- The app only listens on the local network port 45454.
- Anyone with the pairing code can control the child app, so keep it private.
