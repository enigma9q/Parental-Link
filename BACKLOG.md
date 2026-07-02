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
