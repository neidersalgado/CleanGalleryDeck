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
1. `RULES.md` (this file) — golden rules, handoff lifecycle
2. `CONTEXT.md` — project overview, stack, build commands, ADRs
3. `STANDARDS.md` — production-grade coding standards (Kotlin, Compose, MVI, accessibility)
4. `PLANNING.md` — roadmap, sprint status, ADRs, feature flags, concurrency
5. `KNOWLEDGE.md` — technical reference (Scoped Storage, Google APIs, permissions, patterns)

## Agent Priorities

1. Security & Privacy
2. Core functionality (Deck, swipe, delete)
3. Extensibility (MediaSource system)
4. Tests (unit, integration, UI)
5. UI/UX polish

## Coding Conventions

See `.ai/STANDARDS.md` for the full production-grade coding standards with correct/incorrect examples.

Key highlights:
- Kotlin: no `!!`, prefer `val` + `data class`, sealed classes for state/events
- Compose: state hoisting, `@Immutable` state, `collectAsStateWithLifecycle()`
- DI: constructor injection via Hilt, never field injection in ViewModels
- Tests: JUnit 5 + MockK for unit, Robolectric for integration, ComposeTestRule for UI
- Use Cases: single `operator fun invoke()` per class
- Naming: classes PascalCase, functions camelCase, constants SCREAMING_SNAKE_CASE
- Accessibility: contentDescription mandatory, touch targets >= 48dp
- Performance: key in LazyColumn, derivedStateOf, remember lambdas

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

## Handoff Lifecycle

Agents receive work via handoff documents in `.ai/handoffs/`. Rules:

1. **Start**: Read the handoff. Check section 8 (Validation). If any item is unchecked, resolve before starting.
2. **Execute**: Set status to IN_PROGRESS. Work scope is strictly what section 3 defines.
3. **Forbidden scope**: If a handoff says "do not touch X", touching X is a violation.
4. **Completion**: When all acceptance criteria (section 3.3) are met, set status to COMPLETED. If blocked, set CANCELLED and create a new handoff describing the blocker.
5. **Index**: After status change, update `.ai/handoffs/INDEX.md`.
6. **Snapshot**: On COMPLETED, create or update `.ai/snapshots/latest-snapshot.md`.
7. **Limit**: An agent should work on exactly one IN_PROGRESS handoff at a time.

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
