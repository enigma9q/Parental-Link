# Parental-Link v2.9.17

Quick-message activity/status refinement release.

Parental-Link v2.6.3

Changes:
- Sticky mute enforcement added for child volume streams.
- Parent mute command saves the previous child volume level.
- Unmute restores the previous level, capped by the parent maximum.
- Child-side enforcer re-applies mute if the child tries to remove it.
- Volume enforcement interval tightened to 2.5 seconds.
- Added capability flags: strict_volume_enforcement, sticky_mute_enforcement, mute_restore_previous_volume.

Notes:
- Android stream mute behaviour can vary by device/stream. The app also forces stream volume to 0 as a fallback.
- Gradle build not run in this runtime.

## v2.9.15 notes

- Ring and Ping are now one user-facing action: Ring device.
- Chat screen supports preset quick messages, typed parent messages, and manual chat-status refresh.
- Screenshot, Battery saver, DND and Location update remain future actions, not active shortcuts.

## v2.9.16 update

Quick messages now have two simple categories: small top-right banner prompts and full-screen attention prompts. Banner prompts include Dismiss and Snooze 5 min, and the child action is visible from Refresh message status. Full-screen prompts can also block the device.
