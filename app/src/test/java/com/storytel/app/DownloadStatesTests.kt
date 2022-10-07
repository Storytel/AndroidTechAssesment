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
 * A [Consumable] is a representation of a Book or a Podcast.
 * A [Consumable] has a List of [Format]s. For each [Format] there can be a [FormatDownloadState]. So when the [Consumable]
 * is being downloaded, 1 download for each [Format] for that [Consumable] is started.
 * The app wants to show a combined download progress for all formats for the [Consumable]. In this test case, we want to wait
 * until there is a download state for all [Format]s before showing any download state updates in the app.
 *
 * In the first test case, we want a list of download states where there is a download state available for each [Format]
 * in the [Consumable].
 *
 * In the second test case, we want a list of [DownloadStateUiModel], where all download states for a [Consumable] is
 * combined into 1 [DownloadStateUiModel]
 */
class DownloadStatesTests {
    private val dispatcher = UnconfinedTestDispatcher()

    /**
     * The method [emitDownloadStates] emits Lists of [FormatDownloadState] objects. The method [formatsWithDownloadStates]
     * should only return [Consumable]s with [Format]s that also has a [FormatDownloadState].
     * So if a [Consumable] has [Format.EBOOK] and [Format.AUDIO_BOOK], but only a [FormatDownloadState] for the
     * [Format.AUDIO_BOOK] is available, then it should not be in the list that that method [formatsWithDownloadStates]
     * returns.
     * Implement [formatsWithDownloadStates] so this test passes without changing anything but [formatsWithDownloadStates]
     */
    @Test
    fun `Ignore downloadStates updates for a Consumable until all formats has a download state`() =
        runTest(dispatcher) {
            val values = mutableListOf<List<FormatDownloadState>>()
            val collectJob = launch(dispatcher) {
                //implement the method formatsWithDownloadStates so that the unit test will pass
                emitDownloadStates().map { list -> list.formatsWithDownloadStates() }.toList(values)
            }
            assert(values.size == 4) //expect 4 lists
            assert(values[0].size == 1) //the first list contain 1 [FormatDownloadState] since consumable 1 is still waiting for all its formats to have a [FormatDownloadState]
            assert(values[1].size == 1)
            assert(values[2].size == 3)
            assert(values[3].size == 3)
            collectJob.cancel()
        }

    /**
     * Implement [toDownloadStateUiModel] so this test passes without changing anything but [toDownloadStateUiModel]
     *
     */
    @Test
    fun `map download state(s) to a DownloadStateUiModel`() = runTest(dispatcher) {
        val values = mutableListOf<List<DownloadStateUiModel>>()
        val collectJob = launch(dispatcher) {
            //implement(or reuse) the method formatsWithDownloadStates and implement toDownloadStateUiModel so that the unit test will pass
            emitDownloadStates().map { list -> list.formatsWithDownloadStates().toDownloadStateUiModel() }
                .toList(values)
        }
        assert(values.size == 4) { "list was $values" }
        assert(values[0].count { it.consumableId == "2" } == 1)
        assert(values[0].count { it.consumableId == "1" } == 0)
        assert(values[1].count { it.consumableId == "2" } == 1)
        assert(values[1].count { it.consumableId == "1" } == 0)
        assert(values[2].count { it.consumableId == "1" } == 1)
        assert(values[2].count { it.consumableId == "2" } == 1)
        assert(values[2].count { it.consumableId == "1" } == 1)
        assert(values[2].count { it.consumableId == "2" } == 1)

        assert(values[0].size == 1)
        assert(values[0][0].downloadProgressInPct == 100)
        assert(values[0][0].consumableId == "2")

        assert(values[1][0].downloadProgressInPct == 100)
        assert(values[1][0].consumableId == "2")

        assert(values[2][0].downloadProgressInPct == 21) { "downloadProgressInPct was " + values[2][0].downloadProgressInPct }
        assert(values[2][0].consumableId == "1")
        assert(values[2][1].downloadProgressInPct == 100)
        assert(values[2][1].consumableId == "2")

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
 * Download state for a [Format]. A Consumable can have 1 or 2 [Format]s
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
 * @return a list of [FormatDownloadState]s where each [Format] in a [Consumable] has a [FormatDownloadState].
 * If a Format is missing a download state, then that FormatDownloadState should not be present in the list
 */
fun List<FormatDownloadState>.formatsWithDownloadStates(): List<FormatDownloadState> {
    return emptyList()
}

/**
 * A [Consumable] can have multiple [FormatDownloadState]s. This method combines all download states into 1
 * [DownloadStateUiModel] per [Consumable], so the app can present 1 download state for each [Consumable].
 * The user should not know that when downloading, there is actually multiple downloads ongoing.
 * We want to present it as a single download.
 * @return a List of [DownloadStateUiModel]. There must only be returned one [DownloadStateUiModel] per [Consumable]
 */
fun List<FormatDownloadState>.toDownloadStateUiModel(): List<DownloadStateUiModel> {
    return emptyList()
}

