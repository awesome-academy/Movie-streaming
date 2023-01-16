package vn.ztech.software.movie_streaming.data.source

import vn.ztech.software.movie_streaming.data.model.BaseAPIResponse
import vn.ztech.software.movie_streaming.data.model.EpisodeWatchingHistory
import vn.ztech.software.movie_streaming.data.model.Media
import vn.ztech.software.movie_streaming.data.model.Media.Movie
import vn.ztech.software.movie_streaming.data.model.Media.Show
import vn.ztech.software.movie_streaming.data.model.MediaDetails
import vn.ztech.software.movie_streaming.data.model.MediaDetailsWatchingHistory
import vn.ztech.software.movie_streaming.data.model.MediaMyList
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

        suspend fun getStreamingResource(
            episodeId: String,
            mediaId: String,
            server: String
        ): StreamingResource

        suspend fun search(keyword: String, page: Int): BaseAPIResponse

        suspend fun getRecentWatchingMedia(): List<Media>

        suspend fun getMyList(): List<MediaMyList>

        suspend fun addToMyList(mediaMyList: MediaMyList): Boolean

        suspend fun removeFromMyList(mediaMyList: MediaMyList): Boolean

        suspend fun saveCurrentWatchingHistory(
            mediaDetailsWatchingHistory: MediaDetailsWatchingHistory,
            episodeWatchingHistory: EpisodeWatchingHistory
        ): Boolean

        suspend fun isMediaInMyList(id: String): Boolean

        suspend fun getMediaWatchingHistory(id: String): MediaDetailsWatchingHistory?

        suspend fun getListContinueWatching(): List<MediaDetailsWatchingHistory>?
    }

    companion object {
        const val FIREBASE_COLLECTION_MY_LIST = "myLists"
        const val FIREBASE_COLLECTION_RECENT_WATCHING_HISTORY = "recentWatchingHistory"
        const val FIREBASE_COLLECTION_EPISODE = "episodes"
    }
}
