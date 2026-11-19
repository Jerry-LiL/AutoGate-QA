# Mobile Tests

Appium regression tests for Android (`com.vineet.simplifytrack`).

Tests run locally against an emulator or connected device; tests skip automatically when the Appium server is offline.

## Setup

1. Install Appium 2 and UiAutomator2 driver:
   ```bash
   npm install -g appium
   appium driver install uiautomator2
   ```
2. Start the Android emulator and install the target APK:
   ```bash
   adb install app-release.apk
   ```
3. Start Appium on port 4723:
   ```bash
   appium
   ```

## Running

```bash
pytest mobile-tests/
```

To list tests without running them:
```bash
pytest --collect-only mobile-tests/
```
