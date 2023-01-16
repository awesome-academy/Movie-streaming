package vn.ztech.software.movie_streaming.ui.moviedetails

import android.content.Intent
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import com.google.gson.internal.LinkedTreeMap
import org.koin.androidx.viewmodel.ext.android.viewModel
import vn.ztech.software.movie_streaming.R
import vn.ztech.software.movie_streaming.base.BaseFragment
import vn.ztech.software.movie_streaming.data.model.Episode
import vn.ztech.software.movie_streaming.data.model.Media
import vn.ztech.software.movie_streaming.data.model.Media.Movie
import vn.ztech.software.movie_streaming.data.model.MediaDetails
import vn.ztech.software.movie_streaming.data.model.MediaDetailsWatchingHistory
import vn.ztech.software.movie_streaming.databinding.FragmentMovieDetailsBinding
import vn.ztech.software.movie_streaming.ui.MainActivity
import vn.ztech.software.movie_streaming.ui.home.HomeViewModel
import vn.ztech.software.movie_streaming.ui.home.ListMediaAdapter
import vn.ztech.software.movie_streaming.ui.home.MyListViewModel
import vn.ztech.software.movie_streaming.ui.player.MediaPlayerActivity
import vn.ztech.software.movie_streaming.ui.player.WatchingHistoryViewModel
import vn.ztech.software.movie_streaming.utils.extensions.loadImage
import vn.ztech.software.movie_streaming.utils.extensions.toListGroupedBySeason
import vn.ztech.software.movie_streaming.utils.extensions.toListRecommendations
import vn.ztech.software.movie_streaming.utils.extensions.toMediaMyList

