package vn.ztech.software.movie_streaming.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import vn.ztech.software.movie_streaming.base.BaseAdapter
import vn.ztech.software.movie_streaming.base.BaseViewHolder
import vn.ztech.software.movie_streaming.data.model.Media
import vn.ztech.software.movie_streaming.data.model.MediaDetailsWatchingHistory
import vn.ztech.software.movie_streaming.databinding.LayoutMovieContinueWatchingItemBinding
import vn.ztech.software.movie_streaming.utils.Constant
import vn.ztech.software.movie_streaming.utils.extensions.loadImage

class ListMediaContinueWatchingAdapter(
    private val onButtonPlayClicked: (MediaDetailsWatchingHistory) -> Unit,
    private val onButtonInfoClicked: (MediaDetailsWatchingHistory) -> Unit
) : BaseAdapter<
        MediaDetailsWatchingHistory,
        LayoutMovieContinueWatchingItemBinding,
        ListMediaContinueWatchingAdapter.MediaContinueWatchingViewHolder>(MediaDifferCallback()) {

    fun submitData(media: List<MediaDetailsWatchingHistory>) {
        submitList(media)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MediaContinueWatchingViewHolder {
        val view =
            LayoutMovieContinueWatchingItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return MediaContinueWatchingViewHolder(view, onButtonPlayClicked, onButtonInfoClicked)
    }

    class MediaContinueWatchingViewHolder(
        private val binding: LayoutMovieContinueWatchingItemBinding,
        private val onButtonPlayClicked: (MediaDetailsWatchingHistory) -> Unit,
        private val onButtonInfoClicked: (MediaDetailsWatchingHistory) -> Unit
    ) : BaseViewHolder<MediaDetailsWatchingHistory, LayoutMovieContinueWatchingItemBinding>(binding) {
        override fun bind(data: MediaDetailsWatchingHistory) {
            binding.apply {
                ivCover.apply {
                    data.image?.let { loadImage(it) }
                }
                progressBarWatchingPosition.progress = data.getCurrentWatchingPercentage()
                if (data.type == Media.OBJECT_TYPE_TV_SHOW) {
                    textTvShowInfo.text = data.getTVShowEpisodeInfo()
                } else {
                    textTvShowInfo.text = Constant.EMPTY_STRING
                }
                buttonPlay.setOnClickListener {
                    onButtonPlayClicked(data)
                }
                buttonInfo.setOnClickListener {
                    onButtonInfoClicked(data)
                }
            }
        }
    }

    private class MediaDifferCallback : DiffUtil.ItemCallback<MediaDetailsWatchingHistory>() {
        override fun areItemsTheSame(
            oldItem: MediaDetailsWatchingHistory,
            newItem: MediaDetailsWatchingHistory
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: MediaDetailsWatchingHistory,
            newItem: MediaDetailsWatchingHistory
        ): Boolean {
            return oldItem == newItem
        }
    }
}
