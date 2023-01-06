package vn.ztech.software.movie_streaming.data.source.remote

import vn.ztech.software.movie_streaming.data.model.*
import vn.ztech.software.movie_streaming.data.source.IMovieDataSource

class MovieRemoteImpl(private val movieApi: IMovieApi): IMovieDataSource.Remote {

    override suspend fun getTrendingMovies(): BaseAPIResponse {
        return movieApi.getTrendingMovies()
    }

    override suspend fun getRecentMovies(): List<Movie> {
        return movieApi.getRecentMovies()
    }

    override suspend fun getRecentShows(): List<Show> {
        return movieApi.getRecentShows()
    }

    override suspend fun search(keyword: String): BaseAPIResponse {
        return movieApi.search(keyword)
    }

    override suspend fun getMovieInfo(id: String): MovieDetails {
        return movieApi.getMovieInfo(id)
    }

    override suspend fun getStreamingResource(
        episodeId: String,
        mediaId: String,
    ): StreamingResource {
        return movieApi.getStreamingUrl(episodeId, mediaId)
    }
}
