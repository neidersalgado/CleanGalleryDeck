# Planning — CleanGalleryDeck

## Vision

Review and clean media galleries via a Tinder-style swipeable deck. Swipe right = delete, left/up = keep.

## Build Order (Dependencies)

| Order | Layer | Module | Depends On |
|-------|-------|--------|------------|
| 1 | Config | Gradle, libs.versions.toml, Manifest | — |
| 2 | Security | EncryptedSharedPreferences, Permissions | Config |
| 3 | Models (Domain) | MediaItem, MediaType, MediaFilter | — |
| 4 | Ports (Domain) | MediaItemRepository, MediaSource interfaces | Models |
| 5 | Data Sources (Infra) | LocalMediaSource (MediaStore), GoogleMediaSource (Picker) | Ports |
| 6 | Use Cases (App) | GetGalleryItems, DeleteMedia, UndoDelete | Ports + Data Sources |
| 7 | ViewModel (UI) | DeckViewModel, SettingsViewModel | Use Cases |
| 8 | UI (Compose) | 12 screens + Navigation | ViewModels |

## Libraries

| Component | Dependency | Notes |
|-----------|------------|-------|
| DI | Hilt 2.52 | @HiltAndroidApp |
| Local DB | Room 2.6.1 | trash_items, reviewed_items, stats |
| Secure Prefs | security-crypto 1.1.0 | MasterKey.AES256_GCM |
| Google Auth | googleid 1.1.1 | Credential Manager |
| Google Photos | photos-picker 1.0.0 | ActivityResultContract |
| Images | Coil 2.7.0 | AsyncImage + disk cache |
| Tests | JUnit 4.13.2, MockK 1.13.10, Turbine 1.0.0 | TDD cycle |

## Screens (12 total)

| # | Screen | User Story | Use Case |
|---|--------|------------|----------|
| 1 | Onboarding | "Ver qué hace la app y conceder permisos" | PermissionManager |
| 2 | Login Google | "Autenticarme para limpiar mi nube" | AuthenticateGoogleUseCase |
| 3 | Selector Fuentes | "Elegir fotos, videos, carpetas o nube" | GetGalleryItemsUseCase, FilterGalleryUseCase |
| 4 | Deck Principal | "Deslizar fotos para borrar/mantener" | GetGalleryItemsUseCase, DeleteMediaUseCase, KeepMediaUseCase, UndoDeleteUseCase |
| 5 | Reproductor | "Reproducir video/audio antes de borrar" | ExoPlayer + same Use Cases |
| 6 | Vista Previa | "Hacer zoom y ver detalles" | AsyncImage + pinch-to-zoom |
| 7 | Papelera | "Ver y restaurar borrados recientes" | GetTrashItemsUseCase, RestoreFromTrashUseCase, EmptyTrashUseCase |
| 8 | Lista Archivos | "Ver grid/lista y seleccionar varios" | GetGalleryItemsUseCase batch |
| 9 | Selector Carpetas | "Elegir carpeta específica (SAF)" | OpenDocumentTree + MediaFilter |
| 10 | Configuración | "Ajustar sensibilidad, modo oscuro, Google" | DataStore |
| 11 | Estadísticas | "Ver espacio liberado y progreso" | StatsRepository (Room) |
| 12 | Carga Inicial | "Ver progreso de escaneo" | GetGalleryItemsUseCase background |

## Screen Details

### Screen 1: Onboarding
- Show permission cards (Photos, Audio, Documents)
- Tap "Grant" -> requestPermissions()
- Audio/Docs permissions are OPTIONAL (skip button)
- All granted -> enable "Start" button -> navigate to Screen 2

### Screen 2: Login Google
- Tap "Sign in with Google" -> CredentialManager.getCredential()
- Receive GoogleIdTokenCredential -> save token in EncryptedSharedPreferences
- OPTIONAL: user can tap "continue without cloud"

### Screen 3: Source Selector
- Query GetGalleryItemsUseCase with filters
- Show counts (e.g. "Photos: 1,247")
- Toggle to select/deselect sources
- Inject MediaFilter into ViewModel -> navigate to Screen 4

