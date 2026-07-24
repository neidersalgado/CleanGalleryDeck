# CleanGalleryDeck — Project Snapshot

> Generated: 2026-07-24 (Updated)
> Purpose: Pass this to any AI agent (DeepSeek, Claude, OpenCode, etc.) for full context.

---

## 1. PROJECT OVERVIEW

| Field | Value |
|-------|-------|
| **Name** | CleanGalleryDeck |
| **Repository** | `github.com/neidersalgado/CleanGalleryDeck` (public) |
| **License** | MIT — Copyright 2026 Neider Salgado |
| **Author** | Neider Salgado (@neidersalgado) |
| **Email** | hsneider.salgado@hotmail.com |

### Current State

**Android multi-module project** with Clean Architecture, Jetpack Compose, Hilt, Coil. CI/CD with GitHub Actions. Compiles and builds successfully.

---

## 2. REPOSITORY STRUCTURE

```
CleanGalleryDeck/
├── .ai/
│   ├── context.md              # AI agent context (stack, conventions, author)
│   ├── branching-strategy.md   # GitHub Flow + security policies
│   ├── 01-planning.ai          # Product vision, sprints, risks
│   ├── 02-technical.ai         # Architecture, ADRs, models, APIs
│   └── snapshot.md             # THIS FILE
├── .github/
│   ├── workflows/ci.yml        # CI: lint + test + build (Android)
│   ├── CODEOWNERS              # @neidersalgado owns all code
│   └── dependabot.yml          # Weekly updates
├── .lefthook.yml               # Pre-push hooks: lint + test
├── app/                        # Android Application module
│   └── src/main/java/com/deck/clean/
│       ├── CleanGalleryApplication.kt  (#HiltAndroidApp)
│       ├── MainActivity.kt            (Compose + NavHost)
│       └── ui/theme/Theme.kt          (Material 3 dynamic color)
├── core/
│   ├── common/                 # Shared utilities
│   ├── domain/                 # UseCases, Models, Repository interfaces
│   ├── data/                   # Repository implementations
│   ├── analytics/              # Analytics abstraction
│   └── notification/           # Notification abstraction
├── feature/
│   ├── deck/                   # DeckScreen (swipeable card UI)
│   ├── settings/               # Settings screen
│   └── player/                 # Media player
├── media-sources/
│   ├── media-source-api/       # MediaSource interface
│   ├── source-local-images/    # Local image loader
│   ├── source-local-videos/    # Local video loader
│   └── source-google-photos/   # Google Photos integration
├── build.gradle.kts            # Root (plugin declarations)
├── settings.gradle.kts         # Multi-module includes
├── gradle.properties
└── gradle/libs.versions.toml   # Centralized version catalog
```

---

## 3. STACK

| Component | Version | Location |
|-----------|---------|----------|
| Gradle | 8.10.2 | `gradle/wrapper/gradle-wrapper.properties` |
| AGP | 8.7.3 | `libs.versions.toml` |
| Kotlin | 2.1.0 | `libs.versions.toml` |
| Compose BOM | 2024.12.01 | `libs.versions.toml` |
| Dagger Hilt | 2.52 | `libs.versions.toml` |
| Coil | 2.7.0 | `libs.versions.toml` |
| Min SDK | 24 | `app/build.gradle.kts` |
| Target SDK | 35 | `app/build.gradle.kts` |
| Compile SDK | 35 | `app/build.gradle.kts` |
| JDK | 21 (Temurin) | local |

---

## 4. ENVIRONMENT (Local Machine)

| Tool | Version |
|------|---------|
| **OS** | macOS Darwin 25.0 (x86_64) |
| **Shell** | /bin/zsh |
| **Java** | Temurin-25.0.1+8 LTS (default), Temurin-21.0.11 (for Android) |
| **Android SDK** | `~/Library/Android/sdk` (build-tools 36.0.0, platforms 35+36) |
| **Git** | 2.51.0 |
| **Lefthook** | 1.10.4 |
| **gh CLI** | Authenticated as `neidersalgadoy` |

### Git Configuration

| Setting | Value |
|---------|-------|
| `user.name` | neidersalgado |
| `user.email` | hsneider.salgado@hotmail.com |
| `init.defaultBranch` | main |
| `gpg.format` | ssh |
| `user.signingkey` | `~/.ssh/id_ed25519_neidersalgado_personal.pub` |
| `commit.gpgsign` | true |

### SSH Two Identities

| Host Alias | Key | GitHub Account |
|------------|-----|----------------|
| `github.com` | `~/.ssh/id_ed25519` | neidersalgadoy (work) |
| `github.com-neidersalgado` | `~/.ssh/id_ed25519_neidersalgado_personal` | neidersalgado (personal) |

Remote: `git@github.com-neidersalgado:neidersalgado/CleanGalleryDeck.git`

---

## 5. CURRENT BRANCH STATE

- **`main`**: Initial commits (branching strategy, CI setup, project scaffold)
- **`develop`**: Android multi-module migration, CI green, local build passing
- **CI**: GitHub Actions runs `lint` + `test` + `build` (all green)
- **Local build**: `./gradlew assembleDebug` passes with JDK 21

---

## 6. COMMANDS

```bash
# Build APK
JAVA_HOME=/Library/Java/JavaVirtualMachines/temurin-21.jdk/Contents/Home ./gradlew assembleDebug

# Lint
JAVA_HOME=/.../temurin-21.jdk/Contents/Home ./gradlew lint

# Test
JAVA_HOME=/.../temurin-21.jdk/Contents/Home ./gradlew test

# Full build + test
JAVA_HOME=/.../temurin-21.jdk/Contents/Home ./gradlew clean assembleDebug test

# Git with signing
git commit -S -m "message"

# Switch JDK
export JAVA_HOME=/Library/Java/JavaVirtualMachines/temurin-21.jdk/Contents/Home
```