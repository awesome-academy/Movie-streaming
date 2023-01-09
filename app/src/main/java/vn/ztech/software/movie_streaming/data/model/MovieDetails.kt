package vn.ztech.software.movie_streaming.data.model

data class MovieDetails(
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
    val recommendations: List<Movie>?,
    val releaseDate: String?,
    val tags: List<String>?,
    val title: String?,
    val type: String?,
    val url: String?
)