### Screen 4: Deck (Core)
- DeckViewModel loads items via GetGalleryItemsUseCase
- HorizontalPager with swipe detection
- Swipe RIGHT > 40% -> DeleteMediaUseCase -> UndoStack -> Snackbar 5s
- Swipe LEFT/UP > 40% -> KeepMediaUseCase -> advance
- Swipe < 40% -> bounce back
- Overlay: red (delete) / green (keep) when > 40%
- "Undo" tap -> UndoDeleteUseCase

### Screen 5: Media Player
- Receive MediaItem via navigation
- Initialize ExoPlayer with MediaItem.uri
- Play/Pause, SeekBar controls
- "Delete" and "Keep" buttons (same Use Cases as Deck)

### Screen 6: Preview / Zoom
- AsyncImage with Modifier.graphicsLayer for scale/translation
- detectTransformGestures for pinch-to-zoom (max 5x)
- Double-tap to reset

### Screen 7: Trash
- GetTrashItemsUseCase queries Room table trash_items
- List with "Restore" button -> RestoreFromTrashUseCase
- "Empty Trash" button -> EmptyTrashUseCase (permanent physical delete)

### Screen 8: File List (Grid/List)
- LazyVerticalGrid (4 columns) toggle with List view
- Multi-selection with SelectionManager
- "Delete selected" -> batch DeleteMediaUseCase

### Screen 9: Folder Picker (SAF)
- Tap -> ActivityResultContracts.OpenDocumentTree
- Receive folder Uri -> DocumentFile -> count files
- Create MediaFilter with folderUri

### Screen 10: Settings
- DataStore Preferences for values
- Dark mode toggle (updates LocalConfiguration)
- Sensitivity slider (1-10)
- Google status (email if authed, logout button)

### Screen 11: Statistics
- StatsRepository (Room): total deleted, kept, freed bytes
- Simple bar charts with Canvas Compose

### Screen 12: Loading / Scan
- GetGalleryItemsUseCase in background (Dispatchers.IO)
- Emit progress per type (e.g. "Photos: 1,247")
- When done -> navigate to Deck

## Database (Room) Schema

### reviewed_items
- id (PK), itemId, reviewedAt, action (KEPT/DELETED)
- Purpose: review history

### trash_items
- id (PK), uri, displayName, size, deletedAt, restoreDeadline
- Purpose: local trash with restore capability

### stats
- date (PK), deletedCount, keptCount, freedBytes
- Purpose: daily metrics

## Use Case Mapping

| User Story | Use Case |
|------------|----------|
| View my gallery | GetGalleryItemsUseCase |
| Swipe right to delete | DeleteMediaUseCase |
| Swipe left/up to keep | KeepMediaUseCase |
| Undo deletion | UndoDeleteUseCase |
| Filter by album/date | FilterGalleryUseCase |
| Connect Google Photos | AuthenticateGoogleUseCase |
| Select cloud photos | LoadGooglePhotosUseCase |
| View trash | GetTrashItemsUseCase |
| Restore from trash | RestoreFromTrashUseCase |
| Empty trash | EmptyTrashUseCase |

## Security Requirements

- Tokens: EncryptedSharedPreferences with AES256_GCM
- Permissions: READ_MEDIA_IMAGES + READ_MEDIA_VIDEO (API 33+)
- Zero logs in production: ProGuard strips Log.d / Timber
- Deletion: MediaStore.createDeleteRequest() with PendingIntent (API 30+)

## Roadmap

| Sprint | Focus | Status |
|--------|-------|--------|
| Sprint 0 | Setup (multi-module, Gradle, Hilt, Compose, CI) | ✅ Complete |
| Sprint 1 | Domain models + Security + Permissions | ⏳ Pending |
| Sprint 2 | Data Sources (Local MediaStore) + Use Cases | ⏳ Pending |
| Sprint 3 | Deck UI + ViewModel + Gestures | ⏳ Pending |
| Sprint 4 | Deletion + Undo + Trash (Room) | ⏳ Pending |
| Sprint 5 | Google Photos (Auth + Picker) | ⏳ Pending |
| Sprint 6 | Polish (settings, stats, dark mode, Play Store) | ⏳ Pending |

