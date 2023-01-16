package vn.ztech.software.movie_streaming.ui.player

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import vn.ztech.software.movie_streaming.base.BaseViewModel
import vn.ztech.software.movie_streaming.data.model.Media
import vn.ztech.software.movie_streaming.data.model.MediaDetails
import vn.ztech.software.movie_streaming.data.model.MediaDetailsWatchingHistory
import vn.ztech.software.movie_streaming.data.model.StreamingResource
import vn.ztech.software.movie_streaming.data.model.StreamingStates
import vn.ztech.software.movie_streaming.data.repository.IMovieRepository

class MediaPlayerViewModel<T : Media>(private val movieRepository: IMovieRepository) :
    BaseViewModel() {

    private val _mediaDetails = MutableLiveData<MediaDetails<T>>()
    val mediaDetails: LiveData<MediaDetails<T>>
        get() = _mediaDetails

    var media = MutableLiveData<Media>()

    private val _streamingResources = MutableLiveData<StreamingResource>()
    val streamingResources: LiveData<StreamingResource> get() = _streamingResources

    private val _mediaDetailsWatchingHistory = MutableLiveData<MediaDetailsWatchingHistory>()
    val mediaDetailsWatchingHistory: LiveData<MediaDetailsWatchingHistory> get() = _mediaDetailsWatchingHistory

    val streamingStates = StreamingStates()

    fun getStreamingResources(
        episodeId: String,
        mediaId: String?,
        getStreamResourceTryCount: Int = 1
    ) {
        if (mediaId == null) return

        launchTaskAsync(
            onRequest = {
                movieRepository.getStreamingResource(
                    episodeId,
                    mediaId,
                    StreamingResource.listServers[getStreamResourceTryCount - 1]
                )
            },
            onSuccess = {
                _streamingResources.value =
                    StreamingResource(it.headers, it.sources, it.filterOutAuto())
            },
            onError = {
                if (getStreamResourceTryCount < StreamingResource.listServers.size) {
                    /**re-try loading streaming resources from other servers*/
                    getStreamingResources(episodeId, mediaId, getStreamResourceTryCount + 1)
                } else {
                    error.value = it
                }
            }
        )
    }

    fun getMediaInfo(id: String?) {

        if (id == null) return

        launchTaskAsync(
            onRequest = {
                movieRepository.getMovieInfo<T>(id)
            },
            onSuccess = {
                _mediaDetails.value = it
            },
            onError = {
                error.value = it
            }
        )
    }

    fun clearStreamingStates() {
        streamingStates.clearStates()
    }

    fun setMediaDetails(mediaDetails: MediaDetails<T>?) {
        this._mediaDetails.value = mediaDetails
    }

    fun setMediaDetailsWatchingHistory(mediaDetailsWatchingHistory: MediaDetailsWatchingHistory?) {
        this._mediaDetailsWatchingHistory.value = mediaDetailsWatchingHistory
    }
}
