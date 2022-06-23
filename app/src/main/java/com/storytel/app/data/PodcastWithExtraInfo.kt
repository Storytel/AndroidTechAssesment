package com.storytel.app.data

import androidx.room.ColumnInfo
import androidx.room.Embedded
import java.time.OffsetDateTime
import java.util.Objects

class PodcastWithExtraInfo {
    @Embedded
    lateinit var podcast: Podcast

    @ColumnInfo(name = "last_episode_date")
    var lastEpisodeDate: OffsetDateTime? = null

    operator fun component1() = podcast
    operator fun component2() = lastEpisodeDate

    override fun equals(other: Any?): Boolean = when {
        other === this -> true
        other is PodcastWithExtraInfo -> {
            podcast == other.podcast &&
                lastEpisodeDate == other.lastEpisodeDate
        }
        else -> false
    }

    override fun hashCode(): Int = Objects.hash(podcast, lastEpisodeDate)
}
