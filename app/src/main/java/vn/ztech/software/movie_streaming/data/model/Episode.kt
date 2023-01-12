package vn.ztech.software.movie_streaming.data.model

data class Episode(
    val id: String?,
    val number: Int?,
    val season: Int?,
    val title: String?,
    val url: String?
) : EpisodeData() {
    override fun mGetId(): String? {
        return id
    }
}

data class Season(
    val name: String?
) : EpisodeData() {
    override fun mGetId(): String? {
        return name ?: ""
    }
}

abstract class EpisodeData {
    abstract fun mGetId(): String?
}
