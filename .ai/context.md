# CleanGalleryDeck

Clean architecture gallery/deck management application built with Kotlin.

## Stack

- **Language:** Kotlin 2.2.21 (JVM)
- **Build:** Gradle 9.0
- **Group:** `io.github.neidersalgado`

## Conventions

- Kotlin official code style
- Tests: JUnit 5 via `kotlin.test`
- Package structure: `io.github.neidersalgado.cleangallerydeck.*`

## Branching

See [branching-strategy.md](branching-strategy.md) — GitHub Flow with feature/fix/release branches.

## CI/CD

- **Local hook:** Lefthook runs `ktlintCheck` + `test` on pre-push
- **Remote CI:** GitHub Actions runs lint, test, build on push/PR

## Author

Neider Salgado — @neidersalgado