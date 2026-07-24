# CleanGalleryDeck

Clean architecture Android app for gallery/deck media management.

## Stack

- **Language:** Kotlin 2.0.20
- **UI:** Jetpack Compose (Material 3)
- **Architecture:** Clean Architecture + MVVM
- **DI:** Dagger Hilt 2.51.1
- **Images:** Coil 3.0.0
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
- **Remote CI:** GitHub Actions

## Author

Neider Salgado - @neidersalgado
