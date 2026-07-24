# Knowledge Pool — CleanGalleryDeck

Reference knowledge for Android development, Google APIs, security, and architecture patterns.

## Scoped Storage by API Level

| API Level | Permissions | Deletion Method |
|-----------|-------------|----------------|
| 24-28 | READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE | `ContentResolver.delete()` |
| 29 | READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE (deprecated) | `ContentResolver.delete()` |
| 30-32 | READ_EXTERNAL_STORAGE | `MediaStore.createDeleteRequest()` with PendingIntent |
| 33+ | READ_MEDIA_IMAGES, READ_MEDIA_VIDEO | `MediaStore.createDeleteRequest()` |

## Google Photos Integration

- Picker API: user selects photos interactively. No batch/background access.
- Library API: NOT usable for reading pre-existing photos (deprecated since March 2025).
- Authentication: Credential Manager with Google ID token.
- Token storage: EncryptedSharedPreferences (AES256).

## Permissions Code Pattern

```kotlin
// API 33+ granular media permissions
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
    Manifest.permission.READ_MEDIA_IMAGES
    Manifest.permission.READ_MEDIA_VIDEO
} else {
    Manifest.permission.READ_EXTERNAL_STORAGE
}
```

## Deletion on Android 11+

```kotlin
suspend fun deleteItem(context: Context, uri: Uri): Result<Unit> {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val pendingIntent = MediaStore.createDeleteRequest(context.contentResolver, listOf(uri))
        // Launch PendingIntent for user consent
        Result.success(Unit)
    } else {
        context.contentResolver.delete(uri, null, null)
        Result.success(Unit)
    }
}
```

## MediaStore Query Pattern

```kotlin
val projection = arrayOf(
    MediaStore.Images.Media._ID,
    MediaStore.Images.Media.DISPLAY_NAME,
    MediaStore.Images.Media.DATE_TAKEN,
    MediaStore.Images.Media.SIZE,
    MediaStore.Images.Media.MIME_TYPE
)
val cursor = contentResolver.query(
    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
    projection, null, null,
    "${MediaStore.Images.Media.DATE_TAKEN} DESC"
)
```

## Video Thumbnail

```kotlin
fun getVideoThumbnail(context: Context, uri: Uri): Bitmap? {
    val retriever = MediaMetadataRetriever()
    return try {
        retriever.setDataSource(context, uri)
        retriever.getFrameAtTime(0)
    } finally { retriever.release() }
}
```

## Undo Stack Pattern

- Store deleted item in-memory with timestamp
- Expose 5-second countdown via StateFlow
- On undo: restore via `ContentResolver.insert()` for MediaStore, or revert Google API call
- On expiry: commit permanent deletion

## MediaSource Extensibility Pattern

1. Define interface in `:media-sources:media-source-api`
2. Implement in dedicated module (e.g., `:media-sources:source-local-audio`)
3. Register with `@Binds @IntoSet` in a Hilt module
4. `MediaSourceRegistry` receives `Set<MediaSource>` and filters by availability
5. No changes needed in ViewModels or Use Cases

## Baseline Profiles (Performance)

Generate with Macrobenchmark module before Play Store release:

```kotlin
// :benchmarks/src/main/java/com/deck/benchmark/StartupBenchmark.kt
@RunWith(AndroidJUnit4::class)
class StartupBenchmark {
    @get:Rule val benchmarkRule = MacrobenchmarkRule()

    @Test
    fun startup() = benchmarkRule.measureRepeated(
        packageName = "com.deck.clean",
        metrics = listOf(StartupTimingMetric()),
        iterations = 10,
        startupMode = StartupMode.COLD,
    ) { pressHome() }
}
```

Profile rules in `:app/src/main/baseline-prof-gte/com.deck.clean/baseline.prof`:
```
HSPLcom/deck/feature/deck/ui/DeckScreen;
HSPLcom/deck/clean/MainActivity;
HSPLandroidx/compose/foundation/lazy/LazyColumn;
```

## Paging 3 Integration

```kotlin
// In GetGalleryItemsUseCase or repository
class GalleryPagingSource(
    private val localSource: LocalImageMediaSource,
    private val context: Context,
) : PagingSource<Int, MediaItem>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MediaItem> {
        return try {
            val page = params.key ?: 0
            val items = localSource.queryItems(context, page, params.loadSize)
            LoadResult.Page(
                data = items,
                prevKey = if (page > 0) page - 1 else null,
                nextKey = if (items.size == params.loadSize) page + 1 else null,
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
```

ViewModel collects with `Flow<PagingData<MediaItem>>`:
```kotlin
val items: StateFlow<PagingData<MediaItem>> = Pager(PagingConfig(pageSize = 50)) {
    GalleryPagingSource(source, context)
}.flow.cachedIn(viewModelScope).stateIn(viewModelScope)
```

## Feature Flags Pattern

```kotlin
interface FeatureFlagProvider {
    fun isEnabled(flag: FeatureFlag): Flow<Boolean>
}

class DataStoreFeatureFlagProvider @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : FeatureFlagProvider {
    override fun isEnabled(flag: FeatureFlag): Flow<Boolean> {
        return dataStore.data.map { it[boolPreferencesKey(flag.key)] ?: flag.default }
    }
}
```

Usage in UI:
```kotlin
@Composable
fun DeckScreen(state: DeckState, flags: Map<FeatureFlag, Boolean>) {
    if (flags[FeatureFlag.EXPERIMENTAL_ANIMATIONS] == true) {
        // New animation path
    } else {
        // Stable animation path
    }
}
```

## Observability Contracts (Domain Interfaces)

```kotlin
// :core/domain/src/main/java/com/deck/domain/observability/AnalyticsService.kt
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

Implementation in Use Cases:
```kotlin
class DeleteMediaUseCase @Inject constructor(
    private val repository: MediaItemRepository,
    private val tracer: PerformanceTracer,
    private val analytics: AnalyticsService,
) {
    suspend operator fun invoke(item: MediaItem): Resource<Unit> {
        val trace = tracer.startTrace("delete_media")
        return try {
            val result = repository.delete(item)
            trace.putMetric("success", if (result is Resource.Success) 1 else 0)
            analytics.trackEvent("media_deleted", mapOf("source" to item.sourceType.name))
            result
        } finally { trace.stop() }
    }
}
```

## AEP Reference

The AI Engineering Platform (AEP) at `~/aep/` provides:
- Skills: reusable task definitions (create_feature, code_review, debug)
- Agents: specialized personas (architect, backend, reviewer)
- Capabilities: tool interfaces (git, filesystem, terminal)
- Governance: rules, ADRs, policies
- Observability: metrics, traces, cost tracking
- Patterns: hexagonal, clean architecture, DDD
- Antipatterns: god service, shared state, n+1

Full AEP structure is defined in CONTRIBUTING.md for future expansion.
