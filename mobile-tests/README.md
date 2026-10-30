# Mobile Test Suite (Appium 2 + UiAutomator2)

This directory contains automated regression tests for Android mobile applications using Appium and the Page Object Model (POM).

## Target Application

- Application: [Simplify Money Track (v1.0)](https://github.com/Jerry-LiL/simplify-money-track/releases/tag/v1.0)
- Package: `com.vineet.simplifytrack`
- Main Activity: `com.vineet.simplifytrack.MainActivity`
- Architecture: React Native Android app with native accessibility IDs and UiAutomator locators

## Continuous Integration Policy

> **Note**: This suite is intended for local execution against an Android emulator or connected device and is **not part of the CI pipeline**. Standard headless CI runners lack KVM hardware acceleration required for smooth Android emulator execution.
> 
> When executed in environments where no Appium server is active, tests gracefully skip with an informative reason (`pytest.skip`).

## Prerequisites

1. **Java JDK 17+** and **Android SDK Command-line Tools** (`$ANDROID_HOME` configured).
2. **Node.js 22.5+** and **Appium 2**:
   ```bash
   npm install -g appium
   appium driver install uiautomator2
   ```
3. **Android Emulator**:
   - Create an AVD via Android Studio (API 30+ recommended, e.g., Pixel 6 API 33).
   - Start the emulator: `emulator -avd <avd_name>`
4. **Application APK**:
   - Download the release APK (do not commit APKs to git):
     ```bash
     gh release download v1.0 -R Jerry-LiL/simplify-money-track
     adb install app-release.apk
     ```

## Local Execution

1. Start the Appium server on default port 4723:
   ```bash
   appium
   ```

2. Run the mobile test suite using pytest:
   ```bash
   pytest mobile-tests/
   ```

3. To inspect test collection without running an emulator:
   ```bash
   pytest --collect-only mobile-tests/
   ```
