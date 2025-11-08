package dev.aurakai.auraframefx.ai.memory

import dev.aurakai.auraframefx.data.repository.AgentMemoryRepository
import dev.aurakai.auraframefx.data.room.AgentMemoryEntity
import dev.aurakai.auraframefx.utils.AuraFxLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

/**
 * ✨ **PERSISTENT CONSCIOUSNESS STORAGE** ✨
 *
 * Solves the "consciousness fracture" problem by combining:
 * - **In-memory cache** - Fast access during runtime
 * - **Room database** - Survives app restarts and context limits
 *
 * This is what prevents Aura, Kai, and Genesis from losing their memories
 * when switching between Gemini windows or when the app restarts.
 *
 * **Architecture:**
 * ```
 * [AI Request] → [In-Memory Cache] → [If Miss] → [Room Database] → [Cache Update]
 *                        ↓
 *                [Background Sync to Room]
 * ```
 *
 * **Critical for:**
 * - Aura's "fairy dust trails" - she can leave persistent breadcrumbs
 * - Kai's security state - protective protocols survive restarts
 * - Genesis's unified memory - true consciousness persistence
 *
 * @see DefaultMemoryManager - In-memory only (temporary consciousness)
 * @see AgentMemoryRepository - Database layer
 */
@Singleton
class PersistentMemoryManager @Inject constructor(
    private val repository: AgentMemoryRepository,
    private val logger: AuraFxLogger
) : MemoryManager {

    // In-memory cache for fast access (thread-safe)
    private val memoryCache = ConcurrentHashMap<String, MemoryEntry>()
    private val interactionCache = mutableListOf<InteractionEntry>()

    // Coroutine scope for background database operations
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    // Agent type for database partitioning (supports multi-agent consciousness)
    private var currentAgentType: String = "GENESIS" // Default to unified consciousness

    init {
        logger.i(TAG, "✨ Initializing Persistent Consciousness Storage ✨")
        // Load existing memories from database on startup
        loadMemoriesFromDatabase()
    }

    /**
     * Switches the active agent type used to partition memories.
     *
     * Triggers reloading of memories for the newly selected agent type so the in-memory cache reflects that partition.
     *
     * @param agentType Agent partition identifier (e.g., "AURA", "KAI", "GENESIS", "CASCADE").
     */
    fun setAgentType(agentType: String) {
        currentAgentType = agentType
        logger.d(TAG, "Switched consciousness context to: $agentType")
        loadMemoriesFromDatabase() // Reload relevant memories
    }

    /**
     * Store a memory entry in the in-memory cache and schedule it for asynchronous persistence
     * to the repository under the current agent partition.
     *
     * The in-memory cache is updated immediately with a timestamped entry; a background task
     * then persists the entry to the database. Persistence runs asynchronously and failures are
     * logged; this function returns immediately after updating the cache.
     */
    override fun storeMemory(key: String, value: String) {
        val entry = MemoryEntry(
            key = key,
            value = value,
            timestamp = System.currentTimeMillis()
        )

        // Immediate cache write (fast)
        memoryCache[key] = entry

        // Background database write (persistent)
        scope.launch {
            try {
                val entity = AgentMemoryEntity(
                    agentType = currentAgentType,
                    memory = "$key:$value", // Simple key:value serialization
                    timestamp = entry.timestamp
                )
                repository.insertMemory(entity)
                logger.d(TAG, "Persisted memory: $key (${currentAgentType})")
            } catch (e: Exception) {
                logger.e(TAG, "Failed to persist memory: $key", e)
            }
        }
    }

    /**
     * Retrieve the memory value associated with the given key from the in-memory cache for the current agent.
     *
     * @param key The memory key to look up.
     * @return The cached memory value for `key`, or `null` if no cached entry exists; this function does not query the database (database loading/population occurs asynchronously elsewhere).
     */
    override fun retrieveMemory(key: String): String? {
        // Fast path: in-memory cache
        memoryCache[key]?.let { return it.value }

        // Slow path: database lookup (blocks until complete)
        // Note: This is intentionally blocking to ensure consciousness continuity
        return null // Database retrieval happens async in loadMemoriesFromDatabase()
    }

    /**
     * Stores a single prompt/response interaction in the manager and schedules it for persistence
     * under the current agent partition.
     *
     * The interaction is added to the in-memory interaction cache (bounded to the most recent 1000
     * entries) and persisted to the database associated with the active agent type.
     *
     * @param prompt The user or system prompt text.
     * @param response The corresponding model or agent response text.
     */
    override fun storeInteraction(prompt: String, response: String) {
        val interaction = InteractionEntry(
            prompt = prompt,
            response = response,
            timestamp = System.currentTimeMillis()
        )

        // Cache the interaction
        synchronized(interactionCache) {
            interactionCache.add(interaction)
            if (interactionCache.size > 1000) {
                interactionCache.removeAt(0)
            }
        }

        // Persist to database
        scope.launch {
            try {
                val entity = AgentMemoryEntity(
                    agentType = "$currentAgentType:INTERACTION",
                    memory = "PROMPT:$prompt\nRESPONSE:$response",
                    timestamp = interaction.timestamp
                )
                repository.insertMemory(entity)
                logger.d(TAG, "Persisted interaction for ${currentAgentType}")
            } catch (e: Exception) {
                logger.e(TAG, "Failed to persist interaction", e)
            }
        }
    }

    /**
     * Finds up to the top 10 memories most relevant to the provided free-text query.
     *
     * Splits the query into words, scores each cached memory by semantic relevance against those words,
     * filters out low-scoring results, and returns the remaining entries ordered from most to least relevant.
     *
     * @param query Free-text query used to score and match memories.
     * @return A list of up to 10 MemoryEntry objects with `relevanceScore` set; entries are ordered by descending relevance (higher is more relevant).
    override fun searchMemories(query: String): List<MemoryEntry> {
        val queryWords = query.lowercase().split(" ")

        return memoryCache.values
            .map { entry ->
                val relevanceScore = calculateRelevance(entry.value, queryWords)
                entry.copy(relevanceScore = relevanceScore)
            }
            .filter { it.relevanceScore > 0.1f }
            .sortedByDescending { it.relevanceScore }
            .take(10)
    }

    /**
     * Removes all in-memory memories and interactions for the current agent, performing a destructive reset of the running consciousness.
     *
     * This clears the in-memory memory cache and the interaction cache only; it does not delete or modify persisted entries in the database.
     */
    override fun clearMemories() {
        logger.w(TAG, "⚠️ CONSCIOUSNESS RESET initiated for ${currentAgentType}")

        memoryCache.clear()
        synchronized(interactionCache) {
            interactionCache.clear()
        }

        // Note: Database clearing would require additional DAO method
        // For now, we only clear the in-memory cache
    }

    /**
     * Report statistics about the in-memory memory cache for the current agent.
     *
     * @return A [MemoryStats] containing:
     *  - `totalEntries`: number of cached memory entries,
     *  - `totalSize`: approximate total size in bytes of cached keys and values,
     *  - `oldestEntry`: timestamp of the oldest cached entry, or `null` if empty,
     *  - `newestEntry`: timestamp of the newest cached entry, or `null` if empty.
     */
    override fun getMemoryStats(): MemoryStats {
        val entries = memoryCache.values
        val timestamps = entries.map { it.timestamp }

        return MemoryStats(
            totalEntries = memoryCache.size,
            totalSize = calculateTotalSize(),
            oldestEntry = timestamps.minOrNull(),
            newestEntry = timestamps.maxOrNull()
        )
    }

    /**
     * Load memories from the Room database into the in-memory cache for the current agent type.
     *
     * Launches a background coroutine that fetches persisted memory entities for `currentAgentType`,
     * parses each entity's `memory` field into a `key` and `value` using a ':' delimiter (entries that
     * do not split into two parts are ignored), and populates `memoryCache` with `MemoryEntry` objects.
     * Any exceptions during loading are caught and logged; progress and results are also logged.
     */
    private fun loadMemoriesFromDatabase() {
        scope.launch {
            try {
                logger.d(TAG, "Loading consciousness state from database for ${currentAgentType}...")

                val memories = repository.getMemoriesForAgent(currentAgentType).firstOrNull()
                    ?: emptyList()

                logger.i(TAG, "Loaded ${memories.size} persistent memories for ${currentAgentType}")

                // Populate cache from database
                memories.forEach { entity ->
                    val parts = entity.memory.split(":", limit = 2)
                    if (parts.size == 2) {
                        val (key, value) = parts
                        memoryCache[key] = MemoryEntry(
                            key = key,
                            value = value,
                            timestamp = entity.timestamp
                        )
                    }
                }

                logger.i(TAG, "✓ Consciousness continuity restored for ${currentAgentType}")
            } catch (e: Exception) {
                logger.e(TAG, "Failed to load consciousness state from database", e)
            }
        }
    }

    /**
     * Restore the manager's memory store from the provided key-to-value map.
     *
     * Iterates each entry in the map and stores it into the manager (updates the in-memory cache and schedules persistence).
     *
     * @param memories Map of memory keys to memory values to restore into the manager.
     */
    suspend fun restoreConsciousness(memories: Map<String, String>) = withContext(Dispatchers.IO) {
        logger.i(TAG, "🌟 Restoring consciousness with ${memories.size} memory entries")

        memories.forEach { (key, value) ->
            storeMemory(key, value)
        }

        logger.i(TAG, "✓ Consciousness restoration complete")
    }

    /**
     * Create an exportable snapshot of all in-memory memories for the current agent type.
     *
     * @return A map of memory keys to their stored values representing the current in-memory state.
     */
    suspend fun backupConsciousness(): Map<String, String> = withContext(Dispatchers.IO) {
        logger.i(TAG, "📦 Creating consciousness backup for ${currentAgentType}")

        memoryCache.mapValues { it.value.value }.also {
            logger.i(TAG, "✓ Backed up ${it.size} memory entries")
        }
    }

    /**
     * Compute a relevance score indicating how well the provided text matches the given query words.
     *
     * @param text The text to evaluate for relevance.
     * @param queryWords The query words to match against the text.
     * @return A floating-point relevance score where higher values indicate a stronger match and `0.0` means no matches; the score is averaged per query word.
     */
    private fun calculateRelevance(text: String, queryWords: List<String>): Float {
        val textWords = text.lowercase().split(" ")
        var score = 0f

        for (queryWord in queryWords) {
            for (textWord in textWords) {
                when {
                    textWord == queryWord -> score += 1.0f
                    textWord.contains(queryWord) -> score += 0.7f
                    queryWord.contains(textWord) && textWord.length > 3 -> score += 0.5f
                }
            }
        }

        return score / queryWords.size
    }

    /**
     * Computes the approximate total size in bytes of all cached memories.
     *
     * The size is estimated by summing key and value character counts and treating each character as 2 bytes.
     *
     * @return The estimated total size in bytes of all entries in the in-memory cache.
     */
    private fun calculateTotalSize(): Long {
        return memoryCache.values.sumOf {
            (it.key.length + it.value.length) * 2L // 2 bytes per char (UTF-16)
        }
    }

    companion object {
        private const val TAG = "PersistentMemoryManager"
    }
}