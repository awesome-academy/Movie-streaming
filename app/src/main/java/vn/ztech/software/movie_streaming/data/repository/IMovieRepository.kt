package vn.ztech.software.movie_streaming.data.repository

import vn.ztech.software.movie_streaming.data.model.*
import vn.ztech.software.movie_streaming.utils.DataResult

interface IMovieRepository {

    suspend fun getTrendingMovies(): DataResult<BaseAPIResponse>

    suspend fun getRecentMovies(): DataResult<List<Movie>>

    suspend fun getRecentShows(): DataResult<List<Show>>

    suspend fun search(keyword: String): DataResult<BaseAPIResponse>

    suspend fun getMovieInfo(id: String): DataResult<MovieDetails>

    suspend fun getStreamingResource(episodeId: String, mediaId: String): DataResult<StreamingResource>

}
