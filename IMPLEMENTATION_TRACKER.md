# Parental-Link implementation tracker

Version: 3.2.11

## Pairing change

- Child QR now includes both pairing code and child IP:
  - `family-link://pair?code=<code>&ip=<child-ip>`
- Parent QR scan parses the IP and starts pairing immediately.
- Pairing success now opens a stable success screen instead of forcing the dashboard immediately.

## Reason

The app opened after clearing app data, but crashed when connecting to the child. This build isolates pairing from dashboard launch.

## Launcher status

- Manifest launcher remains `.AppEntryActivity`.
- Parent biometric/PIN startup remains bypassed.


# Backlog

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


## v3.2.4

Dashboard crash isolation:

- Live dashboard replaced by safe dashboard shell.
- No automatic monitor startup on dashboard entry.
- No automatic current app/activity/status helper calls on first render.
- Explicit diagnostic buttons added.

Pairing flow:

- After successful pairing, show master password setup.
- Master password stored locally as `masterPassword` / `masterPasswordSet`.
- This is a placeholder for future repair/reset recovery logic.

## v3.2.5 backlog

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


## v3.2.6

Final rescue/diagnostic iteration before possible rewrite.

- Compose dashboard disabled.
- Java safe diagnostic dashboard added.
- Crash logger installed through `Thread.setDefaultUncaughtExceptionHandler`.
- Crash log stored as `files/parental-link-crash-log.txt`.
- Crash log reachable from start screen and Java safe dashboard.
- Child QR screen has `Reinitialise pairing` for failed/stale first connections.
- Ability inventory added before rewrite.


## v3.2.7

Compile fix after v3.2.6.

- Fixed Java lambda variable collision in `showChild()`.
- `Reinitialise pairing` is now added after `ChildHomeScreen.render(...)`, outside the nested lambda list.
- Crash-log exporter and Java safe dashboard remain unchanged.


## v3.2.8

Pairing and removal synchronisation.

- Child persists `childLinked=true` after successful PAIR.
- Child screen treats `childLinked` as connected.
- `remove_parental_link` no longer silently clears the child.
- Removal becomes pending and requires child-side authorisation.
- Parent no longer removes child locally immediately after sending removal.


## v3.2.9

Controlled dashboard test.

- Safe Java dashboard remains default.
- Added `Open Compose dashboard test`.
- Compose dashboard can now be tested manually without creating a startup crash loop.
- Removed/neutralised direct child-side stop-monitoring/remove wording.
- Child removal remains through authorised pending-removal flow.


## v3.2.10

Foreground-service timeout crash fix.

- Crash log showed `ParentMonitorService` foreground dataSync timeout.
- ParentMonitorService is now short-lived and non-sticky.
- It calls `stopForeground(STOP_FOREGROUND_REMOVE)` and `stopSelf(startId)`.
- Notification is no longer ongoing.
- Diagnostic/dashboard paths avoid automatic parent monitor service starts where present.


## v3.2.11

Parent monitor diagnostic hardening.

- Safe Java dashboard remains the default parent entry.
- Compose dashboard remains manual through `Open Compose dashboard test`.
- ParentMonitorService no longer enters foreground mode or declares `dataSync`.
- AppLog no longer auto-starts ParentMonitorService after every log write.
- Parent notification action callbacks no longer auto-start ParentMonitorService.

## v3.2.12

Java dashboard restoration and Compose crash guard.

- Parent launch returns to the styled Java dashboard instead of the diagnostic safe shell.
- Compose dashboard test no longer attaches ComposeView until the activity lifecycle migration is fixed.
- Parent action grid now matches the requested dashboard layout more closely with eight tiles and two custom slots.