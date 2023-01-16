package vn.ztech.software.movie_streaming.ui.home

import android.content.Intent
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import org.koin.androidx.viewmodel.ext.android.viewModel
import vn.ztech.software.movie_streaming.R
import vn.ztech.software.movie_streaming.base.BaseFragment
import vn.ztech.software.movie_streaming.data.model.Media
import vn.ztech.software.movie_streaming.data.model.Media.Movie
import vn.ztech.software.movie_streaming.data.model.MediaDetailsWatchingHistory
import vn.ztech.software.movie_streaming.databinding.FragmentHomeBinding
import vn.ztech.software.movie_streaming.ui.moviedetails.MediaDetailsFragment
import vn.ztech.software.movie_streaming.ui.player.MediaPlayerActivity
import vn.ztech.software.movie_streaming.ui.player.WatchingHistoryViewModel
import vn.ztech.software.movie_streaming.utils.extensions.loadImage
import vn.ztech.software.movie_streaming.utils.extensions.showAlertDialog
import vn.ztech.software.movie_streaming.utils.extensions.toMediaMyList

class HomeFragment() : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    val TAG = "HomeFragment"
    private val listMoviesTrendingAdapter by lazy { ListMediaAdapter { onMediaItemClick(it) } }
    private val listMoviesContinueWatchingAdapter by lazy {
        ListMediaContinueWatchingAdapter(
            { media -> onButtonPlayClicked(media) },
            { media -> onMediaItemClick(media) }
        )
    }
    private val listRecentMoviesAdapter by lazy { ListMediaAdapter { onMediaItemClick(it) } }
    private val listRecentShowsAdapter by lazy { ListMediaAdapter { onMediaItemClick(it) } }

    companion object {
        fun newInstance() = HomeFragment()
    }

    override val viewModel: HomeViewModel by viewModel()
    private val myListViewModel: MyListViewModel by viewModel()
    private val watchingHistoryViewModel: WatchingHistoryViewModel by viewModel()

    override fun initView() {
        binding?.apply {
            layoutListMoviesTrending.recyclerListMovies.adapter = listMoviesTrendingAdapter
            layoutListMoviesTrending.textTitle.text = getString(R.string.trending)

            layoutListMoviesContinueWatching.recyclerListMovies.adapter =
                listMoviesContinueWatchingAdapter
            layoutListMoviesContinueWatching.textTitle.text = getString(R.string.continue_watching)

            layoutListRecentMovies.recyclerListMovies.adapter = listRecentMoviesAdapter
            layoutListRecentMovies.textTitle.text = getString(R.string.recent_movies)

            layoutListRecentShows.recyclerListMovies.adapter = listRecentShowsAdapter
            layoutListRecentShows.textTitle.text = getString(R.string.recent_shows)

            layoutSearchHome.buttonSearch.setOnClickListener {
                moveToTab(R.id.menu_search)
            }

            layoutTop1Trending.apply {
                buttonAddMyList.setOnClickListener {
                    viewModel.trendingMovies.value?.results?.firstOrNull()?.toMediaMyList()
                        ?.let { myListViewModel.addToMyList(it) }
                }
                buttonRemoveMyList.setOnClickListener {
                    viewModel.trendingMovies.value?.results?.firstOrNull()?.toMediaMyList()
                        ?.let { myListViewModel.removeFromMyList(it) }
                }
                buttonPlay.setOnClickListener {
                    val intent = Intent(activity, MediaPlayerActivity::class.java)
                    intent.putExtras(bundleOf(MediaPlayerActivity.BUNDLE_MEDIA to viewModel.trendingMovies.value?.results?.firstOrNull()))
                    startActivity(intent)
                }
                buttonInfo.setOnClickListener {
                    (viewModel.trendingMovies.value?.results?.firstOrNull() as? Media)?.let {
                        onMediaItemClick(it)
                    }
                }
            }
        }
    }

    override fun initData() {
        viewModel.getTrendingMovies()
        viewModel.getRecentMovies()
        viewModel.getRecentShows()
        watchingHistoryViewModel.getListContinueWatching()
    }

    override fun bindData() {
        //TODO  implement later
    }

    override fun observeData() {
        viewModel.trendingMovies.observe(this) {
            it.results?.let { listMovies ->
                listMoviesTrendingAdapter.submitData(listMovies)
                myListViewModel.isThisMediaAlreadyInMyList(it.results.first().id)
                showTop1TrendingMovie(listMovies.first() as Movie)
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

        myListViewModel.addToMyListState.observe(this) {
            when (it) {
                HomeViewModel.Companion.AddToMyListStates.ADDED -> {
                    binding?.layoutTop1Trending?.buttonAddMyList?.visibility = View.INVISIBLE
                    binding?.layoutTop1Trending?.buttonRemoveMyList?.isVisible = true
                }
                else -> {
                    binding?.layoutTop1Trending?.buttonAddMyList?.visibility = View.VISIBLE
                    binding?.layoutTop1Trending?.buttonRemoveMyList?.visibility = View.INVISIBLE
                }
            }
        }

        myListViewModel.isMediaInMyList.observe(this) {
            when (it) {
                true -> {
                    binding?.layoutTop1Trending?.buttonAddMyList?.visibility = View.INVISIBLE
                    binding?.layoutTop1Trending?.buttonRemoveMyList?.isVisible = true
                }
                else -> {
                    binding?.layoutTop1Trending?.buttonAddMyList?.visibility = View.VISIBLE
                    binding?.layoutTop1Trending?.buttonRemoveMyList?.visibility = View.INVISIBLE
                }
            }
        }

        watchingHistoryViewModel.listContinueWatching.observe(this) {
            if (it.isNullOrEmpty()) {
                binding?.layoutListMoviesContinueWatching?.constrainContainer?.visibility =
                    View.GONE
            } else {
                binding?.layoutListMoviesContinueWatching?.constrainContainer?.visibility =
                    View.VISIBLE
                listMoviesContinueWatchingAdapter.submitData(it)
            }
        }

        watchingHistoryViewModel.loadingListContinueWatching.observe(this) {
            binding?.layoutListMoviesContinueWatching?.layoutLoading?.constrainLoading?.isVisible =
                it
        }
    }

    private fun showTop1TrendingMovie(top1Movie: Movie) {
        binding?.layoutTop1Trending?.apply {
            imageCover.loadImage(top1Movie.image)
            layoutImageCoverBlur.imageCoverBlur.loadImage(top1Movie.image)
            textName.text = top1Movie.title
        }
    }

    private fun onMediaItemClick(media: Media) {
        when (media.getMediaClass()) {
            Media.OBJECT_TYPE_MOVIE -> {
                openFragment(MediaDetailsFragment.newInstance<Movie>(media))
            }
            Media.OBJECT_TYPE_TV_SHOW -> {
                openFragment(MediaDetailsFragment.newInstance<Media.Show>(media))
            }
        }
    }

    private fun onMediaItemClick(mediaDetailsWatchingHistory: MediaDetailsWatchingHistory) {
        val media = mediaDetailsWatchingHistory.toMedia()
        when (media.getMediaClass()) {
            Media.OBJECT_TYPE_MOVIE -> {
                openFragment(MediaDetailsFragment.newInstance<Movie>(media))
            }
            Media.OBJECT_TYPE_TV_SHOW -> {
                openFragment(MediaDetailsFragment.newInstance<Media.Show>(media))
            }
        }
    }

    private fun onButtonPlayClicked(media: MediaDetailsWatchingHistory) {
        val intent = Intent(activity, MediaPlayerActivity::class.java)
        intent.putExtras(bundleOf(MediaPlayerActivity.BUNDLE_MEDIA_DETAILS_WATCHING_HISTORY to media))
        startActivity(intent)
    }

    override fun refreshData() {
        watchingHistoryViewModel.getListContinueWatching(showLoading = false)
    }

    override fun onResume() {
        refreshData()
        super.onResume()
    }
}
