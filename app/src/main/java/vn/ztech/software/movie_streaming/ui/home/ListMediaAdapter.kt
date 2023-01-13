package vn.ztech.software.movie_streaming.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import vn.ztech.software.movie_streaming.base.BaseAdapter
import vn.ztech.software.movie_streaming.base.BaseViewHolder
import vn.ztech.software.movie_streaming.data.model.Media
import vn.ztech.software.movie_streaming.databinding.LayoutMovieItemBinding
import vn.ztech.software.movie_streaming.utils.extensions.loadImage

class ListMediaAdapter(private val listener: (Media) -> Unit) : BaseAdapter<
        Media,
        LayoutMovieItemBinding,
        ListMediaAdapter.MediaViewHolder>(MediaDifferCallback()) {

    fun submitData(media: List<Media>) {
        submitList(media)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder {
        val view =
            LayoutMovieItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MediaViewHolder(view, listener)
    }

    class MediaViewHolder(
        private val binding: LayoutMovieItemBinding,
        private val listener: (Media) -> Unit
    ) : BaseViewHolder<Media, LayoutMovieItemBinding>(binding) {
        override fun bind(data: Media) {
            binding.imageCover.apply {
                data.getImg()?.let { loadImage(it) }
                setOnClickListener { listener(data) }
            }
        }
    }

    private class MediaDifferCallback : DiffUtil.ItemCallback<Media>() {
        override fun areItemsTheSame(oldItem: Media, newItem: Media): Boolean {
            return oldItem.mGetId() == newItem.mGetId()
        }

        override fun areContentsTheSame(oldItem: Media, newItem: Media): Boolean {
            return oldItem.mGetId() == newItem.mGetId()
        }
    }
}
