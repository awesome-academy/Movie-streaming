package vn.ztech.software.movie_streaming.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Episode(
    val id: String,
    val number: Int,
    val season: Long = SEASON_NONE,
    val title: String,
    val url: String,
    var position: Long = 0L,
    var duration: Long = 0L
) : EpisodeData(), Parcelable {
    override fun mGetId(): String {
        return id
    }

    fun getTVShowInfo(): String {
        return String.format(FORMAT_TV_SHOW_INFO, season, title)
    }

    fun getWatchingPercentage(): Int {
        if (this.duration == 0L) return 0
        return ((this.position * 100) / this.duration).toInt()
    }

    companion object {
        private const val FORMAT_TV_SHOW_INFO = "Season: %1s | %1s"
        const val SEASON_NONE = -1L
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

@Parcelize
data class EpisodeWatchingHistory(
    val id: String?,
    val title: String?,
    val season: Long? = 0L,
    val position: Long? = 0L,
    val duration: Long? = 0L
) : Parcelable {
    companion object {
        const val id = "id"
        const val title = "title"
        const val season = "season"
        const val position = "position"
        const val duration = "duration"
    }
}

abstract class EpisodeData {
    abstract fun mGetId(): String
}
