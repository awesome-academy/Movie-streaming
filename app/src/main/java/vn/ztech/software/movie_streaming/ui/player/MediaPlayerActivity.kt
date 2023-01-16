package vn.ztech.software.movie_streaming.ui.player

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.MenuItem
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.PopupMenu
import android.widget.RelativeLayout
import android.widget.SeekBar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
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
import vn.ztech.software.movie_streaming.data.model.MediaDetails
import vn.ztech.software.movie_streaming.data.model.MediaDetailsWatchingHistory
import vn.ztech.software.movie_streaming.data.model.Source
import vn.ztech.software.movie_streaming.data.model.StreamingStates
import vn.ztech.software.movie_streaming.data.model.Subtitle
import vn.ztech.software.movie_streaming.databinding.ActivityMediaPlayerBinding
import vn.ztech.software.movie_streaming.utils.extensions.showAlertDialog
import vn.ztech.software.movie_streaming.utils.extensions.toBrightness
import vn.ztech.software.movie_streaming.utils.extensions.toPercentageString
import java.lang.Float.min

class MediaPlayerActivity<T : Media>() :
    BaseActivity<ActivityMediaPlayerBinding>(ActivityMediaPlayerBinding::inflate) {

    override val viewModel: MediaPlayerViewModel<T> by viewModel()
    private val watchingHistoryViewModel: WatchingHistoryViewModel by viewModel()
    private var exoPlayer: ExoPlayer? = null
    private var popupMenuQualities: PopupMenu? = null
    private var popupMenuSubtitles: PopupMenu? = null
    private var popupMenuEpisodes: PopupMenu? = null
    private var trackSelector: DefaultTrackSelector? = null

    private var layoutZoom: RelativeLayout? = null
    private var scaleDetector: ScaleGestureDetector? = null

    private val settingsLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { _ -> }

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

        scaleDetector = ScaleGestureDetector(this, ScaleDetector())

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
                    onBackPressed()
                }

                setOnTouchListener { view, motionEvent ->
                    scaleDetector?.onTouchEvent(motionEvent)
                    super.onTouchEvent(motionEvent)
                }

                seek_bar_brightness.setOnSeekBarChangeListener(object :
                    SeekBar.OnSeekBarChangeListener {
                    private var progress = 0
                    override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                        progress = p1
                    }

                    override fun onStartTrackingTouch(p0: SeekBar?) {}

                    @RequiresApi(Build.VERSION_CODES.M)
                    override fun onStopTrackingTouch(p0: SeekBar?) {

                        if (hasPermissionsToWriteSettings(this@MediaPlayerActivity)) {
                            changeBrightness(progress)
                        } else {
                            //set seekbar to previous state
                            binding?.playerView?.seek_bar_brightness?.progress =
                                viewModel.streamingStates.brightnessProgress

                            val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
                                .also { it.data = Uri.parse("package:" + context.packageName) }
                            settingsLauncher.launch(intent)
                        }
                    }
                })
            }
        }

        layoutZoom = findViewById(R.id.layout_exo_zoom)

        intent?.takeIf { it.hasExtra(BUNDLE_MEDIA_DETAILS) }?.let {
            viewModel.setMediaDetails(it.extras?.getParcelable(BUNDLE_MEDIA_DETAILS))
        }

        intent?.takeIf { it.hasExtra(BUNDLE_MEDIA_DETAILS_WATCHING_HISTORY) }?.let { intent ->
            val mediaDetailsWatchingHistory =
                intent.extras?.getParcelable<MediaDetailsWatchingHistory>(
                    BUNDLE_MEDIA_DETAILS_WATCHING_HISTORY
                )
            viewModel.setMediaDetailsWatchingHistory(
                mediaDetailsWatchingHistory
            )
        }

        intent?.takeIf { it.hasExtra(BUNDLE_MEDIA) }?.let {
            viewModel.media.value = it.extras?.getParcelable(BUNDLE_MEDIA)
        }

        viewModel.streamingStates.brightnessProgress =
            Settings.System.getInt(
                applicationContext.contentResolver,
                Settings.System.SCREEN_BRIGHTNESS,
                0
            ) * 100 / 255
        binding?.playerView?.seek_bar_brightness?.progress =
            viewModel.streamingStates.brightnessProgress

        observeData()
    }

    private fun observeData() {

        viewModel.media.observe(this) {
            if (viewModel.mediaDetailsWatchingHistory.value == null) {
                it.mGetId()?.let {
                    watchingHistoryViewModel.getMediaWatchingHistory(it)
                }
            }
        }

        viewModel.mediaDetails.observe(this) { mediaDetails ->

            if (mediaDetails.selectedEpisode == null || mediaDetails.selectedEpisode == MediaDetails.NOT_SELECTED) {
                mediaDetails.selectedEpisode =
                    viewModel.mediaDetailsWatchingHistory.value?.latestWatchingEpisodeId
                        ?: MediaDetails.NOT_SELECTED
            }

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
                                watchingHistoryViewModel.saveCurrentWatchingPosition(
                                    exoPlayer?.currentPosition ?: 0,
                                    exoPlayer?.duration ?: 0,
                                    viewModel.mediaDetails.value
                                )
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

        viewModel.mediaDetailsWatchingHistory.observe(this) {
            if (it == null) return@observe
            if (viewModel.mediaDetails.value != null) {
                viewModel.streamingStates.position =
                    it.getEpisodeWatchingHistory(viewModel.mediaDetails.value?.selectedEpisode)?.position
                        ?: 0L
            } else {
                // this case happen when clicking in list Continue Watching at Home Screen
                viewModel.streamingStates.position =
                    it.getEpisodeWatchingHistory(it.latestWatchingEpisodeId)?.position ?: 0L
            }
            if (viewModel.mediaDetails.value == null) {
                if (it.id != null) {
                    viewModel.getMediaInfo(it.id)
                } else {
                    if (viewModel.media.value?.mGetId() != null) {
                        viewModel.getMediaInfo(viewModel.media.value?.mGetId())
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

        viewModel.error.observe(this) {
            showAlertDialog(
                resources.getString(R.string.error_title),
                resources.getString(R.string.error_msg, it.message.toString())
            )
        }

        watchingHistoryViewModel.mediaDetailsWatchingHistory.observe(this) {
            viewModel.setMediaDetailsWatchingHistory(it)
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
                    quality = source.quality ?: StreamingStates.DEFAULT_STREAMING_QUALITY
                    position =
                        exoPlayer?.currentPosition ?: StreamingStates.DEFAULT_STREAMING_POSITION
                    playMedia(
                        source,
                        viewModel.streamingResources.value?.findSubtitle(viewModel.streamingStates.lang)
                    )
                }
        }
    }

    private fun changeBrightness(progress: Int) {
        val brightness = progress.toBrightness()
        Settings.System.putInt(
            applicationContext?.contentResolver,
            Settings.System.SCREEN_BRIGHTNESS_MODE,
            Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL
        )
        Settings.System.putInt(
            applicationContext?.contentResolver,
            Settings.System.SCREEN_BRIGHTNESS, brightness
        )
        viewModel.streamingStates.brightnessProgress = progress
    }

    private fun hasPermissionsToWriteSettings(context: Activity): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.System.canWrite(context)
        } else {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.WRITE_SETTINGS
            ) == PackageManager.PERMISSION_GRANTED
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

    override fun onBackPressed() {
        watchingHistoryViewModel.saveCurrentWatchingPosition(
            exoPlayer?.currentPosition ?: 0,
            exoPlayer?.duration ?: 0,
            viewModel.mediaDetails.value,
            isSaveOnStop = true
        )
        this.showAlertDialog(
            R.string.dialog_title_stop_watching_movie,
            R.string.dialog_message_stop_watching_movie,
            onClickOkListener = { _, _ -> finish() }
        )
    }

    override fun onResume() {
        super.onResume()
        exoPlayer?.apply {
            playWhenReady = true
            playbackState
        }
    }

    override fun onPause() {
        super.onPause()
        exoPlayer?.apply {
            playWhenReady = false
            playbackState
        }
    }

    override fun onStop() {
        super.onStop()
        //save latest watching position to the firebase
        watchingHistoryViewModel.saveCurrentWatchingPosition(
            exoPlayer?.currentPosition ?: 0,
            exoPlayer?.duration ?: 0,
            viewModel.mediaDetails.value,
            isSaveOnStop = true
        )
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
        const val BUNDLE_MEDIA_DETAILS_WATCHING_HISTORY = "BUNDLE_MEDIA_DETAILS_WATCHING_HISTORY"
        const val BUNDLE_MEDIA = "BUNDLE_MEDIA"
    }

    inner class ScaleDetector : ScaleGestureDetector.SimpleOnScaleGestureListener() {

        val DEFAULT_SCALE_FACTOR = 1f
        val MIN_SCALE_FACTOR = 0.5f
        val MAX_SCALE_FACTOR = 6f
        var scaleFactor = DEFAULT_SCALE_FACTOR

        override fun onScale(detector: ScaleGestureDetector): Boolean {
            scaleFactor *= detector.scaleFactor
            scaleFactor = maxOf(MIN_SCALE_FACTOR, min(scaleFactor, MAX_SCALE_FACTOR))
            layoutZoom?.apply {
                scaleX = scaleFactor
                scaleY = scaleFactor
            }
            binding?.playerView?.text_zooming_percentage?.apply {
                visibility = View.VISIBLE
                text = (scaleFactor * 100).toInt().toPercentageString()
                visibility = View.VISIBLE
            }
            return super.onScale(detector)
        }
    }
}
