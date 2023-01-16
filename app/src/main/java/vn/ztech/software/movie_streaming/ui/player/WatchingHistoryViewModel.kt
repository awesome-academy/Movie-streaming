package vn.ztech.software.movie_streaming.ui.player

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import vn.ztech.software.movie_streaming.base.BaseViewModel
import vn.ztech.software.movie_streaming.data.model.Media
import vn.ztech.software.movie_streaming.data.model.MediaDetails
import vn.ztech.software.movie_streaming.data.model.MediaDetailsWatchingHistory
import vn.ztech.software.movie_streaming.data.repository.IMovieRepository

class WatchingHistoryViewModel(private val movieRepository: IMovieRepository) : BaseViewModel() {

    private val _mediaDetailsWatchingHistory = MutableLiveData<MediaDetailsWatchingHistory>()
    val mediaDetailsWatchingHistory: LiveData<MediaDetailsWatchingHistory> get() = _mediaDetailsWatchingHistory

    private val _listContinueWatching = MutableLiveData<List<MediaDetailsWatchingHistory>>()
    val listContinueWatching: LiveData<List<MediaDetailsWatchingHistory>> get() = _listContinueWatching

    private val _loadingListContinueWatching = MutableLiveData(false)
    val loadingListContinueWatching: LiveData<Boolean> get() = _loadingListContinueWatching

    fun getListContinueWatching(showLoading: Boolean = true) {
        launchTaskAsync(
            onRequest = {
                _loadingListContinueWatching.value = showLoading
                movieRepository.getListContinueWatching()
            },
            onSuccess = {
                _listContinueWatching.value = it
                _loadingListContinueWatching.value = false
            },
            onError = {
                error.value = it
                _loadingListContinueWatching.value = false
            }
        )
    }

    fun <T : Media> saveCurrentWatchingPosition(
        currentPosition: Long,
        duration: Long,
        mediaDetails: MediaDetails<T>?,
        isSaveOnStop: Boolean = false
    ) {

        if (mediaDetails == null) return

        val episodeWatchingHistory =
            mediaDetails.toEpisodeWatchingHistory(currentPosition, duration)
        val mediaDetailsWatchingHistory =
            mediaDetails.toMediaDetailsWatchingHistory(_mediaDetailsWatchingHistory.value)

        launchTaskAsync<Boolean>(
            onRequest = {
                /** To preserve the works of updating current watching history in a coroutine (preventing coroutine from
                being destroy when we click back button and the activity is destroyed),
                use NonCancellable to make sure that the coroutine stay alive after the Activity is destroyed.
                The NonCancellable coroutine will be destroyed after the code block inside it is finished, that means
                our desire works has been done.*/
                if (isSaveOnStop) withContext(Dispatchers.IO + NonCancellable) {
                    movieRepository.saveCurrentWatchingHistory(
                        mediaDetailsWatchingHistory,
                        episodeWatchingHistory
                    )
                }
                else movieRepository.saveCurrentWatchingHistory(
                    mediaDetailsWatchingHistory,
                    episodeWatchingHistory
                )
            },
            onSuccess = {
            },
            onError = {
            }
        )
    }

    fun getMediaWatchingHistory(id: String?) {

        if (id == null) return

        launchTaskAsync(
            onRequest = {
                movieRepository.getMediaWatchingHistory(id)
            },
            onSuccess = {
                _mediaDetailsWatchingHistory.value = it
            },
            onError = {
            }
        )
    }
}
