package vn.ztech.software.movie_streaming.data.repository

import vn.ztech.software.movie_streaming.data.model.BaseAPIResponse
import vn.ztech.software.movie_streaming.data.model.EpisodeWatchingHistory
import vn.ztech.software.movie_streaming.data.model.Media
import vn.ztech.software.movie_streaming.data.model.Media.Movie
import vn.ztech.software.movie_streaming.data.model.Media.Show
import vn.ztech.software.movie_streaming.data.model.MediaDetails
import vn.ztech.software.movie_streaming.data.model.MediaDetailsWatchingHistory
import vn.ztech.software.movie_streaming.data.model.MediaMyList
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
        mediaId: String,
        server: String
    ): DataResult<StreamingResource>

    suspend fun search(keyword: String, page: Int): DataResult<BaseAPIResponse>

    suspend fun getRecentWatchingMedia(): DataResult<List<Media>>

    suspend fun getMyList(): DataResult<List<MediaMyList>>

    suspend fun addToMyList(mediaMyList: MediaMyList): DataResult<Boolean>

    suspend fun removeFromMyList(mediaMyList: MediaMyList): DataResult<Boolean>

    suspend fun saveCurrentWatchingHistory(
        mediaDetailsWatchingHistory: MediaDetailsWatchingHistory,
        episodeWatchingHistory: EpisodeWatchingHistory
    ): DataResult<Boolean>

    suspend fun isMediaInMyList(id: String): DataResult<Boolean>

    suspend fun getMediaWatchingHistory(id: String): DataResult<MediaDetailsWatchingHistory?>

    suspend fun getListContinueWatching(): DataResult<List<MediaDetailsWatchingHistory>?>
}
