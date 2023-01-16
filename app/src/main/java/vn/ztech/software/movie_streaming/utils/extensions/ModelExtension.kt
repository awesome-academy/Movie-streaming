package vn.ztech.software.movie_streaming.utils.extensions

import com.google.firebase.firestore.DocumentSnapshot
import com.google.gson.internal.LinkedTreeMap
import vn.ztech.software.movie_streaming.data.model.Episode
import vn.ztech.software.movie_streaming.data.model.EpisodeData
import vn.ztech.software.movie_streaming.data.model.EpisodeWatchingHistory
import vn.ztech.software.movie_streaming.data.model.Media
import vn.ztech.software.movie_streaming.data.model.Media.Companion.FIELD_TYPE
import vn.ztech.software.movie_streaming.data.model.Media.Companion.OBJECT_TYPE_MOVIE
import vn.ztech.software.movie_streaming.data.model.MediaDetails
import vn.ztech.software.movie_streaming.data.model.MediaDetailsWatchingHistory
import vn.ztech.software.movie_streaming.data.model.MediaMyList
import vn.ztech.software.movie_streaming.data.model.Season
import vn.ztech.software.movie_streaming.utils.Constant

fun List<Episode>.toListGroupedBySeason(): List<EpisodeData> {
    val season2Episode = this.groupBy { it.season }
    val listEpisodeData = mutableListOf<EpisodeData>()
    season2Episode.entries.forEach { season ->
        listEpisodeData.add(
            Season(
                String.format(
                    Constant.FORMAT_SEASON_STRING,
                    season.key.toString()
                )
            )
        )
        season.value.forEach {
            listEpisodeData.add(it)
        }
    }

    return listEpisodeData
}

fun List<LinkedTreeMap<String, String>>.toListRecommendations(): List<Media> {
    val listMedia = mutableListOf<Media>()
    this.forEach { item ->
        if (item[FIELD_TYPE] == OBJECT_TYPE_MOVIE) listMedia.add(getMediaFromMap<Media.Movie>(item))
        else listMedia.add(getMediaFromMap<Media.Show>(item))
    }
    return listMedia
}

inline fun <reified T : Media> getMediaFromMap(map: LinkedTreeMap<String, String>): T {
    return when (T::class.java) {
        Media.Movie::class.java -> Media.Movie.fromMapToObject(map) as T
        Media.Show::class.java -> Media.Show.fromMapToObject(map) as T
        else -> {
            Media.Movie.fromMapToObject(map) as T
        }
    }
}

fun Media.toMediaMyList(): MediaMyList {
    return MediaMyList(this.mGetId(), this.getImg(), this.mGetType())
}

fun MediaDetails<Media>.toMediaMyList(): MediaMyList {
    return MediaMyList(this.id, this.image, this.type)
}

fun DocumentSnapshot.toMediaMyList(): MediaMyList {
    val id = this.get(MediaMyList.id) as? String
    val image = this.get(MediaMyList.image) as? String
    val type = this.get(MediaMyList.type) as? String

    return MediaMyList(id, image, type)
}

fun DocumentSnapshot.toMediaDetailsWatchingHistory(): MediaDetailsWatchingHistory {
    val id = this.get(MediaDetailsWatchingHistory.id) as? String
    val image = this.get(MediaDetailsWatchingHistory.image) as? String
    val type = this.get(MediaDetailsWatchingHistory.type) as? String
    val latestWatchingEpisodeId =
        this.get(MediaDetailsWatchingHistory.latestWatchingEpisodeId) as? String
    return MediaDetailsWatchingHistory(
        id,
        image,
        type,
        latestWatchingEpisodeId = latestWatchingEpisodeId
    )
}

fun DocumentSnapshot.toEpisodeWatchingHistory(): EpisodeWatchingHistory {
    val id = this.get(EpisodeWatchingHistory.id) as? String
    val title = this.get(EpisodeWatchingHistory.title) as? String
    val season = this.get(EpisodeWatchingHistory.season) as? Long
    val position = this.get(EpisodeWatchingHistory.position) as? Long
    val duration = this.get(EpisodeWatchingHistory.duration) as? Long
    return EpisodeWatchingHistory(id, title, season, position, duration)
}

