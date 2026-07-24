# ADR-0004: MVI with StateFlow

**Status:** Accepted  
**Date:** 2026-07-24  
**Deciders:** Staff Engineer, Tech Lead

## Context

The deck screen needs real-time state updates (current card, undo countdown, loading/error states). Multiple events (swipe, undo, timer tick) must produce a single predictable state.

## Decision

Use MVI (Model-View-Intent) with `StateFlow`:

- `DeckState` — single immutable data class holding all UI state.
- `DeckIntent` — sealed class for all user actions.
- `DeckViewModel` — processes intents via `handleIntent()`, updates state via `StateFlow`.
- UI is stateless: `DeckScreen(state, onEvent)` — no ViewModel reference.

## Consequences

- Single source of truth: UI renders exactly one state.
- Predictable state transitions: each intent maps to a pure state update.
- Testable: pump intents, assert state changes via Turbine.
- State hoisting enforced: container vs. screen separation.
- Boilerplate: more files per screen (State, Intent, ViewModel, Screen, Container).
- Undo timer: managed via `StateFlow` countdown, not platform timers.
