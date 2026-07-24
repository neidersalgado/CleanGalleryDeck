# CleanGalleryDeck — Project Snapshot

> Generated: 2026-07-24
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

Currently a **Kotlin/JVM** scaffold project. Planned migration to **Android with Jetpack Compose**.

---

## 2. REPOSITORY STRUCTURE

```
CleanGalleryDeck/
├── .ai/
│   ├── context.md              # AI agent context (stack, conventions, author)
│   ├── branching-strategy.md   # GitHub Flow + security policies
│   └── snapshot.md             # THIS FILE — full project snapshot
├── .github/
│   ├── workflows/ci.yml        # CI: lint (ktlint) + test + build
│   ├── CODEOWNERS              # @neidersalgado owns all code
│   └── dependabot.yml          # Weekly updates for Gradle + Actions
├── .lefthook.yml               # Pre-push hooks: ktlintCheck + test
├── build.gradle.kts            # Kotlin JVM 2.2.21 (TO BE REPLACED)
├── settings.gradle.kts
├── gradle.properties
├── gradlew / gradlew.bat
├── LICENSE                     # MIT
└── src/main/kotlin/.../
    └── Main.kt                 # Scaffold (fun main)
```

---

## 3. ENVIRONMENT

### Local Machine

| Tool | Version |
|------|---------|
| **OS** | macOS Darwin 25.0 (x86_64) |
| **Shell** | /bin/zsh |
| **Java** | Temurin-25.0.1+8 LTS |
| **Gradle** | 9.0.0 (Kotlin 2.2.0) |
| **Git** | 2.51.0 |
| **Lefthook** | 1.10.4 |
| **gh CLI** | Authenticated as `neidersalgadoy` |

### Git Configuration (Global)

| Setting | Value |
|---------|-------|
| `user.name` | neidersalgado |
| `user.email` | hsneider.salgado@hotmail.com |
| `init.defaultBranch` | main |
| `gpg.format` | ssh |
| `user.signingkey` | `~/.ssh/id_ed25519_neidersalgado_personal.pub` |
| `commit.gpgsign` | true |

### SSH Configuration

Two GitHub identities via `~/.ssh/config`:

| Host Alias | Key | GitHub Account |
|------------|-----|----------------|
| `github.com` | `~/.ssh/id_ed25519` | neidersalgadoy (work) |
| `github.com-neidersalgado` | `~/.ssh/id_ed25519_neidersalgado_personal` | neidersalgado (personal) |

Remote URL: `git@github.com-neidersalgado:neidersalgado/CleanGalleryDeck.git`

---

## 4. IMPLEMENTED SECURITY & QUALITY

| Layer | Mechanism | Status |
|-------|-----------|--------|
| **Branch protection** | Rulesets on `main` + `develop` | ✅ Active |
| **Protected branches** | PR required, 1 approval, no force push | ✅ Active |
| **Required status checks** | `lint`, `test` must pass before merge | ✅ Active |
| **Signed commits** | SSH signing with personal key | ✅ Active |
| **CODEOWNERS** | `.github/CODEOWNERS` → `@neidersalgado` | ✅ Active |
| **Dependabot** | Weekly scans for Gradle + Actions | ✅ Active |
| **Pre-push hooks** | Lefthook runs `ktlintCheck` + `test` | ✅ Active |
| **CI pipeline** | GitHub Actions: lint → test → build | ✅ Active |
| **AI context** | `.ai/` folder for any agent | ✅ Active |

---

## 5. BRANCHING STRATEGY

Model: **GitHub Flow**

```
main ────── merge (PR) ──────────────────
  └── develop ── merge (PR) ──
       ├── feature/*  → PR → develop
       ├── fix/*      → PR → develop
       └── release/*  → PR → main
```

Every push triggers:
1. Lefthook (local): `ktlintCheck` + `test`
2. GitHub Actions (remote): `lint` + `test` + `build`

---

## 6. COMMITS (Git Log)

```
1d1f69c docs: add security section to branching strategy  [signed]
ff4c13d chore: add branching strategy, CI, linting, and pre-push hooks [signed]
df87fc7 chore: initial project setup with AI context              [unsigned]
```

---

## 7. PENDING / NEXT STEPS

### Immediate
- [ ] **Migrate from Kotlin/JVM to Android**
  - Add Android plugins, `app/` module, Compose setup
  - Build config for Hilt, Coil, Material3
  - Package: `com.deck.clean`

### Planned Architecture
```
app/ (Android Application)
├── data/        ← Repositories, DataSources, DTOs
├── domain/      ← UseCases, Models, Repository interfaces
├── ui/          ← Compose screens, ViewModels, Navigation
└── di/          ← Hilt modules
```

---

## 8. USEFUL COMMANDS

```bash
# Lint
./gradlew ktlintCheck

# Test
./gradlew test

# Build
./gradlew build

# Pre-push check (manual)
./gradlew ktlintCheck test

# Git with signing
git commit -S -m "message"
git log --show-signature

# SSH test
ssh -T git@github.com-neidersalgado
```

---

## 9. CONTACT

- **GitHub**: https://github.com/neidersalgado
- **Repo**: https://github.com/neidersalgado/CleanGalleryDeck
- **Email**: hsneider.salgado@hotmail.com

---

*End of Snapshot — Pass this file to any AI agent for full project context.*