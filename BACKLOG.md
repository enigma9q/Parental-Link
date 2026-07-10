# Backlog

## Before finalisation: rotate signing signature

The current release keystore/signing signature is kept temporarily so GitHub-built APKs remain installable as updates on existing devices.

Before the app is treated as final/public:

1. Create a new private release keystore outside the repository.
2. Remove the old tracked keystore from Git history or move to a fresh repository if history rewrite is too risky.
3. Move release signing to local/CI secrets only.
4. Publish a clear migration plan, because Android treats a changed signing key as a different update trust chain.

## Lost connection / possible reset recovery

### Scenario

- Parent loses connection to child, but the child IP still responds like a server.
- Child loses connection to parent, but parent/known pairing state no longer matches.
- This may mean the app was reset, reinstalled, data-cleared, or re-paired on one side.

### Problem

The app should not silently fail or keep showing only “offline” when the device is reachable but authentication/pairing does not match.

### Proposed solution

Detect this as a separate state:

```text
Device found, but pairing no longer matches.
The other side may have been reset.
```

Recovery actions:

1. Retry connection.
2. Repair pairing by QR.
3. Reset local pairing after master password/PIN.
4. On child side, require parent/master password before removing the old parent link.
5. Record the event in activity/history.

### Safety rule

Never auto-reset pairing silently.

A reset/repair must require one of:

- parent PIN
- master password
- existing authenticated parent session
- physical QR repair flow

### Implementation note

This should be implemented after pairing/dashboard stability is confirmed.

## HIGH: Child first-start pairing screen cleanup

### Scenario

- Child device starts before a parent has been saved or paired.
- The current child screen may show request/buttons/activity/status frames.
- These sections are irrelevant before a parent exists.
- On smaller screens/tablets, the QR code can be pushed down and become unreachable because the child screen is not scrollable.

### Required behaviour

If the child has no saved parent connection:

1. Show only the QR/pairing code section.
2. Hide request panels.
3. Hide action buttons.
4. Hide activity/status frames.
5. Make the screen scrollable.
6. Keep the QR visible and easy to scan.
7. Keep the pairing code visible as fallback.

### Priority

High.

### Implementation note

This should be implemented after v3.2.4 dashboard crash isolation is confirmed.

## UI and protocol follow-ups

- HIGH: Troubleshooting status icon polish:
  - Replace the thin/off-position status glyphs with clearer theme-matching icons.
  - Align status icons consistently at the right edge of each row.
  - Use a calmer visual treatment for OK, warning/needed, and unknown states.
- Current app metadata: add app category tags such as game, app, system, browser, education.
- App icons: design a protocol for the child app to send compressed app icons or icon hashes to parent devices.
- Parent dashboard stale status: when the parent app opens and the child has not connected directly in the current session, device-card fields such as battery, Wi-Fi, current app, readiness, and other live values should be shown as unavailable (`---` / `???`) instead of using old cached values.
- Parent-to-parent sync: confirm whether parent commands and child status are broadcast or mirrored to all paired parents.
- Add parent workflow: make parent 2 setup QR-based from parent 1, then let parent 2 and each child perform their own handshake.
- Devices screen: reorganize into clearer categories with list-style rows and dividers.
- Parent permanent notification: add a setting and service behavior for an always-visible parent monitor notification.
- Current app list refresh: verify newly installed apps after package changes and consider a child-side package-change receiver.
