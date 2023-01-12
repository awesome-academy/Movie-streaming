package vn.ztech.software.movie_streaming.ui.player

import android.net.Uri
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.util.MimeTypes
import com.google.common.collect.ImmutableList
import kotlinx.android.synthetic.main.layout_custom_controller_exo_player.*
import kotlinx.android.synthetic.main.layout_custom_controller_exo_player.view.*
import kotlinx.android.synthetic.main.layout_movie_player_info.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import vn.ztech.software.movie_streaming.R
import vn.ztech.software.movie_streaming.base.BaseActivity
import vn.ztech.software.movie_streaming.data.model.Media
import vn.ztech.software.movie_streaming.data.model.Source
import vn.ztech.software.movie_streaming.data.model.StreamingStates
import vn.ztech.software.movie_streaming.data.model.Subtitle
import vn.ztech.software.movie_streaming.databinding.ActivityMediaPlayerBinding
import vn.ztech.software.movie_streaming.utils.extensions.showAlertDialog

class MediaPlayerActivity<T : Media>() :
    BaseActivity<ActivityMediaPlayerBinding>(ActivityMediaPlayerBinding::inflate) {

    override val viewModel: MediaPlayerViewModel<T> by viewModel()
    private var exoPlayer: ExoPlayer? = null
    private var popupMenuQualities: PopupMenu? = null
    private var popupMenuSubtitles: PopupMenu? = null
    private var popupMenuEpisodes: PopupMenu? = null
    private var trackSelector: DefaultTrackSelector? = null

    override fun initialize() {
        WindowCompat.getInsetsController(window, window.decorView).apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        trackSelector = DefaultTrackSelector(this)
        exoPlayer =
            ExoPlayer.Builder(this).setTrackSelector(trackSelector!!).setSeekBackIncrementMs(10000)
                .setSeekForwardIncrementMs(10000).build()
        exoPlayer?.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_BUFFERING -> {
                        binding?.progressBar?.visibility = View.VISIBLE
                    }
                    Player.STATE_READY -> {
                        binding?.progressBar?.visibility = View.GONE
                    }
                }
            }
        })

        binding?.apply {
            playerView.player = exoPlayer
            playerView.apply {
                button_quality.apply {
                    popupMenuQualities = PopupMenu(this@MediaPlayerActivity, this)
                    setOnClickListener {
                        popupMenuQualities?.show()
                    }
                }
                button_subtitle.apply {
                    popupMenuSubtitles = PopupMenu(this@MediaPlayerActivity, this)
                    setOnClickListener {
                        popupMenuSubtitles?.show()
                    }
                }

                button_list_episodes.apply {
                    popupMenuEpisodes = PopupMenu(this@MediaPlayerActivity, this)
                    setOnClickListener {
                        popupMenuEpisodes?.show()
                    }
                }

                button_back.setOnClickListener {
                    context?.showAlertDialog(
                        R.string.title_exit_player,
                        R.string.msg_exit_player,
                        onClickOkListener = { _, _ ->
                            finish()
                        }
                    )
                }
            }

        }

        intent?.takeIf { it.hasExtra(BUNDLE_MEDIA_DETAILS) }?.let {
            viewModel.setMediaDetails(it.extras?.getParcelable(BUNDLE_MEDIA_DETAILS))
        }

        observeData()
    }

    private fun observeData() {

        viewModel.mediaDetails.observe(this) { mediaDetails ->
            val selectedEpisode = mediaDetails.getSelectedEpisode()
            selectedEpisode?.let { episode ->
                viewModel.getStreamingResources(episode.id, mediaDetails?.id)
                binding?.apply {
                    playerView.layout_movie_info.text_movie_title.text =
                        viewModel.mediaDetails.value?.title

                    val hasMultipleEpisode = (mediaDetails.episodes?.size ?: 0) > 1
                    if (hasMultipleEpisode) {
                        playerView.text_tv_show_info.text = episode.getTVShowInfo()

                        button_list_episodes.isVisible = true
                        popupMenuEpisodes?.menu?.clear()
                        mediaDetails.episodes?.forEach {
                            popupMenuEpisodes?.menu?.add(it.getTVShowInfo())
                        }
                        popupMenuEpisodes?.setOnMenuItemClickListener {
                            val clickedEpisode = mediaDetails.findEpisode(it.title.toString())
                            clickedEpisode?.let {
                                viewModel.clearStreamingStates()
                                viewModel.getStreamingResources(it.id, mediaDetails.id)
                                viewModel.mediaDetails.value?.selectedEpisode = clickedEpisode.id
                                playerView.text_tv_show_info.text = clickedEpisode.getTVShowInfo()
                            }
                            true
                        }
                    }
                }
            }
        }

        viewModel.streamingResources.observe(this) { streamingResource ->
            streamingResource?.let {
                popupMenuQualities?.menu?.clear()
                it.sources?.forEach {
                    popupMenuQualities?.menu?.add(it.quality)
                }
                popupMenuQualities?.setOnMenuItemClickListener {
                    it?.let { changeQuality(it) }
                    true
                }
                popupMenuSubtitles?.menu?.clear()
                it.subtitles?.forEach {
                    popupMenuSubtitles?.menu?.add(it.lang)
                }
                popupMenuSubtitles?.menu?.add(Subtitle.TURN_OFF_SUBTITLE)
                popupMenuSubtitles?.setOnMenuItemClickListener {
                    it?.let { changeSubtitle(it) }
                    true
                }
                playMedia(
                    streamingResource.findResourceByQuality(viewModel.streamingStates.quality),
                    streamingResource.findSubtitle(viewModel.streamingStates.lang)
                )
            }
        }

        viewModel.loading.observe(this) {
        }
    }

    private fun changeSubtitle(menu: MenuItem) {
        if (menu.title.toString() == Subtitle.TURN_OFF_SUBTITLE && menu.title != viewModel.streamingStates.lang) {
            val source =
                viewModel.streamingResources.value?.findResourceByQuality(viewModel.streamingStates.quality)
            viewModel.streamingStates.apply {
                position = exoPlayer?.currentPosition ?: StreamingStates.DEFAULT_STREAMING_POSITION
                lang = StreamingStates.STREAMING_LANG_TURN_OFF
            }
            playMedia(source, null)
            return
        }

        val sub = viewModel.streamingResources.value?.findSubtitle(menu.title.toString())
        sub?.let {
            if (sub.lang != viewModel.streamingStates.lang) {
                viewModel.streamingStates.apply {
                    lang = sub.lang ?: StreamingStates.DEFAULT_STREAMING_LANG
                    position =
                        exoPlayer?.currentPosition ?: StreamingStates.DEFAULT_STREAMING_POSITION
                }
                val source =
                    viewModel.streamingResources.value?.findResourceByQuality(viewModel.streamingStates.quality)
                playMedia(source, viewModel.streamingResources.value?.findSubtitle(sub.lang))
            }
        }
    }

    private fun changeQuality(it: MenuItem) {
        val source = viewModel.streamingResources.value?.findResourceByQuality(it.title.toString())
        source?.let {
            if (source.quality != viewModel.streamingStates.quality)
                viewModel.streamingStates.apply {
                    quality = source.quality?:StreamingStates.DEFAULT_STREAMING_QUALITY
                    position =
                        exoPlayer?.currentPosition ?: StreamingStates.DEFAULT_STREAMING_POSITION
                    playMedia(
                        source,
                        viewModel.streamingResources.value?.findSubtitle(viewModel.streamingStates.lang)
                    )
                }
        }
    }

    private fun playMedia(source: Source?, subtitle: Subtitle?) {
        if (source == null) return

        var sub: MediaItem.SubtitleConfiguration? = null
        subtitle?.let {
            sub = MediaItem.SubtitleConfiguration.Builder(Uri.parse(subtitle?.url))
                .setMimeType(MimeTypes.TEXT_VTT).setSelectionFlags(C.SELECTION_FLAG_DEFAULT).build()
        }

        val mediaItemBuilder = MediaItem.Builder().setUri(source.url)
        sub?.let {
            mediaItemBuilder.setSubtitleConfigurations(ImmutableList.of(it))
        }

        exoPlayer?.let {
            it.clearMediaItems()
            it.addMediaItem(mediaItemBuilder.build())
            it.prepare()
            it.seekTo(viewModel.streamingStates.position)
            it.playWhenReady = true
        }
    }

    override fun onPause() {
        super.onPause()
        exoPlayer?.apply {
            playWhenReady = false
            playbackState
        }
    }

    override fun onResume() {
        super.onResume()
        exoPlayer?.apply {
            playWhenReady = true
            playbackState
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        releasePlayer()
    }

    private fun releasePlayer() {
        exoPlayer?.let { exoPlayer ->
            exoPlayer.release()
        }
        exoPlayer = null
    }

    companion object {
        const val BUNDLE_MEDIA_DETAILS = "BUNDLE_MEDIA_DETAILS"
    }
}
