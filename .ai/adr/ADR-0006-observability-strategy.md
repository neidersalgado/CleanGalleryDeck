# ADR-0006: Observability Strategy — Analytics Abstraction

**Status:** Accepted  
**Date:** 2026-07-24  
**Deciders:** SRE, Staff Engineer

## Context

The app needs analytics, crash reporting, and performance tracing. Firebase is the likely provider, but coupling domain code to Firebase would break Clean Architecture and make provider swaps impossible.

## Decision

Define all observability contracts in `:core:domain` as Kotlin interfaces:

```kotlin
interface AnalyticsService {
    fun trackEvent(name: String, properties: Map<String, String> = emptyMap())
}

interface CrashReporter {
    fun log(throwable: Throwable, message: String? = null)
}

interface PerformanceTracer {
    fun startTrace(name: String): Trace
    interface Trace {
        fun putMetric(name: String, value: Long)
        fun stop()
    }
}
```

Implementations live in `:core:analytics` (Firebase, or any provider). Domain/UI never import Firebase. Hilt binds implementations.

## Consequences

- Domain layer stays pure Kotlin — testable without Firebase.
- Provider swap: implement new interfaces, change Hilt module, zero domain changes.
- Mockable in tests: inject `AnalyticsService` as fake.
- Traces in all UseCases: `delete_media`, `load_gallery`, `undo_delete`.
- Crashlytics logging with sanitized data (no PII in logs).
- Performance tracing for critical paths: gallery load (<2s), deletion (<500ms).
