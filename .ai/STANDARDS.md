# Engineering Standards — CleanGalleryDeck

Enterprise-grade Kotlin + Compose coding standards. This document defines **exactly how** code must be written. Every rule includes a correct example and a prohibited anti-pattern.

References: `RULES.md` (project rules), `ADRs` (architectural decisions), `KNOWLEDGE.md` (technical patterns).

---

## 1. Kotlin Language — Reglas de Oro

| # | Regla | Correcto | Prohibido |
|---|-------|----------|-----------|
| 1.1 | Null Safety: NUNCA `!!` | `val uri = intent.getStringExtra("id") ?: return` | `val uri = intent.getStringExtra("id")!!` |
| 1.2 | Inmutabilidad: `data class` con `val` | `state.copy(items = newItems)` | `state.items = newItems` (var) |
| 1.3 | Sellado: `sealed class` para estado/eventos | `sealed class Resource<out T>` | `interface Resource<T>` + `enum` |
| 1.4 | Funciones puras en Domain (sin side effects) | `fun execute(input: Int): Int = input * 2` | `fun execute(i: Int): Int { Log.d("T", "$i"); return i * 2 }` |
| 1.5 | Scope functions con moderación | `item?.let { deleteUseCase(it) }` | `if (item != null) deleteUseCase(item)` |

## 2. State Management — MVI + Flow

| # | Regla | Correcto | Prohibido |
|---|-------|----------|-----------|
| 2.1 | State Hoisting: UI stateless, recibe state+events | `DeckScreen(state, onSwipeRight = { vm.handleIntent(...) })` | `DeckScreen(viewModel = viewModel)` |
| 2.2 | ViewModel usa StateFlow, no mutableStateOf | `private val _state = MutableStateFlow(DeckState())` | `var state by mutableStateOf(DeckState())` |
| 2.3 | Eventos: sealed class Intent + sealed class Effect | `handleIntent(DeckIntent.SwipeRight(item))` | `viewModel.swipeRight(item)` |
| 2.4 | Flow en Composable: collectAsStateWithLifecycle | `val state by vm.state.collectAsStateWithLifecycle()` | `val state by vm.state.collectAsState()` |
| 2.5 | Resource mapeado: Loading→Indicator, Error→Snackbar | `when (resource) { is Resource.Loading -> ... }` | `if (resource is Error) ...` sin Loading/Success |

## 3. Jetpack Compose — Performance

| # | Regla | Correcto | Prohibido |
|---|-------|----------|-----------|
| 3.1 | `@Immutable` / `@Stable` en modelos de estado | `@Immutable data class DeckState(...)` | `data class DeckState(...)` sin anotación |
| 3.2 | LazyColumn para listas, NO Column+forEach | `LazyColumn { items(items) { ... } }` | `Column { items.forEach { ... } }` |
| 3.3 | key en lazy lists siempre | `items(items, key = { it.id }) { ... }` | `items(items) { ... }` sin key |
| 3.4 | derivedStateOf para cálculos derivados | `val total by remember { derivedStateOf { items.size } }` | `val total = items.size` |
| 3.5 | remember para lambdas en hot paths | `val onDelete = remember { { item -> vm.delete(item) } }` | Lambda creada en cada recomposición |
| 3.6 | Modifier order consistente | `.fillMaxSize().padding(16.dp).background(...)` | `.background(...).padding(...)` |

## 4. UI/UX — Diseño y Accesibilidad

| # | Regla | Correcto | Prohibido |
|---|-------|----------|-----------|
| 4.1 | Touch targets >= 48dp | `Modifier.size(48.dp)` | `Modifier.size(24.dp)` |
| 4.2 | Contraste WCAG AA (>4.5:1) | `color = MaterialTheme.colorScheme.onPrimary` | `color = Color.Gray` sobre fondo claro |
| 4.3 | contentDescription en TODA imagen/elemento | `AsyncImage(..., contentDescription = "Profile photo")` | `AsyncImage(...)` sin description |
| 4.4 | Estados: Loading + Error + Empty + Success | `when { state.isLoading -> LoadingState() ... }` | Solo manejar caso vacío |
| 4.5 | NavHost con sealed class Screen | `navController.navigate(Screen.Deck.route)` | `startActivity(Intent(this, DeckActivity::class.java))` |
| 4.6 | Dark Mode via isSystemInDarkTheme() | `color = MaterialTheme.colorScheme.primary` | `color = Color.Blue` (hardcoded) |
| 4.7 | Texto en sp, NO dp | `fontSize = 16.sp` | `fontSize = 16.dp` |

## 5. Error Handling y Permisos

| # | Regla | Correcto | Prohibido |
|---|-------|----------|-----------|
| 5.1 | Permisos: rememberPermissionState | `val ps = rememberPermissionState(Manifest.permission.READ_MEDIA_IMAGES)` | `ActivityCompat.requestPermissions(...)` en ViewModel |
| 5.2 | UseCases devuelven Resource<T>, nunca throw | `Resource.Error("No se pudo borrar")` | `throw Exception("Error")` sin captura |
| 5.3 | SnackbarHost de Material 3, NO Toast | `SnackbarHost(hostState = snackbarHostState)` | `Toast.makeText(context, "...", LENGTH_SHORT).show()` |

