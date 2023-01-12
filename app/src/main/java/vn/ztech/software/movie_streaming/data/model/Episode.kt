package vn.ztech.software.movie_streaming.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Episode(
    val id: String,
    val number: Int,
    val season: Int,
    val title: String,
    val url: String
) : EpisodeData(), Parcelable {
    override fun mGetId(): String {
        return id
    }

    fun getTVShowInfo(): String {
        return String.format(FORMAT_TV_SHOW_INFO, season, title)
    }

    companion object {
        private const val FORMAT_TV_SHOW_INFO = "Season: %1s | %1s"
    }
}

@Parcelize
data class Season(
    val name: String?
) : EpisodeData(), Parcelable {
    override fun mGetId(): String {
        return name ?: ""
    }
}

abstract class EpisodeData {
    abstract fun mGetId(): String
}
