package vn.ztech.software.movie_streaming.data.source

import vn.ztech.software.movie_streaming.data.model.BaseAPIResponse
import vn.ztech.software.movie_streaming.data.model.Media
import vn.ztech.software.movie_streaming.data.model.Media.Movie
import vn.ztech.software.movie_streaming.data.model.Media.Show
import vn.ztech.software.movie_streaming.data.model.MediaDetails
import vn.ztech.software.movie_streaming.data.model.StreamingResource

interface IMovieDataSource {

    interface Local {
        // TODO  implement later
    }

    interface Remote {

        suspend fun getTrendingMovies(): BaseAPIResponse

        suspend fun getRecentMovies(): List<Movie>

        suspend fun getRecentShows(): List<Show>

        suspend fun search(keyword: String): BaseAPIResponse

        suspend fun <T : Media> getMovieInfo(id: String): MediaDetails<T>

        suspend fun getStreamingResource(episodeId: String, mediaId: String): StreamingResource

        suspend fun search(keyword: String, page: Int): BaseAPIResponse
    }
}
