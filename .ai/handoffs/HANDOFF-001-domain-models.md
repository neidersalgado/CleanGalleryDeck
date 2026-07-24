# HANDOFF-001: Domain Models — Sprint 1
**Status:** PENDING
**Date:** 2026-07-24
**Origin:** ARCHITECT
**Destination:** DEVELOPER (AI Agent)
**Priority:** HIGH
**Version:** 1
**Supersedes:** —

---

## 1. Context

Sprint 1 begins. The multi-module skeleton is stable (Sprint 0 complete). CI is green. All ADRs are accepted. The first code deliverable is the domain layer — pure Kotlin models, repository interfaces, and MediaSource contract in `:core:domain`.

Relevant docs:
- `.ai/RULES.md` — golden rules, DoD, concurrency, observability
- `.ai/CONTEXT.md` — module structure, dependency graph
- `.ai/PLANNING.md` — SDD screens, GAPs 1-10, NFR, legal
- `.ai/adr/ADR-0001` (modularization), `ADR-0004` (MVI), `ADR-0003` (MediaSource)

## 2. Completed Work

| # | Task | Status | Evidence |
|---|------|--------|----------|
| 1 | Multi-module Gradle skeleton | ✅ | `settings.gradle.kts` — 12 modules |
| 2 | CI/CD with lint + test + build | ✅ | `.github/workflows/ci.yml` |
| 3 | ADRs 0001-0006 | ✅ | `.ai/adr/ADR-0001` through `0006` |
| 4 | Network security config | ✅ | `res/xml/network_security_config.xml` |
| 5 | Executive Review corrections | ✅ | Applied in `PLANNING.md` |

## 3. Deliverable

### 3.1 Primary Objective
Implement domain models, repository interfaces, and MediaSource contract in `:core:domain` with TDD.

### 3.2 Scope
Create the following files in `:core/domain/src/main/java/com/deck/domain/`:

```
model/
  MediaItem.kt          — data class, URI as String, no Android deps
  MediaType.kt          — enum: IMAGE, VIDEO, BURST, RAW
  MediaSourceType.kt    — enum: LOCAL_IMAGES, LOCAL_VIDEOS, GOOGLE_PHOTOS
  MediaFilter.kt        — value object: album, dateRange, sourceTypes, mediaTypes
  Resource.kt           — sealed class: Success, Loading, Error variants
repository/
  MediaItemRepository.kt  — interface: getItems, delete, restore, observeChanges
  MediaSource.kt          — interface: isAvailable, queryItems, sourceType
```

Test files in `:core/domain/src/test/java/com/deck/domain/`:
```
model/
  MediaItemTest.kt
  MediaFilterTest.kt
  ResourceTest.kt
```

### 3.3 Acceptance Criteria
- [ ] All models compile in `:core:domain` with zero Android dependencies.
- [ ] `MediaItem.uri` is `String`, not `android.net.Uri`.
- [ ] `Resource` sealed class has `Success<T>`, `Loading<T?>` (nullable previous), and typed `Error` variants.
- [ ] `MediaSource` interface defines `suspend fun isAvailable(context: Context): Boolean` and `suspend fun queryItems(context: Context, filter: MediaFilter): Flow<List<MediaItem>>`.
- [ ] `MediaItemRepository` returns `Flow<Resource<List<MediaItem>>>` for reactive observation.
- [ ] Tests: `./gradlew :core:domain:test` passes with >90% coverage.
- [ ] `./gradlew lint` passes with no warnings.
- [ ] No changes to `:core:data`, `:feature:*`, or `:media-sources:*`.

## 4. Dependencies

| Dependency | Available | Notes |
|------------|-----------|-------|
| Kotlin 2.1.0 | ✅ | In `libs.versions.toml` |
| JUnit 5 | ❌ | Not yet in `:core:domain/build.gradle.kts` — must add |
| MockK | ❌ | Not yet in `:core:domain/build.gradle.kts` — must add |
| Turbine | ❌ | Not yet in `:core:domain/build.gradle.kts` — must add |

Add to `:core:domain/build.gradle.kts`:
```kotlin
dependencies {
    // No Android or framework deps — pure Kotlin
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
    testImplementation("io.mockk:mockk:1.13.10")
    testImplementation("app.cash.turbine:turbine:1.0.0")
}
```

## 5. Constraints

- **Forbidden:** No `android.*` imports anywhere in `:core:domain`. No `android.net.Uri` — use `String`. No touching `:core:data`, `:feature:*`, `:media-sources:*`.
- **Must:** Use Kotlin stdlib only for production code. Follow `RULES.md` concurrency rules (suspend functions, Flow, no coroutine scopes). Use `@Inject` constructor annotations on interfaces that need Hilt binding later.
- **Testing:** TDD cycle: RED (test fails) → GREEN (implement) → REFACTOR. All tests in JUnit 5.

## 6. Risks

| Risk | Likelihood | Impact | Mitigation |
|------|------------|--------|------------|
| Accidental Android import | Medium | High | `./gradlew :core:domain:lint` catches this. Run before commit. |
| Circular dependency if MediaSource imports from data layer | Low | High | Enforced by module boundaries — `:core:domain` cannot see `:core:data`. |
| Over-engineering Resource wrapper | Medium | Low | Keep it sealed with 3 variants only (Success/Loading/Error). Add more later. |

## 7. Agent Instructions

1. Read `RULES.md`, `CONTEXT.md`, `PLANNING.md` (sections: Models, GAP 1-4, GAP 6).
2. Start with tests (TDD): `MediaItemTest`, `MediaFilterTest`, `ResourceTest`.
3. Implement models, then repository interfaces, then MediaSource contract.
4. Run `./gradlew :core:domain:test` after each green phase.
5. Run `./gradlew lint` before commit.
6. On completion, verify `./gradlew :core:domain:dependencies` shows zero Android deps.

## 8. Validation

- [x] Receiver has read full context (RULES + CONTEXT + PLANNING)
- [x] Branch confirmed: `develop`
- [x] Build passing before start (`./gradlew assembleDebug test lint`)
- [ ] Acceptance criteria met (section 3.3)
- [ ] HANDOFF-001 marked COMPLETED on finish

---

*Handoff v1 — next: HANDOFF-002 (Local Data Sources).*
