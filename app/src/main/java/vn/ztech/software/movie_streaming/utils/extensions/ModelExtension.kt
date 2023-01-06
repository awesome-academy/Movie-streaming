package vn.ztech.software.movie_streaming.utils.extensions

import com.google.gson.internal.LinkedTreeMap
import vn.ztech.software.movie_streaming.data.model.Episode
import vn.ztech.software.movie_streaming.data.model.EpisodeData
import vn.ztech.software.movie_streaming.data.model.Media
import vn.ztech.software.movie_streaming.data.model.Media.Companion.FIELD_TYPE
import vn.ztech.software.movie_streaming.data.model.Media.Companion.OBJECT_TYPE_MOVIE
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
