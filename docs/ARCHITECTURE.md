# FamilyLink Lite architecture

This refactor is intentionally gradual. The app still keeps the same behaviour, but responsibilities are being separated into packages.

Current package split:

- `core`: constants and app-wide configuration.
- `storage`: saved connection data and logs.
- `protocol`: protocol/version metadata and command packet creation.
- `network`: parent command client.
- `services`: child server, parent monitor, and accessibility blocking service.
- `updates`: version comparison/update helper logic.
- `ui`: shared UI construction helpers.

Next intended steps:

1. Move small screens from `MainActivity` into dedicated UI screen classes.
2. Move pairing flow into a dedicated pairing controller.
3. Move dashboard command wiring into a parent dashboard controller.
4. Add tests for protocol packet creation and version compatibility.
