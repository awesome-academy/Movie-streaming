package vn.ztech.software.movie_streaming.ui.player

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import vn.ztech.software.movie_streaming.base.BaseViewModel
import vn.ztech.software.movie_streaming.data.model.Media
import vn.ztech.software.movie_streaming.data.model.MediaDetails
import vn.ztech.software.movie_streaming.data.model.StreamingResource
import vn.ztech.software.movie_streaming.data.model.StreamingStates
import vn.ztech.software.movie_streaming.data.repository.IMovieRepository

class MediaPlayerViewModel<T : Media>(private val movieRepository: IMovieRepository) :
    BaseViewModel() {

    private val _mediaDetails = MutableLiveData<MediaDetails<T>>()
    val mediaDetails: LiveData<MediaDetails<T>>
    get() = _mediaDetails

    private val _streamingResources = MutableLiveData<StreamingResource>()
    val streamingResources: LiveData<StreamingResource> get() = _streamingResources
    val streamingStates = StreamingStates()

    fun getStreamingResources(episodeId: String, mediaId: String?) {
        if (mediaId == null) return

        launchTaskAsync(
            onRequest = {
                movieRepository.getStreamingResource(episodeId, mediaId)
            },
            onSuccess = {
                _streamingResources.value =
                    StreamingResource(it.headers, it.sources, it.filterOutAuto())
            },
            onError = {
                error.value = it
            }
        )
    }

    fun clearStreamingStates() {
        streamingStates.clearStates()
    }

    fun setMediaDetails(mediaDetails: MediaDetails<T>?){
        this._mediaDetails.value = mediaDetails
    }

}
