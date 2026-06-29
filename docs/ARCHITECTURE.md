# Parental-Link architecture

This refactor is intentionally gradual. The package name is still `com.enigma.familylinklite` for install/update compatibility, but visible app naming is Parental-Link.

Current package split:

- `core`: constants and app-wide configuration.
- `storage`: saved connection data and logs.
- `protocol`: protocol/version metadata and command packet creation.
- `network`: parent command client.
- `security`: crypto helpers.
- `services`: child server, parent monitor, and accessibility blocking service.
- `updates`: version comparison/update helper logic.
- `ui`: shared UI construction helpers.
- `ui/screens`: screen-level UI builders moved out of `MainActivity`.
- `calls`: parent/child call UI groundwork.
- `audio`: child audio-message UI and recording scaffold.
- `devices`: device profile display helpers.

Backlog notes:

1. Continue moving screens from `MainActivity` into dedicated UI screen classes.
2. Move pairing flow into a dedicated pairing controller.
3. Keep six separate digit boxes for pairing; paste support is intentionally not planned.
4. Move dashboard command wiring into a parent dashboard controller.
5. Wire parent-child live audio calls through the secure local channel.
6. Wire child audio-message delivery to the parent device.
7. Add tests for protocol packet creation and version compatibility.
