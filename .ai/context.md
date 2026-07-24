# CleanGalleryDeck

Android app for reviewing and cleaning media galleries via a Tinder-style swipeable deck.

## Tech Stack

| Component | Version |
|-----------|---------|
| Kotlin | 2.1.0 |
| AGP | 8.13.2 |
| Gradle | 8.13 |
| Compose BOM | 2024.12.01 |
| Hilt | 2.52 |
| Coil | 2.7.0 |
| Min/Target SDK | 24/35 |
| Java | 17 |

All dependencies in `gradle/libs.versions.toml`.

## Modules

| Module | Type | Layer |
|--------|------|-------|
| `:app` | application | Entry point, Hilt, NavHost |
| `:core:common` | library | Shared utilities |
| `:core:domain` | library | Entities, VOs, Repo interfaces, Use Cases |
| `:core:data` | library | Repo implementations, DataSources, DI |
| `:core:analytics` | library | Analytics events |
| `:core:notification` | library | Notifications |
| `:feature:deck` | library | DeckScreen, ViewModel |
| `:feature:settings` | library | Settings screen |
| `:feature:player` | library | Media player |
| `:media-sources:media-source-api` | library | MediaSource abstraction |
| `:media-sources:source-google-photos` | library | Google Photos integration |
| `:media-sources:source-local-images` | library | Local image files |
| `:media-sources:source-local-videos` | library | Local video files |

## Build

```bash
./gradlew assembleDebug   # Build debug APK
./gradlew test            # Run unit tests
./gradlew lint            # Static analysis
./gradlew build           # Full build + test + lint
```

## Branching

GitHub Flow: `feature/*` -> PR -> `develop` -> PR -> `main`.
Pre-push hook (Lefthook) runs `test` + `lint`.

## CI

GitHub Actions: `lint` -> `test` -> `build` on every push to `develop`/`main`.

## Author

Neider Salgado — @neidersalgado
