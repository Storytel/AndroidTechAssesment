package com.storytel.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.storytel.app.Graph
import com.storytel.app.data.EpisodeStore
import com.storytel.app.data.EpisodeToPodcast
import com.storytel.app.data.PodcastStore
import com.storytel.app.data.PodcastWithExtraInfo
import com.storytel.app.data.PodcastsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PodcastScreenViewModel(
    private val podcastsRepository: PodcastsRepository = Graph.podcastRepository,
    private val podcastStore: PodcastStore = Graph.podcastStore,
    private val episodeStore: EpisodeStore = Graph.episodeStore
) : ViewModel() {
    private val _state = MutableStateFlow(PodcastCategoryViewState())

    val state: StateFlow<PodcastCategoryViewState>
        get() = _state

    init {
        viewModelScope.launch {
            podcastsRepository.updatePodcasts()
            val recentPodcastsFlow = podcastStore.allPodcasts(limit = 20)
            val episodesFlow = episodeStore.podcastEpisodes(limit = 50)

            var podcastList = emptyList<PodcastWithExtraInfo>()
            var episodeList = emptyList<EpisodeToPodcast>()

            recentPodcastsFlow.collect {
                _state.value = PodcastCategoryViewState(podcastList, episodeList)
                podcastList = it
                episodesFlow.collect {
                    episodeList = it
                    _state.value = PodcastCategoryViewState(podcastList, episodeList)
                }
            }
        }
    }
}

data class PodcastCategoryViewState(
    val topPodcasts: List<PodcastWithExtraInfo> = emptyList(),
    val episodes: List<EpisodeToPodcast> = emptyList()
)