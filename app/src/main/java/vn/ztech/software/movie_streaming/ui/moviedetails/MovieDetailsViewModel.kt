package vn.ztech.software.movie_streaming.ui.moviedetails

import androidx.lifecycle.MutableLiveData
import vn.ztech.software.movie_streaming.base.BaseViewModel
import vn.ztech.software.movie_streaming.data.model.Media
import vn.ztech.software.movie_streaming.data.model.MediaDetails
import vn.ztech.software.movie_streaming.data.repository.IMovieRepository

class MovieDetailsViewModel<T : Media>(private val movieRepository: IMovieRepository) :
    BaseViewModel() {

    var media = MutableLiveData<Media>()
    val mediaDetails = MutableLiveData<MediaDetails<T>>()

    fun getMediaInfo(id: String) {
        launchTaskAsync(
            onRequest = {
                movieRepository.getMovieInfo<T>(id)
            },
            onSuccess = {
                mediaDetails.value = it
            },
            onError = {
                error.value = it
            }
        )
    }
}
