package vn.ztech.software.movie_streaming.data.model

data class Movie(
    val id: String,
    val image: String,
    val seasons: Int,
    val title: String,
    val type: String,
    val url: String
)
