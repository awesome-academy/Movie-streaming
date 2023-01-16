package vn.ztech.software.movie_streaming.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
data class MediaDetails<T : Media>(
    val casts: List<String>?,
    val country: String?,
    val cover: String?,
    val description: String?,
    val duration: String?,
    val episodes: List<Episode>?,
    val genres: List<String>?,
    val id: String?,
    val image: String?,
    val production: String?,
    val rating: Double?,
    val recommendations: @RawValue List<T>?,
    val releaseDate: String?,
    val tags: List<String>?,
    val title: String?,
    val type: String?,
    val url: String?,
    var selectedEpisode: String? = NOT_SELECTED
) : Parcelable {

    fun isTVShow(): Boolean = this.type == TYPE_TV_SHOW

    fun removeRecommendation(): MediaDetails<T> {
        return MediaDetails(
            casts,
            country,
            cover,
            description,
            duration,
            episodes,
            genres,
            id,
            image,
            production,
            rating,
            recommendations = null,
            releaseDate,
            tags,
            title,
            type,
            url,
            selectedEpisode
        )
    }

    fun findEpisode(title: String): Episode? {
        return episodes?.firstOrNull { title.contains(it.title) ?: false }
    }

    private fun findEpisodeById(id: String?): Episode? {
        return episodes?.firstOrNull { it.id == id }
    }

    fun getSelectedEpisode(): Episode? {
        return episodes?.firstOrNull { it.id == selectedEpisode } ?: return episodes?.firstOrNull()
    }

    fun updateEpisodeWatchingHistory(episodeWatchingHistory: EpisodeWatchingHistory) {
        findEpisodeById(episodeWatchingHistory.id)?.let {
            it.duration = episodeWatchingHistory.duration ?: 0
            it.position = episodeWatchingHistory.position ?: 0
        }
    }

    fun toEpisodeWatchingHistory(currentPosition: Long, duration: Long): EpisodeWatchingHistory {
        val selectedEpisode = this.getSelectedEpisode()
        return EpisodeWatchingHistory(
            selectedEpisode?.id,
            selectedEpisode?.title,
            selectedEpisode?.season,
            currentPosition,
            duration
        )
    }

    fun toMediaDetailsWatchingHistory(oldMediaDetailsWatchingHistory: MediaDetailsWatchingHistory?): MediaDetailsWatchingHistory {
        return MediaDetailsWatchingHistory(
            this.id,
            this.image,
            this.type,
            oldMediaDetailsWatchingHistory?.episodesHistory,
            this.getSelectedEpisode()?.id
        )
    }

    companion object {
        const val TYPE_TV_SHOW = "TV Series"
        const val NOT_SELECTED = ""
    }
}
