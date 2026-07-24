# ADR-0001: Multi-Module Clean Architecture

**Status:** Accepted  
**Date:** 2026-07-24  
**Deciders:** Staff Engineer, Tech Lead, CTO

## Context

The app must support multiple media sources (local, Google Photos), a clean gallery deck UI, and be testable and maintainable over years. A single-module app would become unmanageable as features grow.

## Decision

Use a multi-module Gradle project following Clean Architecture layers:

- `:core:domain` — pure Kotlin, no Android deps. Entities, Use Cases, Repository interfaces.
- `:core:data` — implements domain interfaces, orchestrates MediaSource implementations.
- `:feature:*` — Compose UI + ViewModels per feature.
- `:media-sources:*` — pluggable source implementations registered via `@IntoSet`.

## Consequences

- Clear build-time isolation: domain layer cannot accidentally depend on Android.
- Faster CI: Gradle caches per module, only changed modules rebuild.
- Higher initial complexity (12 modules) but scales indefinitely.
- Dependency inversion: `:core:data` depends on `:core:domain`, never vice versa.
- Extra build config per module, but standardized via `libs.versions.toml`.
