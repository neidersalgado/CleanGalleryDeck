# ADR-0003: @IntoSet for MediaSource Extensibility

**Status:** Accepted  
**Date:** 2026-07-24  
**Deciders:** Staff Engineer, System Design

## Context

The app must support multiple media types (images, videos, Google Photos) without modifying core code. Adding a new source type should be a single new module with zero changes to existing code.

## Decision

Define `MediaSource` interface in `:core:domain`. Each source module implements it and registers via Hilt `@Binds @IntoSet`. `MediaSourceRegistry` in `:core:data` receives `Set<MediaSource>` and filters by availability.

## Consequences

- Open/Closed Principle: new sources = new module, no core changes.
- Runtime discovery: `MediaSourceRegistry.isAvailable()` per source.
- Failure isolation: `try/catch` in registry prevents one failing source from blocking others.
- Testability: mock `Set<MediaSource>` in tests.
- DI complexity: each source needs its own Hilt module.
