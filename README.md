# FamilyLink Lite v0.5

Private Android sideload project for parent phone + child tablet.

## Main changes in v0.5
- Keeps a shared signing key in the project so future APKs can update over the previous version.
- Parent QR camera scan added.
- Manual connection improved:
  - parent detects Wi-Fi prefix, e.g. `192.168.1.`
  - enter only the tablet IP last number and the pairing secret shown on the child tablet
  - full pairing-code paste is still available
- Child tablet shows QR, full code, pairing secret, IP prefix and IP last number.
- Saved parent connection loads directly on app start.
- Parent connected screen has status header, refresh, add-device button, screen-time summary, top apps and commands.
- Child discovery broadcast every 60 seconds, with startup burst at 0s / 5s / 15s.
- Parent listens for saved child broadcasts and updates the child IP automatically when it changes.
- Child blocks a bad-handshake source IP for 60 seconds.

## Build with GitHub Actions
Use the existing `.github/workflows/build-apk.yml` file. If missing, create it and run `gradle assembleDebug` at the repo root.

## Install note
v0.5 includes a project signing key. If Android still says package conflict, uninstall the previous app once, then install v0.5. Future v0.5+ builds should update normally as long as the included keystore remains unchanged.
