# Stop Pubs Android App

This repository contains the Android source code for the Stop Pubs app.

## Features

- rewarded AdMob flow
- 30-minute local protection window
- hide app ads while protection is active
- timer and state persistence using SharedPreferences

## Project structure

- `app/src/main/java/com/blockpubs/app/MainActivity.kt`
- `app/src/main/java/com/blockpubs/app/ProtectionManager.kt`
- `app/src/main/AndroidManifest.xml`

## Notes

This is a local protection flow for the app itself. It does not block ads in other applications because Android does not allow a standard app to intercept all system traffic without a VPN/rooted environment.

## Build

Open the project in Android Studio and sync Gradle.
