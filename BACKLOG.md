# Backlog

## Before finalisation: rotate signing signature

The current release keystore/signing signature is kept temporarily so GitHub-built APKs remain installable as updates on existing devices.

Before the app is treated as final/public:

1. Create a new private release keystore outside the repository.
2. Remove the old tracked keystore from Git history or move to a fresh repository if history rewrite is too risky.
3. Move release signing to local/CI secrets only.
4. Publish a clear migration plan, because Android treats a changed signing key as a different update trust chain.

## UI and protocol follow-ups

- High: redesign the chat screen as a proper message list. Each message row should show three compact action icons on the right, and tapping the message should open the action choices we decide for that message.
- High validation: verify child active-time accounting on real devices after the active-usage gate. The child now reports foreground app time only while the device is actively usable, but it still needs overnight, lock-screen, timeout/block, launcher/home, and Samsung-tablet checks before v1.
- High: rename Blocked apps to Manage apps and replace the split blocked/full lists with one app policy list:
  - app icon and app name
  - app state icon: free use, daily app limit, profile allowed, or blocked
  - temporary timeout icon
  - block/unblock icon
  - per-app maximum daily time, separate from temporary timeout and hard block
  - remove blocked apps from the old standalone list model
- App icons: design a protocol for the child app to send compressed app icons or icon hashes to parent devices.
- Parent-to-parent sync: implement real multi-parent command/status mirroring after the child storage model supports multiple parent keys.

## Done in v0.9.3: usage-time accuracy foundation

The dashboard/device card now reports daily active app time from the child usage response instead of an older unlock/session timestamp.

Target behavior:

1. Count only foreground time for real child apps.
2. Exclude Parental-Link itself.
3. Exclude time spent on the launcher/home screen.
4. Exclude locked/attention/permission/setup screens.
5. Show the corrected daily active time on the dashboard device card.
6. Add the missing timer/active-time indicator back into the device card.
7. Keep app-level usage sorted from the same corrected source, so dashboard totals and per-app rows agree.

Implementation notes:

- Prefer UsageStats foreground intervals where available.
- Keep a fallback path for devices where Usage Access is missing, but report that state clearly instead of showing misleading totals.
- Treat launcher packages as non-usage time. If Parental-Link becomes the launcher, exclude both the Parental-Link package and any configured home packages.

## Launcher mode

Add an optional child launcher mode where Parental-Link can be selected as the device Home app.

Target behavior:

1. The child app can register a launcher/home activity.
2. Parent can enable "Use Parental-Link as child launcher" from device settings.
3. Child home shows only allowed apps by default.
4. Existing usage/accessibility blocking remains as backup if an app opens outside the launcher.
5. Advanced kiosk/device-owner lock task mode remains a later stronger option, not required for the first launcher release.

Launcher UI:

1. Desktop/home screen with app icons.
2. App drawer with two display modes:
   - Icon grid.
   - List with icon, name, and usage.
3. Sorting options:
   - Name.
   - Usage.
   - Type/category.
4. Group apps by type/category where possible:
   - Games.
   - Education.
   - Video.
   - Social.
   - Browser.
   - Tools.
   - Unknown/Other.
5. Desktop shortcuts:
   - Parental-Link settings icon.
   - Chat icon.
   - Ask parent/request icon.
6. Parent controls:
   - Choose visible apps.
   - Choose pinned desktop apps.
   - Choose default drawer style.
   - Hide apps by category.
   - Optionally show daily usage next to app names.
7. Later: add profile-related launcher screens for homework, bedtime, travel, free time, and focus desktop modes.

Suggestions to evaluate:

- Add a "Focus desktop" profile that only shows parent-approved apps for homework/sleep/travel modes.
- Add visual app groups as folders, generated automatically from category but editable by parent.
- Add a "recently used" row only when allowed by parent.
- Add a safe search/browser section later, separate from launcher basics.
- Add a clear escape path for parent unlock/settings, protected by master password/biometrics.
