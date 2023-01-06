package vn.ztech.software.movie_streaming.ui.home

import androidx.core.view.isVisible
import org.koin.androidx.viewmodel.ext.android.viewModel
import vn.ztech.software.movie_streaming.R
import vn.ztech.software.movie_streaming.base.BaseFragment
import vn.ztech.software.movie_streaming.data.model.Media
import vn.ztech.software.movie_streaming.data.model.Media.Movie
import vn.ztech.software.movie_streaming.databinding.FragmentHomeBinding
import vn.ztech.software.movie_streaming.ui.moviedetails.MediaDetailsFragment
import vn.ztech.software.movie_streaming.utils.extensions.loadImage
import vn.ztech.software.movie_streaming.utils.extensions.showAlertDialog

class HomeFragment() : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    val TAG = "HomeFragment"
    private val listMoviesTrendingAdapter by lazy { ListMediaAdapter { onMediaItemClick(it) } }
    private val listRecentMoviesAdapter by lazy { ListMediaAdapter { onMediaItemClick(it) } }
    private val listRecentShowsAdapter by lazy { ListMediaAdapter { onMediaItemClick(it) } }

    companion object {
        fun newInstance() = HomeFragment()
    }

    override val viewModel: HomeViewModel by viewModel()

    override fun initView() {
        binding?.apply {
            layoutListMoviesTrending.recyclerListMovies.adapter = listMoviesTrendingAdapter
            layoutListMoviesTrending.textTitle.text = getString(R.string.trending)

            layoutListRecentMovies.recyclerListMovies.adapter = listRecentMoviesAdapter
            layoutListRecentMovies.textTitle.text = getString(R.string.recent_movies)

            layoutListRecentShows.recyclerListMovies.adapter = listRecentShowsAdapter
            layoutListRecentShows.textTitle.text = getString(R.string.recent_shows)

            layoutSearchHome.buttonSearch.setOnClickListener {
                moveToTab(R.id.menu_search)
            }
        }
    }

    override fun initData() {
        viewModel.getTrendingMovies()
        viewModel.getRecentMovies()
        viewModel.getRecentShows()
    }

    override fun bindData() {
        //TODO  implement later
    }

    override fun observeData() {
        viewModel.trendingMovies.observe(this) {
            it.results?.let {
                listMoviesTrendingAdapter.submitData(it)
                showTop1TrendingMovie(it.first() as Movie)
            }
        }

        viewModel.recentMovies.observe(this) {
            listRecentMoviesAdapter.submitData(it)
        }

        viewModel.recentShows.observe(this) {
            listRecentShowsAdapter.submitData(it)
        }

        viewModel.error.observe(this) {
            context?.showAlertDialog(
                getString(R.string.error_title),
                String.format(resources.getString(R.string.error_msg), it.message)
            )
        }

        viewModel.loadingTrendingMovies.observe(this) {
            binding?.layoutListMoviesTrending?.layoutLoading?.constrainLoading?.isVisible = it
        }

        viewModel.loadingRecentMovies.observe(this) {
            binding?.layoutListRecentMovies?.layoutLoading?.constrainLoading?.isVisible = it
        }

        viewModel.loadingRecentShows.observe(this) {
            binding?.layoutListRecentShows?.layoutLoading?.constrainLoading?.isVisible = it
        }
    }

    private fun showTop1TrendingMovie(top1Movie: Movie) {
        binding?.layoutTop1Trending?.apply {
            imageCover.loadImage(top1Movie.image)
            layoutIvCoverBlur.imageCoverBlur.loadImage(top1Movie.image)

            textName.text = top1Movie.title
        }
    }

    private fun onMediaItemClick(media: Media) {
        openFragment(MediaDetailsFragment.newInstance(media))
    }
}
