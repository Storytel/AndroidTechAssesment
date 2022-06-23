package com.storytel.app.data

import androidx.room.ColumnInfo
import androidx.room.Embedded
import java.time.OffsetDateTime

class PodcastWithExtraInfo {
    @Embedded
    lateinit var podcast: Podcast

    @ColumnInfo(name = "last_episode_date")
    var lastEpisodeDate: OffsetDateTime? = null

    operator fun component1() = podcast
    operator fun component2() = lastEpisodeDate
}