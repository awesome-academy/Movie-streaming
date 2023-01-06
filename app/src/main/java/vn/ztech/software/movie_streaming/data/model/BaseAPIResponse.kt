package vn.ztech.software.movie_streaming.data.model

data class BaseAPIResponse(
    val currentPage: Int?,
    val hasNextPage: Boolean?,
    val results: List<Media.Movie>?
)
