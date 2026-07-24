# CleanGalleryDeck

A modern Android application for browsing and managing media galleries from multiple sources, built with **Jetpack Compose** and a **multi-module Clean Architecture**.

## Architecture

Multi-module project following Clean Architecture principles:

```
:app                          → Main application entry point
:core:common                  → Shared utilities and base classes
:core:domain                  → Business logic, use cases, repository interfaces
:core:data                    → Data layer implementations (repositories, data sources)
:core:analytics               → Analytics tracking
:core:notification            → Notification handling
:feature:deck                 → Main gallery deck screen (Compose UI)
:feature:settings             → Settings screen
:feature:player               → Media player screen
:media-sources:media-source-api       → Media source abstraction API
:media-sources:source-google-photos   → Google Photos integration
:media-sources:source-local-images    → Local image files
:media-sources:source-local-videos    → Local video files
```

## Tech Stack

| Component | Version |
|---|---|
| Gradle | 8.13 |
| Android Gradle Plugin | 8.13.2 |
| Kotlin | 2.1.0 |
| Compose BOM | 2024.12.01 |
| Hilt (DI) | 2.52 |
| Coil (image loading) | 2.7.0 |
| Navigation Compose | 2.8.5 |
| Lifecycle | 2.8.7 |
| Core KTX | 1.15.0 |
| Min SDK / Target SDK | 24 / 35 |
| Java | 17 |

All dependencies are centralized in `gradle/libs.versions.toml` (version catalog).

## Setup

1. **Clone the repository**

   ```bash
   git clone git@github.com:neidersalgado/CleanGalleryDeck.git
   cd CleanGalleryDeck
   ```

2. **Set the Android SDK path** (create `local.properties`)

   ```properties
   sdk.dir=/path/to/Android/sdk
   ```

3. **Build**

   ```bash
   ./gradlew assembleDebug
   ```

4. **Run tests**

   ```bash
   ./gradlew test
   ```

5. **Lint check**

   ```bash
   ./gradlew lint
   ```

## CI

GitHub Actions workflow (`.github/workflows/ci.yml`) runs on every push to `develop` and `main`:

- `lint` — static analysis
- `test` — unit tests
- `build` — assemble debug APK

## Development

- Branch from `develop`
- Open a PR into `develop` for feature work
- Releases flow `develop → main`

## License

MIT