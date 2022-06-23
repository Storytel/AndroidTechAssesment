package com.storytel.app.ui.home

import com.storytel.app.data.Episode
import com.storytel.app.data.Podcast
import java.time.OffsetDateTime
import java.time.ZoneOffset

val PreviewPodcasts = listOf(
    Podcast(
        uri = "fakeUri://podcast/1",
        title = "Android Developers Backstage",
        author = "Android Developers"
    ),
    Podcast(
        uri = "fakeUri://podcast/2",
        title = "Google Developers podcast",
        author = "Google Developers"
    )
)

val PreviewEpisodes = listOf(
    Episode(
        uri = "fakeUri://episode/1",
        podcastUri = PreviewPodcasts[0].uri,
        title = "Episode 140: Bubbles!",
        published = OffsetDateTime.of(2020, 6, 2, 9, 27, 0, 0, ZoneOffset.of("-0800"))
    )
)