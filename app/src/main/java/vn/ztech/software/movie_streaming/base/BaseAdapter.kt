package vn.ztech.software.movie_streaming.base

import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.viewbinding.ViewBinding
import java.util.concurrent.Executors

abstract class BaseAdapter<T, VB: ViewBinding, VH: BaseViewHolder<T, VB>>(diffCallBack: DiffUtil.ItemCallback<T>)
    :ListAdapter<T, VH>(AsyncDifferConfig.Builder<T>(diffCallBack)
        .setBackgroundThreadExecutor(Executors.newSingleThreadExecutor())
        .build()
    ){

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(currentList[position])
    }
}
