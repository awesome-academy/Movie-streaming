package vn.ztech.software.movie_streaming.utils.extensions

import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import vn.ztech.software.movie_streaming.R

fun ImageView.loadImage(
    url: String,
    @DrawableRes drawablePlaceHolder: Int = R.drawable.img_movie
) {
    Glide
        .with(this)
        .load(url)
        .centerCrop()
        .placeholder(drawablePlaceHolder)
        .error(drawablePlaceHolder)
        .into(this)
}

fun ImageView.loadImage(
    res: Int,
    @DrawableRes drawablePlaceHolder: Int = R.drawable.img_movie
) {
    Glide
        .with(this)
        .load(res)
        .centerCrop()
        .placeholder(drawablePlaceHolder)
        .error(drawablePlaceHolder)
        .into(this)
}
