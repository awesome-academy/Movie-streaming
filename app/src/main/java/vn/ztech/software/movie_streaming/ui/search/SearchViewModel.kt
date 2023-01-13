package vn.ztech.software.movie_streaming.ui.search

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import vn.ztech.software.movie_streaming.base.BaseViewModel
import vn.ztech.software.movie_streaming.data.model.BaseAPIResponse
import vn.ztech.software.movie_streaming.data.model.Media
import vn.ztech.software.movie_streaming.data.repository.IMovieRepository
import vn.ztech.software.movie_streaming.utils.Constant

class SearchViewModel(private val movieRepository: IMovieRepository) : BaseViewModel() {

    private var response = BaseAPIResponse(Constant.FIRST_PAGE, false, emptyList())
    private var currentKeyword: String = ""
    private val _media = MutableLiveData<List<Media>>(listOf())
    val media: LiveData<List<Media>> get() = _media
    private var loadingCount = 0
    private val _customLoading = MutableLiveData<Boolean>(false)
    val customLoading: LiveData<Boolean> get() = _customLoading

    fun search(keyword: String, page: Int = Constant.FIRST_PAGE) {
        currentKeyword = keyword

        launchTaskAsync(
            onRequest = {
                if (loadingCount == 0) _customLoading.value = true
                loadingCount++
                movieRepository.search(keyword, page)
            },
            onSuccess = {
                response = it
                if (page != Constant.FIRST_PAGE) _media.value = _media.value?.plus(it.results?.toTypedArray()?: emptyArray())
                else {
                    _media.value = it.results
                }
                loadingCount--
                if (loadingCount == 0) _customLoading.value = false
            },
            onError = {
                error.value = it
                loadingCount--
                if (loadingCount == 0) _customLoading.value = false
            }
        )
    }

    fun loadMore(){
        if (response.hasNextPage == true && currentKeyword.isNotEmpty()){
            response.currentPage?.let {
                search(currentKeyword, it + 1)
            }
        }
    }
}
