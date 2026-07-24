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
- ADRs in `.ai/adr/` override general rules when conflicting

## Concurrency Rules

- All coroutines launch in ViewModel scope (`viewModelScope`).
- UseCases are `suspend fun` — no coroutine scope management in use cases.
- Repository/DataSource functions are `suspend fun` — never expose `Job` or `Deferred`.
- Dispatchers: IO for disk/network, Default for CPU, Main for UI.
- Structured concurrency: use `coroutineScope` or `supervisorScope` — never `GlobalScope`.
- Cancellation: all suspending calls respect cancellation (check `isActive` in loops).
- Never launch coroutines from Repository or UseCase constructors.
- Room/WorkManager already manage their own dispatchers — do not override.

## Domain Model Evolution

- Model entities as sealed classes or interfaces + implementations — NOT single class with boolean flags.
- Prefer composition over type flags: `MediaItem` has `source: MediaSourceType`, not `isLocal: Boolean`.
- New types extend the sealed hierarchy — existing code remains untouched.
- Example: `Image`, `Video`, `CloudPhoto` as subtypes where behavior differs.

## Observability Requirements

- AnalyticsService interface in `:core:domain`. Implementations in `:core:analytics`.
- CrashReporter interface in `:core:domain`. Implementations wrap Crashlytics.
- PerformanceTracer in all UseCases: `startTrace("delete_media")` + success/failure metric.
- No Firebase imports in domain or feature modules.
- Timber/Napier for debug logging (stripped in release builds by ProGuard).
- Never log URIs, file paths, or user-identifiable data — even in debug builds.

## Performance Requirements

- Gallery load < 2s for 1000 items: Paging 3 with `PagingSource` or LIMIT/OFFSET.
- Deletion < 500ms per file: `Dispatchers.IO`, non-blocking.
- Composables: `key()` in lists, `remember`/`derivedStateOf` for derived state.
- Coil disk cache + memory cache (20% of available RAM).
- Baseline Profiles: generate with Macrobenchmark before Play Store release.
- JankStats or FrameMetrics for 60fps verification.

## Definition of Done (DoD)

A task is complete only when ALL apply:
1. Code follows `RULES.md` golden rules and architecture rules.
2. Unit tests pass with >80% coverage on domain logic.
3. Integration tests (Robolectric) for data layer where applicable.
4. No prohibited dependencies introduced (verify with dependency-analysis plugin).
5. ADR created for any new architectural decision.
6. `.ai/` files updated if context, rules, or planning changed.
7. `assembleDebug` + `test` + `lint` pass locally.
8. CI green on push (pre-push hooks verify).
