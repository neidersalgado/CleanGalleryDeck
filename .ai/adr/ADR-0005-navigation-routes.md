# ADR-0005: Type-Safe NavHost Routes

**Status:** Accepted  
**Date:** 2026-07-24  
**Deciders:** Staff Engineer

## Context

The app has 12 screens with complex navigation (deck → player → preview, deck → trash, settings → stats, etc.). String-based routes are error-prone and hard to refactor.

## Decision

Define all routes as a `sealed class Screen` with typed arguments:

```kotlin
sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Deck : Screen("deck")
    data class Player(val itemId: String) : Screen("player/{itemId}")
    data class Preview(val itemId: String) : Screen("preview/{itemId}")
    // ...
}
```

Navigation calls use `navController.navigate(Screen.Player(itemId))`.

## Consequences

- Compile-time safety: no string typos in navigation calls.
- Refactorable: changing a route pattern updates all call sites.
- Type-safe arguments: `itemId` is always `String`, no manual casting from `NavBackStackEntry`.
- Single source of truth: all routes in one file.
- Cannot use navigation-compose type-safe args DSL (Kotlin 2.1.0 compatible but opted for simplicity).
