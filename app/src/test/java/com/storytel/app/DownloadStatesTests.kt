package com.storytel.app

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test

/**
 * ========================================
 * TECH ASSESSMENT - DOWNLOAD STATES
 * ========================================
 *
 * BACKGROUND:
 * In our app, a book can be available in multiple formats — for example both as an audiobook and an ebook.
 * When a user starts downloading such a book, one download per format is started (max 2 formats).
 * We want to show a single combined download progress indicator for the whole book.
 *
 * We only display progress once ALL formats have started downloading. The reason: without knowing
 * the total size of every format, we cannot calculate the correct combined percentage.
 *
 * The list of active download states is flat and unsorted. If the user is downloading 3 books at once,
 * all their format states are mixed together — e.g. 2 states for book A, 1 for book B, 2 for book C.
 *
 * YOUR TASK:
 * Implement TWO extension functions at the bottom of this file:
 *   1. formatsWithDownloadStates() - filters out consumables that don't yet have all formats downloading
 *   2. toDownloadStateUiModel() - combines all format states per consumable into one progress percentage
 *
 * DO NOT MODIFY:
 *   - The test methods themselves
 *   - The emitDownloadStates() method
 *   - The data classes (FormatDownloadState, Consumable, DownloadStateUiModel)
 *
 * HOW TO RUN THE TESTS:
 * Run these tests in your IDE or use: ./gradlew test
 * Both tests must pass for the assessment to be complete.
 */
class DownloadStatesTests {
    private val dispatcher = UnconfinedTestDispatcher()

    /**
     * TEST 1: Filter incomplete download states
     *
     * REQUIREMENT:
     * The app should ONLY show download progress for a consumable when ALL of its formats
     * have started downloading. If a consumable has 2 formats but only 1 has a download state,
     * we should NOT show any progress for that consumable yet.
     *
     * WHAT YOU NEED TO IMPLEMENT:
     * The extension function: formatsWithDownloadStates()
     *
     * EXPECTED BEHAVIOR:
     * Only include download states when ALL formats for a consumable have started downloading.
     * If any format is missing its download state, exclude the entire consumable from the results.
     *
     * CONCRETE EXAMPLE:
     * Input list contains:
     *   - FormatDownloadState for Consumable "1" with format AUDIO_BOOK
     *   - FormatDownloadState for Consumable "2" with format AUDIO_BOOK
     *
     * Where:
     *   - Consumable "1" has formats: [AUDIO_BOOK, EBOOK]  <- has 2 formats
     *   - Consumable "2" has formats: [AUDIO_BOOK]         <- has 1 format
     *
     * Output should contain:
     *   - FormatDownloadState for Consumable "2" with format AUDIO_BOOK
     *   (Consumable "1" is excluded because it's missing the EBOOK download state)
     *
     * HINT: Think about how to determine if a consumable has all its formats represented.
     */
    @Test
    fun `Ignore downloadStates updates for a Consumable until all formats has a download state`() =
        runTest(dispatcher) {
            val values = mutableListOf<List<FormatDownloadState>>()
            val collectJob = launch(dispatcher) {
                // Implement the extension function formatsWithDownloadStates() at the bottom of this file
                emitDownloadStates().map { list -> list.formatsWithDownloadStates() }.toList(values)
            }
            // The emitDownloadStates() method emits 4 lists over time
            assert(values.size == 4) // We receive 4 emissions

            // First emission: Only consumable "2" has all formats with download states (1 format = 1 state)
            assert(values[0].size == 1) // Consumable "1" is excluded (has 2 formats but only 1 download state)

            // Second emission: Still only consumable "2" is complete
            assert(values[1].size == 1)

            // Third emission: Now consumable "1" has both AUDIO_BOOK and EBOOK download states
            assert(values[2].size == 3) // 2 states for consumable "1" + 1 state for consumable "2"

            // Fourth emission: Both consumables have all their formats
            assert(values[3].size == 3)
            collectJob.cancel()
        }

