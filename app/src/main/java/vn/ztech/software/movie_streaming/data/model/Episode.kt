package vn.ztech.software.movie_streaming.data.model

data class Episode(
    val id: String,
    val number: Int,
    val season: Int,
    val title: String,
    val url: String
)
