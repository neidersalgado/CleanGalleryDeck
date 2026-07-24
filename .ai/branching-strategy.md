# Branching Strategy

## Model: GitHub Flow (Simplified)

A lightweight, professional branching model suitable for personal and small team projects.

## Branches

| Branch | Purpose | Source | Lifecycle |
|--------|---------|--------|-----------|
| `main` | Production-ready code | — | Permanent |
| `develop` | Integration branch | `main` | Permanent |
| `feature/*` | New features | `develop` | Ephemeral |
| `fix/*` | Bug fixes | `develop` | Ephemeral |
| `release/*` | Release candidates | `develop` | Ephemeral |

## Workflow

```mermaid
gitGraph
  commit
  branch develop
  checkout develop
  branch feature/amazing-feature
  commit
  commit
  checkout develop
  merge feature/amazing-feature
  checkout main
  merge develop tag: v1.0.0
```

### Feature Development

```bash
git checkout develop
git pull
git checkout -b feature/my-feature
# ... work, commit, lint, test ...
git push -u origin feature/my-feature
# Open PR → develop
```

### Hotfix

```bash
git checkout main
git checkout -b fix/critical-bug
# ... fix, commit, lint, test ...
git push -u origin fix/critical-bug
# Open PR → main (then backport to develop)
```

## Pre-Push Quality Gates

Every push triggers automatically:

1. **ktlintCheck** — Kotlin code style verification
2. **Test** — JUnit 5 test execution

Configured via `.lefthook.yml` (local) and `.github/workflows/ci.yml` (remote).

## Commit Conventions

Use conventional commits:

```
feat: add gallery card component
fix: resolve deck shuffle ordering
chore: update dependencies
docs: update branching strategy
style: format code per ktlint rules
test: add unit tests for deck model
```

## PR Checklist

Before opening a PR:

- [ ] `./gradlew ktlintCheck` passes
- [ ] `./gradlew test` passes
- [ ] Branch is up to date with target
- [ ] Commits follow conventional format
- [ ] No debug/console leftovers in production code

## Security

| Layer | Mechanism |
|-------|-----------|
| **Branch protection** | PR required, 1 approval, status checks, no force push |
| **CODEOWNERS** | `@neidersalgado` owns all code |
| **Dependabot** | Weekly dependency updates (Gradle + Actions) |
| **Signed commits** | SSH signing with personal key |
| **Pre-push hooks** | ktlintCheck + test (Lefthook) |
| **CI/CD** | GitHub Actions (lint + test + build) |

### Branch Protection Setup (GitHub Web)

1. Go to `Settings → Branches → Add rule`
2. Pattern: `main`
3. Enable:
   - ✅ Require a pull request before merging
   - ✅ Require approvals (1)
   - ✅ Dismiss stale reviews
   - ✅ Require status checks (lint, test)
   - ✅ Do not allow bypassing
   - ✅ Block force pushes
4. Repeat for `develop`

## Tools

| Tool | Purpose |
|------|---------|
| **ktlint** | Kotlin linter (via Gradle plugin) |
| **JUnit 5** | Test framework |
| **Lefthook** | Local git hooks manager |
| **GitHub Actions** | Remote CI pipeline |
| **Dependabot** | Automated dependency updates |
| **SSH signing** | Commit/tag verification |