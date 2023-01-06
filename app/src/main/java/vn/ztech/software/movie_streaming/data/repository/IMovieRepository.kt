package vn.ztech.software.movie_streaming.data.repository

import vn.ztech.software.movie_streaming.data.model.BaseAPIResponse
import vn.ztech.software.movie_streaming.data.model.Media
import vn.ztech.software.movie_streaming.data.model.Media.Movie
import vn.ztech.software.movie_streaming.data.model.Media.Show
import vn.ztech.software.movie_streaming.data.model.MediaDetails
import vn.ztech.software.movie_streaming.data.model.StreamingResource
import vn.ztech.software.movie_streaming.utils.DataResult

interface IMovieRepository {

    suspend fun getTrendingMovies(): DataResult<BaseAPIResponse>

    suspend fun getRecentMovies(): DataResult<List<Movie>>

    suspend fun getRecentShows(): DataResult<List<Show>>

    suspend fun search(keyword: String): DataResult<BaseAPIResponse>

    suspend fun <T : Media> getMovieInfo(id: String): DataResult<MediaDetails<T>>

    suspend fun getStreamingResource(
        episodeId: String,
        mediaId: String
    ): DataResult<StreamingResource>

}
