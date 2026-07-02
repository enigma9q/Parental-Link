# Parental-Link ability inventory before possible rewrite

Version captured: 3.2.6

## Core architecture

- Local-only parent/child app.
- No cloud backend.
- One APK can act as parent or child depending on setup.
- Pairing uses local network, child IP address, pairing code and shared key.
- Parent commands are sent to child over local socket protocol.
- Child exposes local command server service.
- Parent monitors child through a monitor service.

## Pairing

1. Child starts in child mode.
2. Child displays QR code and pairing code.
3. QR payload includes pairing code and child IP.
4. Parent scans QR.
5. Parent connects directly to child IP.
6. Child returns key.
7. Parent saves child IP/key.
8. Parent shows master password step.
9. Parent shows paired screen.
10. Child pairing screen has a reinitialise pairing button for failed/stale first connections.

## Parent abilities

- Pair with child by QR.
- Pair with child by manual IP + code.
- Save child IP/key.
- Test connection using `hello`.
- Open device list.
- Repair pairing.
- Show menu.
- Show/copy/clear crash log.
- Legacy intended dashboard commands:
  - Block / Enable
  - Timeout / Stop timeout
  - Sound/volume
  - Ring
  - Requests
  - Activity
  - Devices
  - Interface/settings
  - More actions

## Child abilities

- Run child server service.
- Show QR/pairing code.
- Reinitialise pairing from QR screen.
- Accept pairing.
- Receive parent commands.
- Accessibility blocking service.
- Parent monitor status reports.
- Ask parent requests.
- Quick messages/prompts.
- Lock / timeout / enable/disable flows.
- Volume/sound handling.
- Ring/ping handling.

## Command protocol highlights

- `hello`
- `usage`
- `current_app`
- `volume`
- `disable_use`
- `enable_use`
- `lock_timeout`
- `stop_timeout`
- `snooze_ask_parent`
- `remove_all_limitations`
- `profile`
- `quick_chat`
- `chat`
- `chat_status`
- `ping`
- `blocked_apps`
- `block_app`
- `unblock_app`
- `list_apps`
- `set_language`
- `set_parent_password`
- `remove_parental_link`

## Recovery/security backlog

- Lost connection / possible reset recovery.
- Detect server reachable but auth/pairing mismatch.
- Offer retry, QR repair, local reset after master password/PIN.
- Never auto-reset silently.
- Log event.
- Master password set after pairing for future recovery.

## UI backlog

- Child first-start pairing screen cleanup:
  - if no saved parent, show only QR and pairing code
  - hide requests/buttons/activity/status frames
  - make screen scrollable
  - keep QR visible

## Known current problem

- Pairing works.
- Master password step works.
- Paired screen works.
- Devices screen works.
- Dashboard crashes.
- v3.2.6 routes dashboard to Java diagnostic screen and adds crash log export.
