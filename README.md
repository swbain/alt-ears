# Alt Ears

A Kotlin Multiplatform mobile app for managing your Big Ears Festival schedule.

## Features

- **Artists tab**: Browse all festival artists with photos
- **All Shows tab**: View complete schedule grouped by day
- **My Schedule tab**: Your personalized schedule
- Add/remove shows with a single tap
- Offline caching - data persists locally
- Pull-to-refresh for latest updates

## Tech Stack

- **Kotlin Multiplatform** - Shared business logic
- **Compose Multiplatform** - Shared UI
- **SQLDelight** - Local database (works on both iOS and Android)
- **Ktor** - HTTP client
- **Koin** - Dependency injection
- **Coil** - Image loading

## Project Structure

```
alt-ears/
├── shared/                 # Shared KMP module
│   └── src/
│       ├── commonMain/     # Shared Kotlin code
│       ├── androidMain/    # Android-specific code
│       └── iosMain/        # iOS-specific code
├── androidApp/             # Android app entry point
└── iosApp/                 # iOS app entry point (SwiftUI wrapper)
```

## Building

### Android

```bash
./gradlew :androidApp:assembleDebug
```

### iOS

Open `iosApp/iosApp.xcodeproj` in Xcode and build.

## API

Fetches festival data from `https://api.tmsqr.app/api/v1/festival/455/dashboard`

## License

MIT
