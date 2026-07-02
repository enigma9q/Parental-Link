# Parental-Link v3.0.4

Build-stability fix after v3.0.3.

## Fixed

GitHub Actions passed the duplicate MainActivity stage, but failed during DEX merge with:

- `D8: java.lang.OutOfMemoryError: Java heap space`
- `:app:mergeDexRelease FAILED`

This version increases Gradle/D8 memory and reduces worker pressure.

## Changes

- Added/updated `gradle.properties`:
  - `org.gradle.jvmargs=-Xmx4096m -XX:MaxMetaspaceSize=1024m -Dfile.encoding=UTF-8`
  - `org.gradle.workers.max=2`
  - `android.enableR8.fullMode=false`
  - keeps `android.useAndroidX=true`
- Updated release workflow build command:
  - `gradle clean assembleRelease --no-daemon --stacktrace`
- Bumped:
  - `versionCode 30004`
  - `versionName 3.0.4`
  - `APP_VERSION 3.0.4`

No feature or UI changes.
