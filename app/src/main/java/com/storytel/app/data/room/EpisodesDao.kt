package com.storytel.app.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.storytel.app.data.Episode
import com.storytel.app.data.EpisodeToPodcast
import kotlinx.coroutines.flow.Flow

@Dao
abstract class EpisodesDao {

    @Transaction
    @Query(
        """
        SELECT episodes.* FROM episodes
        ORDER BY datetime(published) DESC
        LIMIT :limit
        """
    )
    abstract fun podcastEpisodes(
        limit: Int
    ): Flow<List<EpisodeToPodcast>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertAll(entities: Collection<Episode>)
}
