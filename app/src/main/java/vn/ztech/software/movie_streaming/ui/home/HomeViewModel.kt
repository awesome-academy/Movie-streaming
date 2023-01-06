package vn.ztech.software.movie_streaming.ui.home

import android.util.Log
import androidx.lifecycle.MutableLiveData
import vn.ztech.software.movie_streaming.base.BaseViewModel
import vn.ztech.software.movie_streaming.data.model.BaseAPIResponse
import vn.ztech.software.movie_streaming.data.repository.IMovieRepository

class HomeViewModel(private val movieRepository: IMovieRepository): BaseViewModel() {

    val trendingMovies = MutableLiveData<BaseAPIResponse>()
    val error = MutableLiveData<Exception>()

    fun getTrendingMovies(){
        Log.d("XXX","XXX")
        launchTaskAsync<BaseAPIResponse>(
            onRequest = {
                movieRepository.getTrendingMovies()
            },
            onSuccess = {
                trendingMovies.value = it
            },
            onError = {
                error.value = it
            }
        )
    }
}
