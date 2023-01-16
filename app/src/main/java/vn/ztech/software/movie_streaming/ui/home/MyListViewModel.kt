package vn.ztech.software.movie_streaming.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import vn.ztech.software.movie_streaming.base.BaseViewModel
import vn.ztech.software.movie_streaming.data.model.MediaMyList
import vn.ztech.software.movie_streaming.data.repository.IMovieRepository

class MyListViewModel(private val movieRepository: IMovieRepository) : BaseViewModel() {

    private val _addToMyListState = MutableLiveData(HomeViewModel.Companion.AddToMyListStates.NONE)
    val addToMyListState: LiveData<HomeViewModel.Companion.AddToMyListStates> get() = _addToMyListState

    private val _myList = MutableLiveData<List<MediaMyList>>()
    val myList: LiveData<List<MediaMyList>> get() = _myList

    private val _isMediaInMyList = MutableLiveData(false)
    val isMediaInMyList: LiveData<Boolean> get() = _isMediaInMyList

    private val _loadingMyList = MutableLiveData(false)
    val loadingMyList: LiveData<Boolean> get() = _loadingMyList

    fun addToMyList(mediaMyList: MediaMyList) {
        launchTaskAsync<Boolean>(
            onRequest = {
                _addToMyListState.value = HomeViewModel.Companion.AddToMyListStates.ADDING
                movieRepository.addToMyList(mediaMyList)
            },
            onSuccess = {
                if (it) _addToMyListState.value = HomeViewModel.Companion.AddToMyListStates.ADDED
            },
            onError = {
                error.value = it
                _addToMyListState.value = HomeViewModel.Companion.AddToMyListStates.FAILED
            }
        )
    }

    fun removeFromMyList(mediaMyList: MediaMyList) {
        launchTaskAsync<Boolean>(
            onRequest = {
                _addToMyListState.value = HomeViewModel.Companion.AddToMyListStates.ADDING
                movieRepository.removeFromMyList(mediaMyList)
            },
            onSuccess = {
                if (it) _addToMyListState.value = HomeViewModel.Companion.AddToMyListStates.REMOVED
            },
            onError = {
                error.value = it
                _addToMyListState.value = HomeViewModel.Companion.AddToMyListStates.FAILED
            }
        )
    }

    fun getMyList(showLoading: Boolean = true) {
        launchTaskAsync<List<MediaMyList>>(
            onRequest = {
                _loadingMyList.value = showLoading
                movieRepository.getMyList()
            },
            onSuccess = {
                _myList.value = it
                _loadingMyList.value = false
            },
            onError = {
                error.value = it
                _loadingMyList.value = false
            }
        )
    }

    fun isThisMediaAlreadyInMyList(id: String?) {

        if (id == null) return

        launchTaskAsync<Boolean>(
            onRequest = {
                movieRepository.isMediaInMyList(id)
            },
            onSuccess = {
                _isMediaInMyList.value = it
            },
            onError = {
                error.value = it
                _isMediaInMyList.value = false
            }
        )
    }
}