## 6. UI Architecture (Presentation Layer)

| # | Regla | Correcto | Prohibido |
|---|-------|----------|-----------|
| 6.1 | UI no importa data/domain directamente | `implementation(project(":core:domain"))` solo ViewModel | `import com.deck.data.repository` en DeckScreen |
| 6.2 | ViewModel scoped a NavBackStackEntry via hiltViewModel | `val vm: DeckViewModel = hiltViewModel()` | `val vm = DeckViewModel(...)` manual |
| 6.3 | Operaciones largas: WorkManager, no viewModelScope | `WorkManager.getInstance(context).enqueue(work)` | `viewModelScope.launch { scanGallery() }` sin límite |

## 7. Testing (TDD)

| # | Regla | Correcto | Prohibido |
|---|-------|----------|-----------|
| 7.1 | UseCases + Repositorios: MockK + Turbine | `@Test fun delete_callsRepo() = runTest { ... }` | Sin tests o con Thread.sleep() |
| 7.2 | UI: ComposeTestRule con performClick/swipe | `composeTestRule.onNodeWithTag("delete").performClick()` | `Thread.sleep(1000)` en UI tests |
| 7.3 | @Preview para TODOS los composables críticos | `@Preview @Composable fun DeckPreview() { DeckScreen(...) }` | Sin previews |

## 8. Naming Convention

```
GalleryScreen            — Composable (screen-level)
GalleryViewModel         — state holder
GalleryUiState           — sealed interface for screen state
GalleryUiEvent           — sealed interface for user actions
GalleryRepository        — data access
DeletePhotosUseCase      — single action
GalleryCard              — reusable composable
GalleryRoute             — navigation route
```

## 9. Design System

- No hardcoded colors or dimensions. Use `MaterialTheme.colorScheme.*`.
- Custom composables (never raw Material components in features):
  `AppButton`, `AppCard`, `AppText`, `AppToolbar`, `AppDialog`, `AppLoading`, `AppChip`

## 10. Architecture

- Clean Architecture: Presentation → Domain ← Data (dependency inversion)
- MVI/UDF for Compose (see ADR-0004)
- Repository Pattern, Use Cases per feature (`operator fun invoke()`)
- Hilt for DI (see ADR-0001), modularization feature-first

## 11. Code Quality (Automated)

| Tool | Purpose | Run |
|------|---------|-----|
| ktlint | Kotlin formatting | `./gradlew ktlintCheck` |
| detekt | Static analysis (complexity, style, bugs) | `./gradlew detekt` |
| Android lint | Android-specific issues | `./gradlew lint` |
| dependencyCheck | Vulnerability scanning | `./gradlew dependencyCheckAnalyze` |
| Lefthook | Pre-push hooks | `test` + `lint` |

## 12. App Performance Metrics

| Metric | Target | Tool |
|--------|--------|------|
| Cold startup | < 1.5s | Macrobenchmark |
| Jank (missed frames) | < 5% | JankStats |
| Memory (mid-range) | < 256MB | LeakCanary + Profiler |
| ANR rate | 0% | Crashlytics + Play Console |
| Crash rate | < 1% | Crashlytics |

Baseline Profiles via Macrobenchmark before release. Animations: 150–300ms, subtle.

## 13. Code Review Checklist

Every PR must pass this checklist before merge:

| # | Check | Pass? |
|---|-------|-------|
| 1 | No `!!` assertions | ✅/❌ |
| 2 | State is immutable (`data class` + `val`) | ✅/❌ |
| 3 | UI is stateless (receives state + events) | ✅/❌ |
| 4 | `@Stable`/`@Immutable` on state models | ✅/❌ |
| 5 | `contentDescription` on all images/icons | ✅/❌ |
| 6 | Touch targets >= 48dp | ✅/❌ |
| 7 | `collectAsStateWithLifecycle()` not `collectAsState()` | ✅/❌ |
| 8 | `key` defined in all `LazyColumn`/`LazyGrid` | ✅/❌ |
| 9 | Errors mapped to `Resource<T>` → Snackbar | ✅/❌ |
| 10 | Long operations use `WorkManager` | ✅/❌ |
| 11 | Unit tests for all UseCases | ✅/❌ |
| 12 | `@Preview` for critical composables | ✅/❌ |

## 14. Agent Pre-Commit Verification

Before any commit, agent MUST run:

```bash
./gradlew ktlintCheck detekt    # formatting + static analysis
./gradlew test                  # unit tests
./gradlew lint                  # Android lint
```

Any failure = code not ready for production.

---

## Golden Rule

Code must be **legible, maintainable, scalable, testable, accessible, and consistent**. Every feature evolves over years with low maintenance cost.

References:
- [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- [Android Material Design](https://developer.android.com/develop/ui/views/theming/look-and-feel)
- [Compose Performance](https://developer.android.com/jetpack/compose/performance)
