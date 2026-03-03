# Ridango Assignment - News API

An Android news reader app built with Jetpack Compose and Material3 that fetches top headlines from [newsapi.org](https://newsapi.org).

## Architecture

MVVM with manual dependency injection (ServiceLocator pattern).

```
com.example.ridangoassignmentnewsapi/
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

The app reads the News API key from `local.properties` (gitignored) via `BuildConfig`:

1. Open `local.properties` in the project root
2. Add: `NEWS_API_KEY=your_api_key_here`
3. Get a free key at [newsapi.org/register](https://newsapi.org/register)

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

## Features

- **Adaptive grid layout**: 2 columns in portrait, 3 in landscape
- **Infinite scroll pagination**: Loads more articles as you scroll
- **Article detail screen**: Full article view with hero image, metadata, and content
- **Open in Browser**: Opens the full article in your browser
- **Save with Protocol Buffers**: Serializes article data using protobuf and logs to Logcat (mock API)
- **Error handling**: Error states with retry functionality

## Known Limitations

- The free tier of newsapi.org truncates article content (visible as `[+NNN chars]` - these are stripped in the app)
- Articles marked as `[Removed]` by newsapi.org are filtered out
- The "Save" feature uses a mock API (logs protobuf data to Logcat) rather than persisting to a real backend
