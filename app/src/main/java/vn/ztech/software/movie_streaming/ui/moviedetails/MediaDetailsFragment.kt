package vn.ztech.software.movie_streaming.ui.moviedetails

import androidx.core.os.bundleOf
import org.koin.androidx.viewmodel.ext.android.viewModel
import vn.ztech.software.movie_streaming.base.BaseFragment
import vn.ztech.software.movie_streaming.data.model.Media
import vn.ztech.software.movie_streaming.data.model.Media.Movie
import vn.ztech.software.movie_streaming.databinding.FragmentMovieDetailsBinding

class MediaDetailsFragment() :
    BaseFragment<FragmentMovieDetailsBinding>(FragmentMovieDetailsBinding::inflate) {

    override val viewModel: MovieDetailsViewModel by viewModel()

    override fun initView() {
        //TODO  implement later
    }

    override fun initData() {
        arguments?.takeIf { it.containsKey(BUNDLE_MOVIE) }?.let {
            viewModel.media.value = it.getParcelable<Movie>(BUNDLE_MOVIE)
        }
    }

    override fun bindData() {
        //TODO  implement later
    }

    override fun observeData() {
        viewModel.media.observe(this) {
            binding?.text?.text = it.toString()
        }
    }

    companion object {
        fun newInstance(media: Media) = MediaDetailsFragment().apply {
            arguments = bundleOf(BUNDLE_MOVIE to media)
        }

        const val BUNDLE_MOVIE = "BUNDLE_MOVIE"
    }
}
