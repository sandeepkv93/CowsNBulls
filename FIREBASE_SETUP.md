# Firebase Setup Guide

To enable multiplayer "Play with Friend" mode, you need to set up Firebase Realtime Database.

## Prerequisites

- Google account
- Android Studio installed

## Step 1: Create Firebase Project

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Click "Add project"
3. Enter project name: `CowsNBulls` (or any name)
4. Disable Google Analytics (optional for this project)
5. Click "Create project"

## Step 2: Add Android App to Firebase

1. In Firebase Console, click "Add app" → Android icon
2. Enter package name: `com.example.cowsnbulls`
3. App nickname (optional): `Bulls & Cows`
4. Debug signing certificate SHA-1 (optional for development)
5. Click "Register app"

## Step 3: Download google-services.json

1. Download the `google-services.json` file
2. Move it to your project's `app/` directory:
   ```
   CowsNBulls/
   ├── app/
   │   ├── google-services.json  ← Place here
   │   ├── build.gradle.kts
   │   └── src/
   ```

## Step 4: Enable Realtime Database

1. In Firebase Console, go to **Build** → **Realtime Database**
2. Click "Create Database"
3. Choose location (e.g., `us-central1`)
4. Start in **Test mode** for development
   ```json
   {
     "rules": {
       ".read": "true",
       ".write": "true"
     }
   }
   ```
   ⚠️ **WARNING**: Test mode allows anyone to read/write. For production, use proper security rules.

## Step 5: Production Security Rules (Optional)

For production, update Realtime Database rules:

```json
{
  "rules": {
    "rooms": {
      "$roomCode": {
        ".read": "true",
        ".write": "!data.exists() || data.child('status').val() != 'FINISHED'"
      }
    }
  }
}
```

This allows:
- Anyone to read rooms (needed for joining)
- Anyone to create new rooms
- Only ongoing games can be modified (prevents tampering with finished games)

## Step 6: Build and Run

1. Sync Gradle files in Android Studio
2. Build the app: `./gradlew assembleDebug`
3. Run on device or emulator
4. Navigate to **Play with Friend** mode

## Troubleshooting

### "google-services.json not found"
- Ensure `google-services.json` is in `app/` directory (not `app/src/`)
- Sync Gradle files

### "Failed to create room" / "Failed to join room"
- Check internet connection
- Verify Realtime Database is enabled in Firebase Console
- Check Database rules allow read/write access

### Firebase dependencies not found
- Ensure `google-services` plugin is applied in `build.gradle.kts`
- Check that Firebase BOM version matches in `libs.versions.toml`

## Firebase Console Access

- Project Dashboard: https://console.firebase.google.com/
- Realtime Database: Go to **Build** → **Realtime Database** → **Data** tab
- Here you can see live game rooms being created and updated in real-time!

## Cost

Firebase Spark Plan (Free tier):
- ✅ 1GB storage
- ✅ 10GB/month bandwidth
- ✅ 100 simultaneous connections

Enough for thousands of game sessions per month!
