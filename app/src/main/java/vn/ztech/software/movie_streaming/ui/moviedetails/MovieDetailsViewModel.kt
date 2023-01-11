package vn.ztech.software.movie_streaming.ui.moviedetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import vn.ztech.software.movie_streaming.base.BaseViewModel
import vn.ztech.software.movie_streaming.data.model.Media

class MovieDetailsViewModel: BaseViewModel() {
    var media = MutableLiveData<Media>()
}
