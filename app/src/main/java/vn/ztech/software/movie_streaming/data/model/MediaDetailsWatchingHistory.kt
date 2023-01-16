package vn.ztech.software.movie_streaming.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MediaDetailsWatchingHistory(
    val id: String? = null,
    val image: String? = null,
    val type: String? = null,
    val episodesHistory: List<EpisodeWatchingHistory>? = null,
    val latestWatchingEpisodeId: String? = null
) : Parcelable {
    fun toMapObj(): Map<String, String?> {
        val map = hashMapOf<String, String?>()
        map[Companion.id] = this.id
        map[Companion.image] = this.image
        map[Companion.type] = this.type
        map[Companion.latestWatchingEpisodeId] = this.latestWatchingEpisodeId
        return map
    }

    fun getEpisodeWatchingHistory(id: String?): EpisodeWatchingHistory? {
        return this.episodesHistory?.firstOrNull { it.id == id }
    }

    private fun getLatestWatchingEpisode(): EpisodeWatchingHistory? {
        return episodesHistory?.firstOrNull { it.id == latestWatchingEpisodeId }
    }

    fun getCurrentWatchingPercentage(): Int {
        val latestWatchingEpisode = getLatestWatchingEpisode()
        var percentage: Int = 0
        latestWatchingEpisode?.let {
            percentage = if (it.position == null || it.duration == null) 0
            else ((it.position * 100) / it.duration).toInt()
        }
        return percentage
    }

    fun getTVShowEpisodeInfo(): String {
        val latestWatchingEpisode = getLatestWatchingEpisode()
        var info = ""
        latestWatchingEpisode?.let {
            info = "Ss: ${it.season} | ${it.title}"
        }
        return info
    }

    fun getTVShowEpisodeInfoFull(): String {
        val latestWatchingEpisode = getLatestWatchingEpisode()
        var info = ""
        latestWatchingEpisode?.let {
            info = "Season: ${it.season} | ${it.title}"
        }
        return info
    }

    fun toMedia(): Media {
        return if (this.type == Media.OBJECT_TYPE_MOVIE) Media.Movie(this.id, type = this.type)
        else Media.Show(this.id, type = this.type)
    }

    companion object {
        const val id = "id"
        const val image = "image"
        const val type = "type"
        const val latestWatchingEpisodeId = "latestWatchingEpisodeId"
        const val episodesHistory = "episodesHistory"
    }
}