## ADRs

### ADR-001: Clean Architecture + MVVM
- Context: complex business logic (deletion, sync) with reactive UI
- Decision: Clean Architecture with MVVM
- Rationale: testability, maintainability, Google standard

### ADR-002: Scoped Storage with MediaStore
- Context: Android 11+ restricts file access
- Decision: MediaStore API for all ops, SAF as fallback
- Strategy: API 24-28 legacy, 29 partial, 30+ createDeleteRequest, 33+ granular permissions

### ADR-003: Google Picker over Library API
- Context: Google Photos Library API stopped allowing pre-existing photo access (March 2025)
- Decision: Use Photos Picker API exclusively

### ADR-004: @IntoSet for MediaSource extensibility
- Context: app must support multiple media types without modifying core
- Decision: Dagger Hilt @IntoSet to collect all MediaSource implementations

### ADR-005: Room for Trash + Stats
- Context: need persistent trash with restore deadlines and daily metrics
- Decision: Room database with 3 tables (reviewed_items, trash_items, stats)

## Risks

| Risk | Probability | Impact | Mitigation |
|------|-------------|--------|------------|
| Scoped Storage (Android 11+) | High | High | createDeleteRequest + SAF |
| Google Photos API Rate Limit | Medium | Medium | Exponential backoff + batching |
| Performance with 10k+ photos | Medium | High | Pagination in Pager |
| Accidental deletion | High | High | Undo snackbar (5s) |
| Permissions denied (Android 13+) | Medium | Medium | Explain and guide user |

## Metrics

- Gallery load time: < 2s (1000 items)
- Deletion success rate: > 95%
- Test coverage: > 85%
- Crashes: < 1%

---
## Gap Analysis — SDD Closing Annex

### GAP 1: Physical Module Mapping (Clean Architecture vs Gradle)

| Clean Layer | Gradle Module | Package Path | Dependency Rule |
|-------------|---------------|--------------|-----------------|
| Domain | `:core:domain` | `com.deck.domain` | NO Android deps. Only Kotlin stdlib. |
| Application (Use Cases) | `:core:domain` | `com.deck.domain.usecase` | Depends ONLY on `:core:domain` |
| Data (Repositories) | `:core:data` | `com.deck.data` | Depends on `:core:domain` + `:media-sources:*` |
| Data Sources | `:media-sources:*` | `com.deck.mediasource` | Depends on `:core:domain` + `:media-sources:api` |
| Presentation | `:feature:deck`, `:feature:settings` | `com.deck.feature.deck` | Depends on `:core:domain` (ViewModels) + `:core:data` |

**Directive:** Each UseCase goes in `:core:domain/src/main/java/com/deck/domain/usecase`. Each Repository impl goes in `:core:data/src/main/java/com/deck/data/repository`. MediaSource impls go in `:media-sources:source-*`.

### GAP 2: Explicit MVI Pattern (Unidirectional Data Flow)

State:
```kotlin
data class DeckState(
    val items: List<MediaItem> = emptyList(),
    val currentIndex: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null,
    val undoItem: MediaItem? = null,
    val undoRemainingSeconds: Int = 0
)
```

Events (Intent):
```kotlin
sealed class DeckIntent {
    data class SwipeRight(val item: MediaItem) : DeckIntent()
    data class SwipeLeft(val item: MediaItem) : DeckIntent()
    data class SwipeUp(val item: MediaItem) : DeckIntent()
    object LoadGallery : DeckIntent()
    object UndoDelete : DeckIntent()
    object TimerTick : DeckIntent()
}
```

ViewModel:
```kotlin
fun handleIntent(intent: DeckIntent) {
    when (intent) {
        is DeckIntent.SwipeRight -> viewModelScope.launch {
            deleteMediaUseCase(intent.item).collect { result ->
                _state.update { it.copy(undoItem = result.item) }
            }
        }
        else -> { /* ... */ }
    }
}
```

### GAP 3: Navigation Routes (NavHost)

