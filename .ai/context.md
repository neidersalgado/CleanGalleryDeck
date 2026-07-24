# CleanGalleryDeck

Clean architecture Android app for gallery/deck media management.

## Stack

- **Language:** Kotlin 2.1.0
- **UI:** Jetpack Compose (Material 3) BOM 2024.12.01
- **Architecture:** Clean Architecture + MVVM
- **DI:** Dagger Hilt 2.52
- **Images:** Coil 2.7.0
- **Gradle:** 8.10.2 | **AGP:** 8.7.3
- **Min SDK:** API 24 | **Target:** API 35

## Modules

- `:app` - Application entry point
- `:core:*` - Common, Domain, Data, Analytics, Notification
- `:feature:*` - Deck, Settings, Player
- `:media-sources:*` - Extensible media source system (API, Local Images, Local Videos, Google Photos)

## Branching

See [branching-strategy.md](branching-strategy.md)

## CI/CD

- **Local hook:** Lefthook runs lint + test on pre-push
- **Remote CI:** GitHub Actions (lint, test, build)

## Author

Neider Salgado - @neidersalgado