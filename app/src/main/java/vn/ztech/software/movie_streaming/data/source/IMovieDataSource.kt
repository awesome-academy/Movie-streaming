package vn.ztech.software.movie_streaming.data.source

import vn.ztech.software.movie_streaming.data.model.*

interface IMovieDataSource {

    interface Local {
        // todo implement later
    }

    interface Remote {

        suspend fun getTrendingMovies(): BaseAPIResponse

        suspend fun getRecentMovies(): List<Movie>

        suspend fun getRecentShows(): List<Show>

        suspend fun search(keyword: String): BaseAPIResponse

        suspend fun getMovieInfo(id: String): MovieDetails

        suspend fun getStreamingResource(episodeId: String, mediaId: String): StreamingResource

    }
}
