package com.storytel.app.data

import androidx.room.Embedded
import androidx.room.Ignore
import androidx.room.Relation

class EpisodeToPodcast {
    @Embedded
    lateinit var episode: Episode

    @Relation(parentColumn = "podcast_uri", entityColumn = "uri")
    lateinit var _podcasts: List<Podcast>

    @get:Ignore
    val podcast: Podcast
        get() = _podcasts[0]

    /**
     * Allow consumers to destructure this class
     */
    operator fun component1() = episode
    operator fun component2() = podcast
}