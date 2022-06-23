package com.storytel.app.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.storytel.app.data.Episode
import com.storytel.app.data.Podcast

@Database(
    entities = [
        Podcast::class,
        Episode::class,
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateTimeTypeConverters::class)
abstract class StorytelDatabase : RoomDatabase() {
    abstract fun podcastsDao(): PodcastsDao
    abstract fun episodesDao(): EpisodesDao
    abstract fun transactionRunnerDao(): TransactionRunnerDao
}