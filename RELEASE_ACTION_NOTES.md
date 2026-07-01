# Parental-Link v2.9.17

Focused quick-message refinement.

## Changes

- Parent Activity now uses compact quick-message rows:
  - `💬 Come to eat — Delivered`
  - `💬 Come to eat — Dismissed`
  - `💬 Come to eat — Snoozed`
  - `💬 Come to eat — Accepted` for full-screen OK
- Refreshing message status updates the same quick-message event row instead of creating a bulky chat line.
- Parent-to-child presets remain commands like `Come to eat`.
- Child-to-parent preset messages are now separate and more natural, including `🍽️ I am hungry`.
- No full chat added; quick messages remain lightweight.

## Version

- versionCode 20917
- versionName 2.9.17
- APP_VERSION 2.9.17

---

# Parental-Link v2.9.16

Focused quick-message update.

## Changed

- Kept chat lightweight: the parent UI is now **Quick messages**, not a full chat screen.
- Added quick-message modes:
  - **Small banner for 5 minutes**
  - **Full-screen attention**
  - **Full-screen and block device**
- Small banner messages appear near the top-right of the child screen.
- Banner messages include:
  - **Dismiss**
  - **Snooze 5 min**
- Child action is saved so the parent can press **Refresh message status** and see what the child pressed.
- Full-screen attention messages show the selected emoticon large.
- Blocking attention messages also set a child lock state so the device can stay blocked until the parent clears restrictions.
- Existing preset emoticon messages remain:
  - 🍽️ Come to eat
  - ⏱️ 5 minutes
  - 🤫 Don't yell
  - 🧹 Tidy up
  - 🛁 Bath time
  - 😴 Bedtime
  - 📞 Call me
- Added custom emoji quick message flow.

## Still future backlog

- Full live chat thread
- Automatic parent notification when child dismisses/snoozes a message
- Per-message read/delivery UI
- Screenshot
- Battery saver
- DND direct control
- Location update

## Version

- versionCode 20916
- versionName 2.9.16
- APP_VERSION 2.9.16
