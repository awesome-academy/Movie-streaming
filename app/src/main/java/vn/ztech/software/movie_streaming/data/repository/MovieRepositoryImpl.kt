package vn.ztech.software.movie_streaming.data.repository

import kotlinx.coroutines.withContext
import vn.ztech.software.movie_streaming.base.BaseRepository
import vn.ztech.software.movie_streaming.data.model.*
import vn.ztech.software.movie_streaming.data.source.IMovieDataSource
import vn.ztech.software.movie_streaming.utils.DataResult

class MovieRepositoryImpl(private val remote: IMovieDataSource.Remote): BaseRepository(), IMovieRepository {

    override suspend fun getTrendingMovies(): DataResult<BaseAPIResponse> = getResult {
        remote.getTrendingMovies()
    }

    override suspend fun getRecentMovies(): DataResult<List<Movie>> = getResult {
        remote.getRecentMovies()
    }

    override suspend fun getRecentShows(): DataResult<List<Show>> = getResult {
        remote.getRecentShows()
    }

    override suspend fun search(keyword: String): DataResult<BaseAPIResponse> = getResult {
        remote.search(keyword)
    }

    override suspend fun getMovieInfo(id: String): DataResult<MovieDetails> = getResult {
        remote.getMovieInfo(id)
    }

    override suspend fun getStreamingResource(
        episodeId: String,
        mediaId: String,
    ): DataResult<StreamingResource> = getResult {
        remote.getStreamingResource(episodeId, mediaId)
    }

}
