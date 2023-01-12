package vn.ztech.software.movie_streaming.ui.moviedetails

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import vn.ztech.software.movie_streaming.base.BaseViewHolder
import vn.ztech.software.movie_streaming.data.model.Episode
import vn.ztech.software.movie_streaming.data.model.EpisodeData
import vn.ztech.software.movie_streaming.data.model.Season
import vn.ztech.software.movie_streaming.databinding.LayoutEpisodeItemBinding
import vn.ztech.software.movie_streaming.databinding.LayoutSeasonItemBinding
import vn.ztech.software.movie_streaming.utils.Constant

class ListEpisodesAdapter(private val listener: (Episode) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val data = mutableListOf<EpisodeData>()

    fun setData(data: List<EpisodeData>) {
        this.data.clear()
        this.data.addAll(data)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            VIEW_TYPE_SEASON -> {
                val view = LayoutSeasonItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                SeasonViewHolder(view)
            }
            else -> {
                val view = LayoutEpisodeItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                EpisodeViewHolder(view, listener)
            }
        }
    }

    class EpisodeViewHolder(
        private val binding: LayoutEpisodeItemBinding,
        private val listener: (Episode) -> Unit
    ) : BaseViewHolder<Episode, LayoutEpisodeItemBinding>(binding) {
        override fun bind(data: Episode) {
            binding.tvTitle.text = String.format(Constant.FORMAT_EPISODE_STRING, data.title)
            binding.root.setOnClickListener { listener(data) }
        }
    }

    class SeasonViewHolder(private val binding: LayoutSeasonItemBinding) :
        BaseViewHolder<Season, LayoutSeasonItemBinding>(binding) {
        override fun bind(data: Season) {
            binding.textTitle.text = data.name
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            VIEW_TYPE_SEASON -> {
                (holder as SeasonViewHolder).bind(data[position] as Season)
            }
            VIEW_TYPE_EPISODE -> {
                (holder as EpisodeViewHolder).bind(data[position] as Episode)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (data[position] is Episode) VIEW_TYPE_EPISODE else VIEW_TYPE_SEASON
    }

    override fun getItemCount(): Int {
        return data.size
    }

    companion object {
        private const val VIEW_TYPE_EPISODE = 2
        private const val VIEW_TYPE_SEASON = 1
    }
}
