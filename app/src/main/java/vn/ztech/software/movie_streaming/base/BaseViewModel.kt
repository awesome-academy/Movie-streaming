package vn.ztech.software.movie_streaming.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import vn.ztech.software.movie_streaming.utils.DataResult

open class BaseViewModel : ViewModel() {
    private var _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> get() = _loading

    protected fun <T> launchTaskAsync(
        onRequest: suspend CoroutineScope.() -> DataResult<T>,
        onSuccess: (T) -> Unit = {},
        onError: (Exception) -> Unit = {},
        loading: LoadingState = LoadingState.LOADING
    ) = viewModelScope.launch {
        updateLoading(loading)

        when(val task = onRequest(this)){
            is DataResult.Success -> {
                onSuccess(task.data)
            }
            is DataResult.Error -> {
                onError(task.exception)
            }
            else -> {}
        }

        updateLoading(LoadingState.NOT_LOADING)
    }

    private fun updateLoading(state: LoadingState){
        _loading.value = state.value
    }
    companion object{
        enum class LoadingState(val value: Boolean = false){
            LOADING(true), NOT_LOADING(false)
        }
    }
}
