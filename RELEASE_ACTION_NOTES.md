# Release workflow

`.github/workflows/release-apk.yml` creates a GitHub Release only after `gradle assembleDebug --stacktrace` succeeds.

Triggers:
- manual run from Actions (`workflow_dispatch`)
- pushed tags matching `v*.*.*`

The release tag is generated from `app/build.gradle` `versionName`.
The APK is attached as `parental-link-v<version>-debug.apk`.
