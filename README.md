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
