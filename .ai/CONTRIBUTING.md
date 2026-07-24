# Contributing to .ai/ — CleanGalleryDeck

This folder is the **Project Adapter** — the bridge between the project and AI agents.

## File Structure

| File | Purpose | When to update |
|------|---------|----------------|
| `CONTEXT.md` | Project overview (stack, modules, build) | Dep change, new module, CI change |
| `RULES.md` | Golden rules, agent orchestration, conventions | New rule, priority change |
| `PLANNING.md` | Roadmap, sprints, ADRs, risks | Sprint start/end, new ADR |
| `KNOWLEDGE.md` | Technical reference pool | New API, pattern, or integration |
| `CONTRIBUTING.md` | This file. How to expand .ai/ | Structure changes |

## How to Add Knowledge

1. Open `KNOWLEDGE.md`
2. Add a new section with your topic
3. Include code examples, API levels, permission notes
4. Keep it concise (reference, not tutorial)

## How to Add an ADR

1. Open `PLANNING.md`
2. Add under the ADRs section with format:
   ```
   ### ADR-NNN: Title
   - Context: ...
   - Decision: ...
   - Rationale: ...
   ```

## How to Create a Snapshot

1. Create `snapshots/YYYY-MM-DD-snapshot.md`
2. Include: git log, file tree, build status, current sprint
3. Update `latest-snapshot.md` symlink or copy

## Handoff Protocol

### Lifecycle

```
PENDING → IN_PROGRESS → COMPLETED
                   ↘ CANCELLED
```

Each handoff has exactly one origin and one destination. Only the destination may set IN_PROGRESS. Only the origin (or Tech Lead) may set CANCELLED.

### Rules

1. **One deliverable per handoff** — a handoff covers exactly one atomic deliverable (e.g. "domain models", not "domain + data + UI").
2. **Versioned** — `HANDOFF-NNN.md` where NNN auto-increments. No overwrites.
3. **Controlled** — status transitions are explicit. No skipping states.
4. **Limited scope** — acceptance criteria must be achievable in ≤1 sprint.
5. **Validation gate** — section 8 must be checked before the destination starts working.
6. **Supersession** — if a handoff becomes obsolete, create a new one with `Supersedes: HANDOFF-NNN` in the header.
7. **Snapshots** — create a new snapshot when a handoff transitions to COMPLETED.

### Template

Copy `.ai/handoffs/TEMPLATE.md` to `.ai/handoffs/HANDOFF-NNN-description.md`.

### Index

Handoffs are listed in `.ai/handoffs/INDEX.md` with current status. When a handoff completes, update the index.

## Future Expansion (AEP)

When ready to expand to the full AI Engineering Platform, use this structure:

```
aep/
├── VERSION
├── control-plane/     (scheduler, router, policy-engine, budget, approval, session)
├── boot/              (BOOT.md, STARTUP.md, DISCOVERY.md, ROUTING.md, health)
├── context/           (memory/permanent, sessions, knowledge, project, user)
├── capabilities/      (git, github, filesystem, terminal, docker)
├── skills/            (create_feature, code_review, debug, refactor)
├── agents/            (architect, backend, frontend, devops, reviewer)
├── workflows/         (new_feature, bug_fix, migration, incident)
├── governance/        (rules/architecture, coding, security; ADRs)
├── specifications/    (architecture, API, business, security, UI, DB)
├── evaluation/        (quality, reasoning, hallucination, security)
├── observability/     (metrics, cost, latency, traces)
├── traces/            (task, agent, tool, mcp, tokens, latency, cost)
├── maintenance/       (health, audit, cleanup, repair, migration)
├── learning/          (experience, candidate-knowledge, validation)
├── context-loader/    (rules, selectors, compression, cache)
├── policy-engine/     (coding, security, privacy, performance)
├── knowledge-graph/   (nodes, relationships)
├── indexes/           (skills.json, agents.json, knowledge.json)
├── patterns/          (hexagonal, ddd, clean_architecture)
├── antipatterns/      (god_service, shared_state, n+1)
├── best-practices/    (kotlin, android, testing, security)
├── token-optimization/ (compression, summaries, lazy-loading)
├── budgets/           (per phase: planning, architecture, implementation)
├── contribution/      (CONTRIBUTING, STYLE_GUIDE, REVIEW_GUIDE, QUALITY_GATE)
├── playbooks/
├── benchmarks/
├── quality-gates/
├── risk/
├── compliance/
├── provenance/
├── cache/
├── registry/
├── telemetry/
├── CHANGELOG.md
└── MANIFEST.md
```

To expand:
1. Create `~/aep/` directory
2. Populate each subdirectory with markdown files as needed
3. Reference AEP from `KNOWLEDGE.md` and `RULES.md`
4. Agents will load AEP first, then `.ai/` for project-specific context

## Commit & PR Templates

### Commit
```
<type>: <description>

- <detail>
- <detail>
```
Types: feat, fix, chore, docs, refactor, test

### PR
```
## What
- <changes>

## Why
- <rationale>

## Testing
- <how tested>
```
