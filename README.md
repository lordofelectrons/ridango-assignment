# Ridango Assignment - News API

An Android news reader app built with Jetpack Compose and Material3 that fetches top headlines from [newsapi.org](https://newsapi.org).

## Architecture

MVVM with manual dependency injection (ServiceLocator pattern).

```
com.example.ridangoassignmentnewsapi/
  data/local/           -- Disk cache (SharedPreferences)
  data/remote/          -- Retrofit API service + DTOs
  data/repository/      -- Repository interface + implementation
  di/                   -- ServiceLocator (manual DI)
  domain/model/         -- Domain Article model
  ui/screens/newslist/  -- Grid screen + ViewModel
  ui/screens/articledetail/ -- Detail screen + ViewModel
  ui/components/        -- ArticleCard, ErrorState, LoadingIndicator
  ui/navigation/        -- NavGraph + ArticleCache
  util/                 -- ArticleSerializer (protobuf)
```

## Setup

### API Key

A free-tier newsapi.org key is hardcoded in `app/build.gradle.kts` for convenience. No setup needed — clone and run.

### Build & Run

```bash
./gradlew assembleDebug
```

Install on a connected device/emulator via Android Studio or:

```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

### Run Tests

Unit tests:
```bash
./gradlew test
```

UI tests (requires emulator/device):
```bash
./gradlew connectedAndroidTest
```

## Tech Stack

| Category | Technology |
|---|---|
| Language & Platform | Kotlin, Android (minSdk 24, targetSdk 35, AGP 9.1.0) |
| UI | Jetpack Compose + Material3, Coil (image loading), Navigation Compose |
| Networking | Retrofit 2.11.0 + Gson converter, OkHttp 4.12.0 |
| Serialization | Protocol Buffers (protobuf-javalite 4.29.3, manual encoding) |
| Caching | SharedPreferences + Gson |
| Architecture | MVVM, manual DI (ServiceLocator), Coroutines / StateFlow |
| Testing | JUnit, Mockito-Kotlin, Compose UI Test, Espresso |
| CI | GitHub Actions (JDK 21, Gradle) |

## Features

- **Adaptive grid layout**: 2 columns in portrait, 3 in landscape
- **Infinite scroll pagination**: Automatically loads more articles as you scroll, with a loading indicator and "end of feed" message
- **Offline caching**: Previously loaded articles are persisted to disk and shown when the network is unavailable
- **Smart error handling**: Distinguishes between no internet, API rate limits, timeouts, and other errors — with retry via Snackbar
- **Article detail screen**: Full article view with hero image, metadata, and content
- **Open in Browser**: Opens the full article in your default browser
- **Save with Protocol Buffers**: Serializes article data using protobuf and logs to Logcat (mock API)
- **Scroll to top**: Floating action button to jump back to the top of the feed
- **Pull to refresh**: Toolbar refresh button reloads articles and scrolls to top
- **CI pipeline**: GitHub Actions workflow builds the APK and runs unit tests on every push/PR

## Known Limitations

- The free tier of newsapi.org truncates article content (visible as `[+NNN chars]` - these are stripped in the app)
- Articles marked as `[Removed]` by newsapi.org are filtered out
- The "Save" feature uses a mock API (logs protobuf data to Logcat) rather than persisting to a real backend
