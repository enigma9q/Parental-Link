# Parental-Link v3.2.7

Compile fix for v3.2.6.

## Fixed

GitHub Actions failed at Java compilation:

- `variable v is already defined in method showChild()`

Cause:

- The `Reinitialise pairing` button was injected inside an existing child-screen lambda that already used `v` as the lambda parameter.

Fix:

- Moved the `Reinitialise pairing` button creation outside the nested lambda.
- Used a unique lambda parameter name: `reinitClick`.
- Kept the crash-log exporter and Java safe dashboard from v3.2.6.

## Preserved from v3.2.6

- Dashboard routes to Java safe diagnostic screen.
- Start screen has `Show crash log`.
- Java safe dashboard has `Show crash log`.
- Internal crash log file:
  - `files/parental-link-crash-log.txt`
- Child QR screen has `Reinitialise pairing`.
- Ability inventory before rewrite remains.

## Version

- `versionCode 32007`
- `versionName 3.2.7`
- `APP_VERSION 3.2.7`