```kotlin
sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object GoogleLogin : Screen("google_login")
    object SourceSelector : Screen("source_selector")
    object Deck : Screen("deck")
    object Player : Screen("player/{itemId}")
    object Preview : Screen("preview/{itemId}")
    object Trash : Screen("trash")
    object FileList : Screen("file_list")
    object FolderPicker : Screen("folder_picker")
    object Settings : Screen("settings")
    object Stats : Screen("stats")
    object Loading : Screen("loading")
}
```

NavHost routing in MainActivity.

### GAP 4: Error Handling Strategy (Resource Wrapper)

```kotlin
sealed class Resource<out T> {
    data class Success<T>(val data: T) : Resource<T>()
    data class Loading<T>(val data: T? = null) : Resource<T>()
    sealed class Error : Resource<Nothing>() {
        data class Network(val cause: Throwable) : Error()
        data class Permission(val message: String) : Error()
        data class Disk(val cause: Throwable) : Error()
        data class GoogleAuth(val message: String) : Error()
        object Unknown : Error()
    }
}
```

Use Cases emit `Flow<Resource<T>>`. ViewModel reacts to Loading/Success/Error states.

### GAP 5: Integration Tests with MediaStore (Robolectric)

```kotlin
@RunWith(RobolectricTestRunner::class)
class LocalImageSourceTest {
    private lateinit var context: Context
    private lateinit var source: LocalImageMediaSource

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        source = LocalImageMediaSource()
    }

    @Test
    fun `queryItems returns ordered list`() = runTest {
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "test.jpg")
            put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
        }
        val uri = context.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values
        )
        val result = source.queryItems(context, null).first()
        assertTrue(result.any { it.displayName == "test.jpg" })
        uri?.let { context.contentResolver.delete(it, null, null) }
    }
}
```

### Final Agent Checklist

Before writing code, agent must confirm:
- [ ] Module structure: what goes in `:core:domain`, `:core:data`, `:feature:deck` (GAP 1)
- [ ] MVI pattern: `sealed class Intent` for user actions (GAP 2)
- [ ] NavHost: 12 routes in `sealed class Screen` (GAP 3)
- [ ] Resource wrapper: `Resource<T>` for error handling without exceptions (GAP 4)
- [ ] MediaStore tests: Robolectric + ContentValues for query tests (GAP 5)
- [ ] Implement TDD: RED (test) -> GREEN (code) -> REFACTOR

---
## Gap Analysis — Part 2 (GAPs 6-10)

### GAP 6: Hilt Dependency Injection — Explicit Modules

```kotlin
// :app/src/main/java/com/deck/clean/di/UseCaseModule.kt
@Module
@InstallIn(SingletonComponent::class)
abstract class UseCaseModule {
    @Binds
    abstract fun bindGetGalleryUseCase(impl: GetGalleryItemsUseCaseImpl): GetGalleryItemsUseCase
    @Binds
    abstract fun bindDeleteUseCase(impl: DeleteMediaUseCaseImpl): DeleteMediaUseCase
    @Binds
    abstract fun bindKeepUseCase(impl: KeepMediaUseCaseImpl): KeepMediaUseCase
    @Binds
    abstract fun bindUndoUseCase(impl: UndoDeleteUseCaseImpl): UndoDeleteUseCase
}

// :core/data/src/main/java/com/deck/data/di/RepositoryModule.kt
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindMediaRepository(impl: MediaRepositoryImpl): MediaItemRepository
}

// :media-sources/source-local-images/src/.../di/ImageSourceModule.kt
@Module
@InstallIn(SingletonComponent::class)
abstract class ImageSourceModule {
    @Binds @IntoSet
    abstract fun bindLocalImageSource(impl: LocalImageMediaSource): MediaSource
}

// :core/data/src/main/java/com/deck/data/di/RegistryModule.kt
@Module
@InstallIn(SingletonComponent::class)
object RegistryModule {
    @Provides
    fun provideRegistry(sources: Set<@JvmSuppressWildcards MediaSource>): MediaSourceRegistry {
        return MediaSourceRegistry(sources)
    }
}
```

### GAP 7: User Preferences (DataStore)