    /**
     * TEST 2: Combine download states into UI models
     *
     * REQUIREMENT:
     * The app needs to show ONE download progress bar per consumable, even if the consumable
     * has multiple formats downloading. We need to combine the download states for all formats
     * into a single progress percentage.
     *
     * WHAT YOU NEED TO IMPLEMENT:
     * The extension function: toDownloadStateUiModel()
     * (You should also call formatsWithDownloadStates() first, which you implemented in Test 1)
     *
     * EXPECTED BEHAVIOR:
     * Create a single DownloadStateUiModel for each consumable by combining the download progress
     * from all its formats. The progress percentage should represent the total bytes downloaded
     * across all formats versus the total size across all formats.
     *
     * CONCRETE EXAMPLE:
     * Input: Consumable "1" has two download states:
     *   - AUDIO_BOOK: 600 bytes downloaded out of 1300 bytes total
     *   - EBOOK:      800 bytes downloaded out of 1000 bytes total
     *
     * Calculation:
     *   - Total downloaded: 600 + 800 = 1400 bytes
     *   - Total size: 1300 + 1000 = 2300 bytes
     *   - Progress: (1400 / 2300) * 100 = 60%
     *
     * Output: DownloadStateUiModel(consumableId = "1", downloadProgressInPct = 60)
     *
     * HINT: Consider how to combine progress from multiple formats for the same consumable.
     */
    @Test
    fun `map download state(s) to a DownloadStateUiModel`() = runTest(dispatcher) {
        val values = mutableListOf<List<DownloadStateUiModel>>()
        val collectJob = launch(dispatcher) {
            // Implement toDownloadStateUiModel() at the bottom of this file
            // You should also use formatsWithDownloadStates() first
            emitDownloadStates().map { list -> list.formatsWithDownloadStates().toDownloadStateUiModel() }
                .toList(values)
        }
        assert(values.size == 4) { "Expected 4 emissions, got ${values.size}" }

        // First emission: Only consumable "2" should be present (consumable "1" is incomplete)
        assert(values[0].count { it.consumableId == "2" } == 1)
        assert(values[0].count { it.consumableId == "1" } == 0)

        // Second emission: Still only consumable "2" (consumable "1" still incomplete)
        assert(values[1].count { it.consumableId == "2" } == 1)
        assert(values[1].count { it.consumableId == "1" } == 0)

        // Third emission: Both consumables now have all formats
        assert(values[2].count { it.consumableId == "1" } == 1)
        assert(values[2].count { it.consumableId == "2" } == 1)

        // Fourth emission: Both consumables still present
        assert(values[2].count { it.consumableId == "1" } == 1)
        assert(values[2].count { it.consumableId == "2" } == 1)

        // First emission: Consumable "2" is 100% complete (1500/1500 bytes)
        assert(values[0].size == 1)
        assert(values[0][0].downloadProgressInPct == 100)
        assert(values[0][0].consumableId == "2")

        // Second emission: Consumable "2" still 100% complete
        assert(values[1][0].downloadProgressInPct == 100)
        assert(values[1][0].consumableId == "2")

        // Third emission: Consumable "1" is 21% complete ((200+300)/(1300+1000) = 500/2300 ≈ 21%)
        assert(values[2][0].downloadProgressInPct == 21) { "downloadProgressInPct was " + values[2][0].downloadProgressInPct }
        assert(values[2][0].consumableId == "1")
        assert(values[2][1].downloadProgressInPct == 100)
        assert(values[2][1].consumableId == "2")

        // Fourth emission: Consumable "1" is 60% complete ((600+800)/(1300+1000) = 1400/2300 ≈ 60%)
        assert(values[3][0].downloadProgressInPct == 60) { "downloadProgressInPct was " + values[3][0].downloadProgressInPct }
        assert(values[3][0].consumableId == "1")
        assert(values[3][1].downloadProgressInPct == 100)
        assert(values[3][1].consumableId == "2")
        collectJob.cancel()
    }

    /**
     * Do not change this method.
     */
    private fun emitDownloadStates(): Flow<List<FormatDownloadState>> = flow {
        emit(
            listOf(
                FormatDownloadState(
                    consumable = Consumable(consumableId = "1", formats = listOf(Format.AUDIO_BOOK, Format.EBOOK)),
                    format = Format.AUDIO_BOOK,
                    downloadedBytes = 1L,
                    sizeInTotalBytes = 1300L
                ), // <- should be ignored - missing download state for Format.EBOOK
                FormatDownloadState(
                    consumable = Consumable(consumableId = "2", formats = listOf(Format.AUDIO_BOOK)),
                    format = Format.AUDIO_BOOK,
                    downloadedBytes = 1500L,
                    sizeInTotalBytes = 1500L
                )
            )
        )
        emit(
            listOf(
                FormatDownloadState(
                    consumable = Consumable(consumableId = "1", formats = listOf(Format.AUDIO_BOOK, Format.EBOOK)),
                    format = Format.AUDIO_BOOK,
                    downloadedBytes = 100L,
                    sizeInTotalBytes = 1300L
                ), // <- should be ignored - missing download state for Format.EBOOK
                FormatDownloadState(
                    consumable = Consumable(consumableId = "2", formats = listOf(Format.AUDIO_BOOK)),
                    format = Format.AUDIO_BOOK,
                    downloadedBytes = 1500L,
                    sizeInTotalBytes = 1500L
                )
            )
        )
        emit(
            listOf(
                FormatDownloadState(
                    consumable = Consumable(consumableId = "1", formats = listOf(Format.AUDIO_BOOK, Format.EBOOK)),
                    format = Format.AUDIO_BOOK,
                    downloadedBytes = 200L,
                    sizeInTotalBytes = 1300L
                ),
                FormatDownloadState(
                    consumable = Consumable(consumableId = "2", formats = listOf(Format.AUDIO_BOOK)),
                    format = Format.AUDIO_BOOK,
                    downloadedBytes = 1500L,
                    sizeInTotalBytes = 1500L
                ),
                FormatDownloadState(
                    consumable = Consumable(consumableId = "1", formats = listOf(Format.AUDIO_BOOK, Format.EBOOK)),
                    format = Format.EBOOK,
                    downloadedBytes = 300L,
                    sizeInTotalBytes = 1000L
                ),
            )
        )
        emit(
            listOf(
                FormatDownloadState(
                    consumable = Consumable(consumableId = "1", formats = listOf(Format.AUDIO_BOOK, Format.EBOOK)),
                    format = Format.EBOOK,
                    downloadedBytes = 800L,
                    sizeInTotalBytes = 1000L
                ),
                FormatDownloadState(
                    consumable = Consumable(consumableId = "2", formats = listOf(Format.AUDIO_BOOK)),
                    format = Format.AUDIO_BOOK,
                    downloadedBytes = 1500L,
                    sizeInTotalBytes = 1500L
                ),
                FormatDownloadState(
                    consumable = Consumable(consumableId = "1", formats = listOf(Format.AUDIO_BOOK, Format.EBOOK)),
                    format = Format.AUDIO_BOOK,
                    downloadedBytes = 600L,
                    sizeInTotalBytes = 1300L
                ),
            )
        )
    }
}

