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
| Java | 21 |

All dependencies in `gradle/libs.versions.toml`.

## Modules & Dependency Graph

`MediaSource` interface lives in `:core:domain`. Sources (`:media-sources:*`) implement it. `:core:data` orchestrates via `MediaSourceRegistry`.

```text
:core:domain           ← defines MediaSource (interface)
↑                      ↑
|                      |
:media-sources:api     ← contracts (deprecated — MediaSource is in domain)
:media-sources:google  ← implements MediaSource
:media-sources:local   ← implements MediaSource
↑                      ↑
|                      |
:core:data             ← uses MediaSourceRegistry (injects Set<MediaSource>)
```

| Module | Type | Layer |
|--------|------|-------|
| `:app` | application | Entry point, Hilt, NavHost |
| `:core:common` | library | Shared utilities |
| `:core:domain` | library | Entities, VOs, Repo interfaces, Use Cases, MediaSource |
| `:core:data` | library | Repo implementations, DataSources, DI |
| `:core:analytics` | library | Analytics events |
| `:core:notification` | library | Notifications |
| `:feature:deck` | library | DeckScreen, ViewModel |
| `:feature:settings` | library | Settings screen |
| `:feature:player` | library | Media player |
| `:media-sources:source-google-photos` | library | Google Photos integration (delayed to V2) |
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

GitHub Actions: `lint` → `test` → `dependencyCheck` → `assembleDebug` → upload APK.
Full workflow in `.github/workflows/ci.yml`.  
Pre-push hooks (Lefthook): `lint` + `test` in parallel.

## Observability Stack

| Service | Interface (domain) | Implementation | Bound In |
|---------|-------------------|----------------|----------|
| Analytics | `AnalyticsService` | Firebase Analytics | `:core:analytics` |
| Crash Reports | `CrashReporter` | Firebase Crashlytics | `:core:analytics` |
| Performance | `PerformanceTracer` | Firebase Performance | `:core:analytics` |
| Logging | Timber / Napier | Android Log (debug) | `:app` |

Domain code never imports Firebase. All observability goes through domain interfaces.

## Architecture Decision Records

Located in `.ai/adr/`:

| ADR | Title | Status |
|-----|-------|--------|
| 0001 | Multi-Module Clean Architecture | Accepted |
| 0002 | Compose-Only UI | Accepted |
| 0003 | @IntoSet for MediaSource | Accepted |
| 0004 | MVI with StateFlow | Accepted |
| 0005 | Type-Safe NavHost Routes | Accepted |
| 0006 | Observability — Analytics Abstraction | Accepted |

## Author

Neider Salgado — @neidersalgado
