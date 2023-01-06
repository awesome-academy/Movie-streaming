package vn.ztech.software.movie_streaming.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import vn.ztech.software.movie_streaming.base.BaseViewModel
import vn.ztech.software.movie_streaming.data.model.BaseAPIResponse
import vn.ztech.software.movie_streaming.data.model.Media.Movie
import vn.ztech.software.movie_streaming.data.model.Media.Show
import vn.ztech.software.movie_streaming.data.repository.IMovieRepository

class HomeViewModel(private val movieRepository: IMovieRepository) : BaseViewModel() {

    private val _trendingMovies = MutableLiveData<BaseAPIResponse>()
    private val _loadingTrendingMovies = MutableLiveData<Boolean>()
    private val _recentMovies = MutableLiveData<List<Movie>>()
    private val _loadingRecentMovies = MutableLiveData<Boolean>()
    private val _recentShows = MutableLiveData<List<Show>>()
    private val _loadingRecentShows = MutableLiveData<Boolean>()
    private val _error = MutableLiveData<Exception>()

    val trendingMovies: LiveData<BaseAPIResponse> get() = _trendingMovies
    val loadingTrendingMovies: LiveData<Boolean> = _loadingTrendingMovies
    val recentMovies: LiveData<List<Movie>> = _recentMovies
    val loadingRecentMovies: LiveData<Boolean> = _loadingRecentMovies
    val recentShows: LiveData<List<Show>> = _recentShows
    val loadingRecentShows: LiveData<Boolean> = _loadingRecentShows
    val error: LiveData<Exception> = _error

    fun getTrendingMovies() {
        launchTaskAsync<BaseAPIResponse>(
            onRequest = {
                _loadingTrendingMovies.value = true
                movieRepository.getTrendingMovies()
            },
            onSuccess = {
                _trendingMovies.value = it
                _loadingTrendingMovies.value = false
            },
            onError = {
                _error.value = it
                _loadingTrendingMovies.value = false
            }
        )
    }

    fun getRecentMovies() {
        launchTaskAsync<List<Movie>>(
            onRequest = {
                _loadingRecentMovies.value = true
                movieRepository.getRecentMovies()
            },
            onSuccess = {
                _recentMovies.value = it
                _loadingRecentMovies.value = false
            },
            onError = {
                _error.value = it
                _loadingRecentMovies.value = false
            }
        )
    }

    fun getRecentShows() {
        launchTaskAsync<List<Show>>(
            onRequest = {
                _loadingRecentShows.value = true
                movieRepository.getRecentShows()
            },
            onSuccess = {
                _recentShows.value = it
                _loadingRecentShows.value = false
            },
            onError = {
                _error.value = it
                _loadingRecentShows.value = false
            }
        )
    }
}
