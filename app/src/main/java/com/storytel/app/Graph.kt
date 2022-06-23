
package com.storytel.app

import android.content.Context
import androidx.room.Room
import com.storytel.app.data.EpisodeStore
import com.storytel.app.data.PodcastStore
import com.storytel.app.data.PodcastsFetcher
import com.storytel.app.data.room.StorytelDatabase
import com.storytel.app.data.room.TransactionRunner
import com.rometools.rome.io.SyndFeedInput
import com.storytel.app.data.PodcastsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.LoggingEventListener
import java.io.File

/**
 * A very simple global singleton dependency graph.
 *
 * For a real app, you would use something like Hilt/Dagger instead.
 */
object Graph {
    lateinit var okHttpClient: OkHttpClient

    lateinit var database: StorytelDatabase
        private set

    private val transactionRunner: TransactionRunner
        get() = database.transactionRunnerDao()

    private val syndFeedInput by lazy { SyndFeedInput() }

    val podcastRepository by lazy {
        PodcastsRepository(
            podcastsFetcher = podcastFetcher,
            podcastStore = podcastStore,
            episodeStore = episodeStore,
            transactionRunner = transactionRunner,
            mainDispatcher = mainDispatcher
        )
    }

    private val podcastFetcher by lazy {
        PodcastsFetcher(
            okHttpClient = okHttpClient,
            syndFeedInput = syndFeedInput,
            backgroundDispatcher = backgroundDispatcher
        )
    }

    val podcastStore by lazy {
        PodcastStore(
            podcastDao = database.podcastsDao(),
        )
    }

    val episodeStore by lazy {
        EpisodeStore(
            episodesDao = database.episodesDao()
        )
    }

    private val mainDispatcher: CoroutineDispatcher
        get() = Dispatchers.Main

    private val backgroundDispatcher: CoroutineDispatcher
        get() = runBlocking {
            delay(3000)
            Dispatchers.Main
        }

    fun provide(context: Context) {
        okHttpClient = OkHttpClient.Builder()
            .cache(Cache(File(context.cacheDir, "http_cache"), (20 * 1024 * 1024).toLong()))
            .apply {
                if (BuildConfig.DEBUG) eventListenerFactory(LoggingEventListener.Factory())
            }
            .build()

        database = Room.databaseBuilder(context, StorytelDatabase::class.java, "data.db")
            // This is not recommended for normal apps, but the goal of this sample isn't to
            // showcase all of Room.
            .fallbackToDestructiveMigration()
            .build()
    }
}