class MediaDetailsFragment<T : Media>() :
    BaseFragment<FragmentMovieDetailsBinding>(FragmentMovieDetailsBinding::inflate) {

    private val listEpisodesAdapter by lazy { ListEpisodesAdapter { onEpisodeClicked(it) } }
    private val listRecommendationsAdapter by lazy { ListMediaAdapter { onMediaItemClick(it) } }
    override val viewModel: MovieDetailsViewModel<T> by viewModel()
    private val myListViewModel: MyListViewModel by viewModel()
    private val watchingHistoryViewModel: WatchingHistoryViewModel by viewModel()

    override fun initView() {
        binding?.apply {
            (activity as? MainActivity)?.apply {
                setSupportActionBar(toolbar)
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
                toolbar.title = ""
            }

            buttonPlay.setOnClickListener {
                viewModel.mediaDetails.value?.let {
                    val latestEpisodeId =
                        watchingHistoryViewModel.mediaDetailsWatchingHistory.value?.latestWatchingEpisodeId
                            ?: it.episodes?.first()?.id

                    viewModel.mediaDetails.value?.selectedEpisode = latestEpisodeId

                    val intent = Intent(activity, MediaPlayerActivity::class.java)
                    intent.putExtras(bundleOf(MediaPlayerActivity.BUNDLE_MEDIA_DETAILS to it.removeRecommendation()))
                    intent.putExtras(bundleOf(MediaPlayerActivity.BUNDLE_MEDIA_DETAILS_WATCHING_HISTORY to watchingHistoryViewModel.mediaDetailsWatchingHistory.value))
                    startActivity(intent)
                }
            }

            buttonAddToMyList.setOnClickListener {
                (viewModel.mediaDetails.value as? MediaDetails<Media>)?.toMediaMyList()
                    ?.let { myListViewModel.addToMyList(it) }
            }

            buttonRemoveFromMyList.setOnClickListener {
                (viewModel.mediaDetails.value as? MediaDetails<Media>)?.toMediaMyList()
                    ?.let { myListViewModel.removeFromMyList(it) }
            }

        }
    }

    override fun initData() {
        arguments?.takeIf { it.containsKey(BUNDLE_MEDIA) }?.let {
            viewModel.media.value = it.getParcelable<Movie>(BUNDLE_MEDIA)
        }
    }

    override fun bindData() {
        //TODO implement later
    }

    override fun observeData() {
        viewModel.media.observe(this) { media ->
            media.mGetId()?.let { viewModel.getMediaInfo(it) }
        }

        viewModel.mediaDetails.observe(this) { mediaDetails ->
            mediaDetails?.let {
                myListViewModel.isThisMediaAlreadyInMyList(it.id)
                watchingHistoryViewModel.getMediaWatchingHistory(it.id)
            }
            bindUI(mediaDetails)
        }

        viewModel.loading.observe(this) { isLoading ->
            binding?.layoutLoading?.constrainLoading?.isVisible = isLoading
            binding?.layoutLoading2?.constrainLoading?.isVisible = isLoading
        }

        myListViewModel.addToMyListState.observe(this) {
            when (it) {
                HomeViewModel.Companion.AddToMyListStates.ADDED -> {
                    binding?.buttonAddToMyList?.visibility = View.INVISIBLE
                    binding?.buttonRemoveFromMyList?.isVisible = true
                }
                HomeViewModel.Companion.AddToMyListStates.REMOVED -> {
                    binding?.buttonAddToMyList?.visibility = View.VISIBLE
                    binding?.buttonRemoveFromMyList?.visibility = View.INVISIBLE
                }
                else -> {}
            }
        }

        myListViewModel.isMediaInMyList.observe(this) {
            when (it) {
                true -> {
                    binding?.buttonAddToMyList?.visibility = View.INVISIBLE
                    binding?.buttonRemoveFromMyList?.isVisible = true
                }
                else -> {
                    binding?.buttonAddToMyList?.visibility = View.VISIBLE
                    binding?.buttonRemoveFromMyList?.visibility = View.INVISIBLE
                }
            }
        }

        watchingHistoryViewModel.mediaDetailsWatchingHistory.observe(this) {
            it?.let {
                when (viewModel.media.value?.getMediaClass()) {
                    Media.OBJECT_TYPE_MOVIE -> {
                        showWatchingHistoryMovie(it)
                    }
                    Media.OBJECT_TYPE_TV_SHOW -> {
                        updateInfoArea(it)
                        showWatchingHistoryShow(it)
                    }
                }
            }
        }
    }

    private fun updateInfoArea(mediaDetailsWatchingHistory: MediaDetailsWatchingHistory) {
        binding?.layoutBasicInfo?.tvTvShowInfo?.text =
            mediaDetailsWatchingHistory.getTVShowEpisodeInfoFull()
        mediaDetailsWatchingHistory.getTVShowEpisodeInfo().let {
            if (it.isNotEmpty()) binding?.buttonPlay?.text =
                resources.getString(R.string.format_button_play, it)
        }
    }

    private fun showWatchingHistoryMovie(history: MediaDetailsWatchingHistory) {
        history.getCurrentWatchingPercentage().let {
            binding?.layoutBasicInfo?.progressBarWatchingPosition?.progress = it
        }
    }

    private fun showWatchingHistoryShow(history: MediaDetailsWatchingHistory) {
        viewModel.updateEpisodes(history)
        viewModel.mediaDetails.value?.episodes?.let { listEpisodesAdapter.setData(it.toListGroupedBySeason()) }
    }

    private fun bindUI(mediaDetails: MediaDetails<T>) {
        binding?.apply {
            layoutBasicInfo.apply {
                imageCover.loadImage(mediaDetails.cover)
                textTitle.text = mediaDetails.title
                textYear.text = mediaDetails.releaseDate
                textDuration.text = mediaDetails.duration
                textRating.text = mediaDetails.rating.toString()
            }

            layoutAdvancedInfo.apply {
                textDescription.text = mediaDetails.description
                textCast.text =
                    resources.getString(R.string.format_casts, mediaDetails.casts.toString())
                textGenres.text =
                    resources.getString(R.string.format_genres, mediaDetails.genres.toString())
                textProduction.text =
                    resources.getString(R.string.format_production, mediaDetails.production)
                textCountry.text =
                    resources.getString(R.string.format_country, mediaDetails.country)
            }

            if (mediaDetails.isTVShow()) {
                layoutMovieDetailsEpisodes.constrainContainer.visibility = View.VISIBLE
                layoutMovieDetailsEpisodes.recyclerListEpisodes.adapter = listEpisodesAdapter
                mediaDetails.episodes?.toListGroupedBySeason()
                    ?.let { listEpisodesAdapter.setData(it) }
            }
            layoutListRecommendations.apply {
                textTitle.text = "Recommendation"
                recyclerListMovies.adapter = listRecommendationsAdapter
                listRecommendationsAdapter.submitData((mediaDetails.recommendations as List<LinkedTreeMap<String, String>>).toListRecommendations())
            }
        }
    }

    private fun onEpisodeClicked(episode: Episode) {
        viewModel.mediaDetails.value?.let {
            it.selectedEpisode = episode.id

            val intent = Intent(activity, MediaPlayerActivity::class.java)
            intent.putExtras(bundleOf(MediaPlayerActivity.BUNDLE_MEDIA_DETAILS to it.removeRecommendation()))
            intent.putExtras(bundleOf(MediaPlayerActivity.BUNDLE_MEDIA_DETAILS_WATCHING_HISTORY to watchingHistoryViewModel.mediaDetailsWatchingHistory.value))
            startActivity(intent)
        }
    }

    private fun onMediaItemClick(media: Media) {
        when (media.getMediaClass()) {
            Media.OBJECT_TYPE_MOVIE -> {
                openFragment(newInstance<Movie>(media))
            }
            Media.OBJECT_TYPE_TV_SHOW -> {
                openFragment(newInstance<Media.Show>(media))
            }
        }
    }

    override fun refreshData() {
        viewModel.mediaDetails.value?.id?.let { watchingHistoryViewModel.getMediaWatchingHistory(it) }
    }

    override fun onResume() {
        refreshData()
        super.onResume()
    }

    companion object {
        fun <T : Media> newInstance(media: Media): MediaDetailsFragment<T> {
            return MediaDetailsFragment<T>().apply {
                arguments = bundleOf(BUNDLE_MEDIA to media)
            }
        }

        const val BUNDLE_MEDIA = "BUNDLE_MEDIA"
    }
}
