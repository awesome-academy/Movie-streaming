package vn.ztech.software.movie_streaming.base

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.viewbinding.ViewBinding

abstract class BaseViewHolder<T, VB: ViewBinding>(binding: VB)
    : ViewHolder(binding.root) {

    abstract fun bind(data: T)

}