```kotlin
// :core/data/src/main/java/com/deck/data/preferences/UserPreferences.kt
@Singleton
class UserPreferences @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        val SENSITIVITY = intPreferencesKey("gesture_sensitivity")
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val TRASH_DAYS = intPreferencesKey("trash_retention_days")
    }
    val sensitivityFlow: Flow<Int> = dataStore.data.map { it[SENSITIVITY] ?: 7 }
    suspend fun setSensitivity(value: Int) { dataStore.edit { it[SENSITIVITY] = value } }
}

// DataStoreModule.kt
@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {
    @Provides
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create {
            context.preferencesDataStoreFile("user_prefs")
        }
    }
}
```

### GAP 8: Background Processing (WorkManager)

```kotlin
// :core/data/src/main/java/com/deck/data/worker/ScanGalleryWorker.kt
class ScanGalleryWorker(
    context: Context,
    params: WorkerParameters,
    private val getGalleryUseCase: GetGalleryItemsUseCase
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        return try {
            getGalleryUseCase(MediaFilter()).collect { items ->
                setProgress(workDataOf("count" to items.size))
            }
            Result.success()
        } catch (e: Exception) { Result.retry() }
    }
    companion object { const val WORK_NAME = "gallery_scan_work" }
}
```

Usage in ViewModel:
```kotlin
fun startBackgroundScan() {
    val request = OneTimeWorkRequestBuilder<ScanGalleryWorker>()
        .setConstraints(Constraints.Builder().build())
        .build()
    WorkManager.getInstance(context).enqueue(request)
}
```

### GAP 9: Compose State Hoisting (Stateless UI)

```kotlin
// Stateless UI — receives state + events, no ViewModel reference
@Composable
fun DeckScreen(
    state: DeckState,
    onSwipeRight: (MediaItem) -> Unit,
    onSwipeLeft: (MediaItem) -> Unit,
    onUndo: () -> Unit
) { /* UI only, pure Compose */ }

// Container — only this imports hiltViewModel
@Composable
fun DeckScreenContainer(
    viewModel: DeckViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    DeckScreen(
        state = state,
        onSwipeRight = { viewModel.handleIntent(DeckIntent.SwipeRight(it)) },
        onSwipeLeft = { viewModel.handleIntent(DeckIntent.SwipeLeft(it)) },
        onUndo = { viewModel.handleIntent(DeckIntent.UndoDelete) }
    )
}
```
Rule: DeckScreen NEVER imports hiltViewModel. Only DeckScreenContainer does.

### GAP 10: Reactive Permission Handling

```kotlin
@Composable
fun PermissionAwareContent(
    viewModel: PermissionViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val permissionState = rememberPermissionState(Manifest.permission.READ_MEDIA_IMAGES)

    LaunchedEffect(permissionState.status) {
        when (permissionState.status) {
            PermissionStatus.Granted -> viewModel.onPermissionGranted()
            PermissionStatus.Denied -> viewModel.onPermissionDenied()
            else -> Unit
        }
    }

    if (permissionState.status.isGranted) {
        DeckScreenContainer()
    } else {
        OnboardingScreen(onRequestPermission = { permissionState.launchPermissionRequest() })
    }
}
```

### Final Checklist (Updated)

- [ ] **Module structure** (GAP 1): what goes in :core:domain, :core:data, :feature:deck
- [ ] **MVI/UDF** (GAP 2): sealed class Intent for user actions
- [ ] **NavHost** (GAP 3): 12 routes in sealed class Screen
- [ ] **Resource wrapper** (GAP 4): Resource<T> for errors without exceptions
- [ ] **MediaStore tests** (GAP 5): Robolectric + ContentValues
- [ ] **Hilt modules** (GAP 6): UseCaseModule, RepositoryModule, RegistryModule, ImageSourceModule
- [ ] **DataStore** (GAP 7): UserPreferences for sensitivity, dark mode, trash days
- [ ] **WorkManager** (GAP 8): ScanGalleryWorker for long-running scans
- [ ] **State Hoisting** (GAP 9): Stateless UI, state + events as params
- [ ] **Reactive Permissions** (GAP 10): Handle revocations from Settings
- [ ] **TDD**: RED (test) -> GREEN (code) -> REFACTOR

