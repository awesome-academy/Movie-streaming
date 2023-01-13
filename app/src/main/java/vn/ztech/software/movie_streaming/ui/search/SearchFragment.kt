package vn.ztech.software.movie_streaming.ui.search

import android.util.Log
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import org.koin.androidx.viewmodel.ext.android.viewModel
import vn.ztech.software.movie_streaming.base.BaseFragment
import vn.ztech.software.movie_streaming.data.model.Media
import vn.ztech.software.movie_streaming.databinding.FragmentSearchBinding
import vn.ztech.software.movie_streaming.ui.home.ListMediaAdapter
import vn.ztech.software.movie_streaming.ui.moviedetails.MediaDetailsFragment
import vn.ztech.software.movie_streaming.utils.extensions.toast

class SearchFragment() : BaseFragment<FragmentSearchBinding>(FragmentSearchBinding::inflate) {

    override val viewModel: SearchViewModel by viewModel()
    private val listMediaAdapter by lazy { ListMediaAdapter{ media -> onItemClick(media) }}

    override fun initView() {
        binding?.apply {
            layoutToolBar.editTextSearch.doOnTextChanged { text, start, before, count ->
                if(count > 0){
                    viewModel.search(text.toString())
                }
            }
            recyclerListMovies.adapter = listMediaAdapter
            listMediaAdapter.setOnLoadMoreListener(recyclerListMovies){
                viewModel.loadMore()
            }
        }
    }

    override fun initData() {
        //TODO  implement later
    }

    override fun bindData() {
        //TODO  implement later
    }

    override fun observeData() {
        viewModel.media.observe(this){
            listMediaAdapter.submitData(it)
        }

        viewModel.customLoading.observe(this){
            binding?.layoutToolBar?.progressBarLoading?.isVisible = it
        }
    }

    private fun onItemClick(media: Media) {
        when (media.getMediaClass()) {
            Media.OBJECT_TYPE_MOVIE -> {
                openFragment(MediaDetailsFragment.newInstance<Media.Movie>(media))
            }
            Media.OBJECT_TYPE_TV_SHOW -> {
                openFragment(MediaDetailsFragment.newInstance<Media.Show>(media))
            }
        }
    }

    companion object {
        fun newInstance() = SearchFragment()
    }
}
