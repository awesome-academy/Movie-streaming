package vn.ztech.software.movie_streaming.data.repository

import vn.ztech.software.movie_streaming.base.BaseRepository
import vn.ztech.software.movie_streaming.data.model.BaseAPIResponse
import vn.ztech.software.movie_streaming.data.model.EpisodeWatchingHistory
import vn.ztech.software.movie_streaming.data.model.Media
import vn.ztech.software.movie_streaming.data.model.Media.Movie
import vn.ztech.software.movie_streaming.data.model.Media.Show
import vn.ztech.software.movie_streaming.data.model.MediaDetails
import vn.ztech.software.movie_streaming.data.model.MediaDetailsWatchingHistory
import vn.ztech.software.movie_streaming.data.model.MediaMyList
import vn.ztech.software.movie_streaming.data.model.StreamingResource
import vn.ztech.software.movie_streaming.data.source.IMovieDataSource
import vn.ztech.software.movie_streaming.utils.DataResult

class MovieRepositoryImpl(private val remote: IMovieDataSource.Remote) : BaseRepository(),
    IMovieRepository {

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

    override suspend fun <T : Media> getMovieInfo(id: String): DataResult<MediaDetails<T>> =
        getResult {
            remote.getMovieInfo(id)
        }

    override suspend fun getStreamingResource(
        episodeId: String,
        mediaId: String,
        server: String
    ): DataResult<StreamingResource> = getResult {
        remote.getStreamingResource(episodeId, mediaId, server)
    }

    override suspend fun search(keyword: String, page: Int): DataResult<BaseAPIResponse> =
        getResult {
            remote.search(keyword, page)
        }

    override suspend fun getRecentWatchingMedia(): DataResult<List<Media>> {
        TODO("Not yet implemented")
    }

    override suspend fun getMyList(): DataResult<List<MediaMyList>> = getResult {
        remote.getMyList()
    }

    override suspend fun addToMyList(mediaMyList: MediaMyList) = getResult {
        remote.addToMyList(mediaMyList)
    }

    override suspend fun removeFromMyList(mediaMyList: MediaMyList) = getResult {
        remote.removeFromMyList(mediaMyList)
    }

    override suspend fun saveCurrentWatchingHistory(
        mediaDetailsWatchingHistory: MediaDetailsWatchingHistory,
        episodeWatchingHistory: EpisodeWatchingHistory,
    ): DataResult<Boolean> = getResult {
        remote.saveCurrentWatchingHistory(mediaDetailsWatchingHistory, episodeWatchingHistory)
    }

    override suspend fun isMediaInMyList(id: String): DataResult<Boolean> = getResult {
        remote.isMediaInMyList(id)
    }

    override suspend fun getMediaWatchingHistory(id: String): DataResult<MediaDetailsWatchingHistory?> =
        getResult {
            remote.getMediaWatchingHistory(id)
        }

    override suspend fun getListContinueWatching(): DataResult<List<MediaDetailsWatchingHistory>?> =
        getResult {
            remote.getListContinueWatching()
        }
}