---
## NFR + Deployment (Non-Functional Requirements & DevOps)

### 1. Performance

| Metric | SLO | Tool | Strategy |
|--------|-----|------|----------|
| Initial load | < 2s for 1000 files | Android Profiler | Paginate Pager (50 items), Coil disk cache |
| Deletion time | < 500ms per file | System.currentTimeMillis() | Dispatchers.IO, non-blocking UI |
| RAM | < 256MB mid-range | LeakCanary | Coil MemoryCache at 20% RAM, recycle onStop |
| FPS | 60 stable during swipe | GPU Profiler | remember, derivedStateOf, key in lists |
| ANR | 0 in production | Crashlytics | WorkManager for long tasks, no disk/network on Main |

### 2. Security

- Tokens: EncryptedSharedPreferences + MasterKey.AES256_GCM
- Network: HTTPS only (Google APIs). No custom servers.
- Zero logs: ProGuard strips Log.d/Timber in release. CrashlyticsTree sanitizes.
- Minimum permissions: READ_MEDIA_IMAGES + READ_MEDIA_VIDEO (API 33+). NEVER MANAGE_EXTERNAL_STORAGE.

### 3. Scalability

- 50k files supported: Room indexes on date_taken + media_type
- Paging: LIMIT + OFFSET with PagingSource
- New media types: @IntoSet + MediaSource, no core changes

### 4. Reliability

- Crash rate: < 1% (Firebase Crashlytics)
- Deletion success: > 99%. Fallback: SAF if MediaStore.delete fails on API 30+

### 5. Usability & Accessibility

- Touch targets: min 48dp, action buttons 56dp
- Contrast: Material 3 meets WCAG 2.1 AA (ratio > 4.5:1)
- TalkBack: Modifier.semantics on all images and buttons

### 6. Testing Strategy

Pyramid: 70% unit (JUnit + MockK) -> 20% integration (Robolectric + MockWebServer) -> 10% E2E (ComposeTestRule)

| Level | Tool | Coverage Target | Run |
|-------|------|----------------|-----|
| Unit (Domain) | JUnit 5 + MockK | 100% Use Cases & Models | Every PR |
| Integration (Data) | Robolectric + MockWebServer | 85% DataSources & Repos | Every PR |
| UI (Compose) | ComposeTestRule | 70% critical flows | Every PR (headless) |
| Performance | Baseline Profiles + Macrobenchmark | Launch & scroll < 2s | Before release |

### 7. CI/CD Pipeline (GitHub Actions)

| Phase | Command | Tool |
|-------|---------|------|
| 1. Lint | ./gradlew lint | Android Lint |
| 2. Test | ./gradlew testDebugUnitTest | JUnit, Robolectric |
| 3. Build | ./gradlew assembleDebug | Gradle |

### 8. Signing & Distribution

- Release keys: NEVER in repo. Use GitHub secrets (SIGNING_KEY_ALIAS, SIGNING_KEY_PASSWORD, SIGNING_STORE_PASSWORD).
- Channels: Internal Testing (per PR) -> Closed Alpha (per Sprint) -> Open Beta -> Production

### 9. Build Variants

| Variant | Purpose | Config |
|---------|---------|--------|
| debug | Daily dev | Logs on, minify off, .debug suffix |
| release | Play Store | Logs off, minify on, ProGuard |

### 10. Observability (Production)

| Tool | Metric | Alert |
|------|--------|-------|
| Crashlytics | Crash rate > 1% | Slack notification |
| Analytics | Delete Success vs Fail | Alert if fails > 2% |
| Performance | Gallery load > 3s | Optimize MediaStore query |
| Play Console | ANR rate > 0.5% | Review Dispatchers + WorkManager |

### NFR Checklist

- [ ] Performance: Pager paginated? Coil with cache?
- [ ] Security: EncryptedSharedPreferences? Logs stripped in release?
- [ ] Scalability: @IntoSet for MediaSource?
- [ ] Tests: Robolectric integration tests in CI?
- [ ] CI/CD: GitHub Actions signs APK automatically?
- [ ] Deployment: Signing secrets in GitHub, not in code?