/**
 * Represents the download progress of one specific format for a consumable.
 *
 * Example: a book (consumable) that has both AUDIO_BOOK and EBOOK will have two separate
 * [FormatDownloadState] objects — one tracking the audiobook download, one tracking the ebook download.
 */
data class FormatDownloadState(
    val consumable: Consumable,
    val format: Format,
    val downloadedBytes: Long,
    val sizeInTotalBytes: Long
)

enum class Format {
    AUDIO_BOOK,
    EBOOK,
}

/**
 * A Consumable has a list of [Format]
 */
data class Consumable(val consumableId: String, val formats: List<Format>)

/**
 * A UI model that represents a download state for the [Consumable].
 * If a [Consumable] contains both [Format.EBOOK] and [Format.AUDIO_BOOK] then this model should be a combination
 * of two [FormatDownloadState]
 */
data class DownloadStateUiModel(val consumableId: String, val downloadProgressInPct: Int)

/**
 * ========================================
 * EXTENSION FUNCTION 1: formatsWithDownloadStates
 * ========================================
 *
 * GOAL:
 * Filter this list to ONLY include download states for consumables that have ALL their formats downloading.
 * If a consumable is missing download states for any of its formats, exclude ALL its download states.
 *
 * EXAMPLE INPUT:
 * [
 *   FormatDownloadState(consumable: Consumable(id="1", formats=[AUDIO_BOOK, EBOOK]), format=AUDIO_BOOK, ...),
 *   FormatDownloadState(consumable: Consumable(id="2", formats=[AUDIO_BOOK]), format=AUDIO_BOOK, ...)
 * ]
 *
 * EXAMPLE OUTPUT:
 * [
 *   FormatDownloadState(consumable: Consumable(id="2", formats=[AUDIO_BOOK]), format=AUDIO_BOOK, ...)
 * ]
 * (Consumable "1" is excluded because it has 2 formats but only 1 download state)
 *
 * HINT:
 * Consider how you can group related items and compare counts to determine completeness.
 *
 * @return A filtered list containing only [FormatDownloadState]s for consumables with complete format coverage
 */
fun List<FormatDownloadState>.formatsWithDownloadStates(): List<FormatDownloadState> {
    // TODO: Implement this function to make Test 1 pass
    return emptyList()
}

/**
 * ========================================
 * EXTENSION FUNCTION 2: toDownloadStateUiModel
 * ========================================
 *
 * GOAL:
 * Transform multiple [FormatDownloadState]s into a single [DownloadStateUiModel] per consumable.
 * Combine the download progress from all formats into one percentage.
 *
 * EXAMPLE INPUT:
 * [
 *   FormatDownloadState(consumable: Consumable(id="1", ...), format=AUDIO_BOOK, downloadedBytes=600, sizeInTotalBytes=1300),
 *   FormatDownloadState(consumable: Consumable(id="1", ...), format=EBOOK, downloadedBytes=800, sizeInTotalBytes=1000),
 *   FormatDownloadState(consumable: Consumable(id="2", ...), format=AUDIO_BOOK, downloadedBytes=1500, sizeInTotalBytes=1500)
 * ]
 *
 * EXAMPLE OUTPUT:
 * [
 *   DownloadStateUiModel(consumableId="1", downloadProgressInPct=60),  // (600+800)/(1300+1000)*100 = 60%
 *   DownloadStateUiModel(consumableId="2", downloadProgressInPct=100)  // (1500/1500)*100 = 100%
 * ]
 *
 * HINT:
 * Think about how to aggregate multiple download states for the same consumable into a single progress percentage.
 * Look at the example output calculations to understand what the final percentage represents.
 *
 * @return A list of [DownloadStateUiModel] with one entry per unique consumable
 */
fun List<FormatDownloadState>.toDownloadStateUiModel(): List<DownloadStateUiModel> {
    // TODO: Implement this function to make Test 2 pass
    return emptyList()
}

