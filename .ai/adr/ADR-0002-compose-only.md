# ADR-0002: Compose-Only UI

**Status:** Accepted  
**Date:** 2026-07-24  
**Deciders:** Staff Engineer, Tech Lead

## Context

The app is gesture-heavy (swipe deck, pinch-to-zoom). Fragments/XML Views add boilerplate without benefit. Developers must focus on gesture interactions, not lifecycle callbacks.

## Decision

Use Jetpack Compose exclusively for all UI. No XML layouts (except `AndroidManifest.xml`, `network_security_config.xml`). No Fragments.

## Consequences

- Pure Compose navigation via NavHost — no Fragment transactions.
- State hoisting pattern enforces unidirectional data flow.
- ComposeTestRule for E2E UI tests (no Espresso).
- Material 3 + Dynamic Color supported natively.
- Compose compiler plugin (Kotlin 2.1.0) avoids `@Composable` annotation processing issues.
- Risk: Compose performance with large lists. Mitigated via `LazyColumn` + `key()` + Coil disk cache.
