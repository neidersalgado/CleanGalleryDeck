# Contributing to CleanGalleryDeck

## Quick Start

```bash
git clone git@github.com:neidersalgado/CleanGalleryDeck.git
cd CleanGalleryDeck
./gradlew assembleDebug   # verify build
./gradlew test            # verify tests
./gradlew lint            # verify lint
```

Pre-push hooks run `test` + `lint` automatically via Lefthook.

## Branch Strategy

- `develop` — integration branch. All PRs merge here.
- `main` — production releases. Merged from `develop`.
- `feature/*` — feature branches off `develop`.

## Commit Convention

```
<type>: <description>

- <detail>
- <detail>
```

Types: `feat`, `fix`, `chore`, `docs`, `refactor`, `test`, `perf`, `security`

## Code Style

- Kotlin: follow official style guide (`.editorconfig` enforced by Spotless/ktlint).
- Classes: PascalCase. Functions: camelCase. Constants: SCREAMING_SNAKE_CASE.
- Compose: state hoisting, single source of truth, unidirectional data flow.
- DI: constructor injection via Hilt. No field injection in ViewModels.
- Domain: pure Kotlin. No Android imports. No `android.net.Uri` — use `String`.
- Tests: TDD (Red → Green → Refactor). JUnit 5 + MockK for unit, Robolectric for integration, ComposeTestRule for UI.

## Architecture Rules

See `.ai/RULES.md` and `.ai/adr/` for detailed decisions:
- `ADR-0001`: Multi-module structure
- `ADR-0002`: Compose-only UI
- `ADR-0003`: @IntoSet for MediaSource
- `ADR-0004`: MVI state management
- `ADR-0005`: NavHost routes
- `ADR-0006`: Observability abstraction

## Definition of Done

A task is complete only when ALL apply:
1. Code follows golden rules in `RULES.md`.
2. Unit tests pass with >80% coverage on domain logic.
3. No prohibited dependencies introduced.
4. `assembleDebug` + `test` + `lint` pass.
5. CI green on push.
6. `.ai/` updated if context changed.

## Pull Request Process

1. Open PR from `feature/*` into `develop`.
2. Title follows commit convention.
3. Description includes: What, Why, Testing.
4. CI must be green.
5. At least one review before merge (squash merge preferred).

## Reporting Issues

Report bugs at [github.com/neidersalgado/CleanGalleryDeck/issues](https://github.com/neidersalgado/CleanGalleryDeck/issues).

## License

MIT — see `LICENSE`.
