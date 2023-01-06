package vn.ztech.software.movie_streaming.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

abstract class Media {
    @Parcelize
    data class Movie(
        val id: String?,
        val image: String?,
        val seasons: Int?,
        val title: String?,
        val type: String?,
        val url: String?
    ) : Media(), Parcelable {
        override fun getImg(): String? {
            return image
        }

        override fun mGetId(): String? {
            return id
        }

        override fun getObj(): Any {
            return this
        }
    }

    @Parcelize
    data class Show(
        val id: String?,
        val image: String?,
        val latestEpisode: String?,
        val season: String?,
        val title: String?,
        val type: String?,
        val url: String?
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
    }

    abstract fun getImg(): String?
    abstract fun mGetId(): String?
    abstract fun getObj(): Any?
}

