package vn.ztech.software.movie_streaming.data.model

import android.os.Parcelable
import com.google.gson.internal.LinkedTreeMap
import kotlinx.android.parcel.Parcelize

abstract class Media {
    @Parcelize
    data class Movie(
        val id: String? = null,
        val image: String? = null,
        val seasons: Int? = null,
        val title: String? = null,
        val type: String? = null,
        val url: String? = null
    ) : Media(), Parcelable {
        override fun getImg(): String? {
            return image
        }

        override fun mGetId(): String? {
            return id
        }

        override fun getObj(): Any? {
            return this
        }

        override fun mGetType(): String? {
            return type
        }


        companion object {
            fun fromMapToObject(map: LinkedTreeMap<String, String>): Movie {
                val id = map[FIELD_ID]
                val image = map[FIELD_IMAGE]
                val seasons = map[FIELD_SEASONS]?.toInt()
                val title = map[FIELD_TITLE]
                val type = map[FIELD_TYPE]
                val url = map[FIELD_URL]
                return Movie(id, image, seasons, title, type, url)
            }
        }
    }

    @Parcelize
    data class Show(
        val id: String? = null,
        val image: String? = null,
        val latestEpisode: String? = null,
        val season: String? = null,
        val title: String? = null,
        val type: String? = null,
        val url: String? = null
    ) : Media(), Parcelable {
        override fun getImg(): String? {
            return image
        }

        override fun mGetId(): String? {
            return id
        }

        override fun getObj(): Any? {
            return this
        }

        override fun mGetType(): String? {
            return type
        }

        companion object {
            fun fromMapToObject(map: LinkedTreeMap<String, String>): Show {
                val id = map[FIELD_ID]
                val image = map[FIELD_IMAGE]
                val latestEpisode = map[FIELD_LATEST_EPISODE]
                val season = map[FIELD_SEASON]
                val title = map[FIELD_TITLE]
                val type = map[FIELD_TYPE]
                val url = map[FIELD_URL]
                return Show(id, image, latestEpisode, season, title, type, url)
            }
        }
    }

    abstract fun getImg(): String?
    abstract fun mGetId(): String?
    abstract fun getObj(): Any?
    abstract fun mGetType(): String?

    fun getMediaClass(): String? {
        return if (this.mGetType() == OBJECT_TYPE_MOVIE) OBJECT_TYPE_MOVIE
        else OBJECT_TYPE_TV_SHOW
    }

    fun toMovie() = Movie(this.mGetId(), this.getImg(), null, null, this.mGetType(), null)
    fun toShow() = Show(this.mGetId(), this.getImg(), null, null, null, this.mGetType(), null)

    companion object {
        const val FIELD_ID = "id"
        const val FIELD_IMAGE = "image"
        const val FIELD_SEASONS = "seasons"
        const val FIELD_TITLE = "title"
        const val FIELD_TYPE = "type"
        const val FIELD_URL = "url"
        const val FIELD_LATEST_EPISODE = "latestEpisode"
        const val FIELD_SEASON = "season"

        const val OBJECT_TYPE_MOVIE = "Movie"
        const val OBJECT_TYPE_TV_SHOW = "TV Series"
    }
}

