package com.storytel.app.data

import com.storytel.app.data.room.EpisodesDao
import kotlinx.coroutines.flow.Flow

class EpisodeStore(
    private val episodesDao: EpisodesDao
) {
    fun podcastEpisodes(
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<EpisodeToPodcast>> {
        return episodesDao.podcastEpisodes(limit)
    }

    suspend fun addEpisodes(episodes: List<Episode>) {
        episodesDao.insertAll(episodes)
    }
}
