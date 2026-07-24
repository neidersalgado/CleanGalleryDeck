# Rules — CleanGalleryDeck

Golden rules, agent orchestration, and coding conventions.

## Golden Rules (Non-Negotiable)

1. NEVER upload photos/videos to external servers. Everything is LOCAL.
2. NEVER use `java.io.File` for deletion on Android 10+. Use `MediaStore.createDeleteRequest()`.
3. Google Photos: use Picker API (NOT Library API — deprecated for reading pre-existing photos).
4. Privacy: zero logs of URIs in production. Use EncryptedSharedPreferences for tokens.
5. Extensibility: all MediaSource implementations register via `@Binds @IntoSet` in Hilt.
6. No Android framework dependencies in Domain layer. Domain is pure Kotlin.
7. Implement tests FIRST (TDD: Red -> Green -> Refactor).

## Agent Initialization Order

When beginning a session, agent reads:
1. `RULES.md` (this file) — golden rules
2. `CONTEXT.md` — project overview, stack, build commands
3. `PLANNING.md` — roadmap, sprint status, ADRs
4. `KNOWLEDGE.md` — technical reference (Scoped Storage, Google APIs, permissions)

## Agent Priorities

1. Security & Privacy
2. Core functionality (Deck, swipe, delete)
3. Extensibility (MediaSource system)
4. Tests (unit, integration, UI)
5. UI/UX polish

## Coding Conventions

- Kotlin: follow official style guide
- Naming: classes PascalCase, functions/methods camelCase, constants SCREAMING_SNAKE_CASE
- Compose: state hoisting, single source of truth, unidirectional data flow
- DI: constructor injection via Hilt, never field injection in ViewModels
- Tests: JUnit 5 + MockK for unit, Robolectric for integration, ComposeTestRule for UI
- Use Cases: single `operator fun invoke()` per class

## Architecture Rules

- Clean Architecture: Presentation -> Domain <- Data (dependency inversion)
- Domain has ZERO Android dependencies
- Data layer implements Domain interfaces via Hilt `@Binds`
- ViewModels use StateFlow, never MutableState exposed publicly
- Navigation via NavHost with string routes
