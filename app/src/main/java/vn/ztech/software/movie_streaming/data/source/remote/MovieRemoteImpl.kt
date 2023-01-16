package vn.ztech.software.movie_streaming.data.source.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
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
import vn.ztech.software.movie_streaming.utils.extensions.removeIllegalChars
import vn.ztech.software.movie_streaming.utils.extensions.toEpisodeWatchingHistory
import vn.ztech.software.movie_streaming.utils.extensions.toMediaDetailsWatchingHistory
import vn.ztech.software.movie_streaming.utils.extensions.toMediaMyList

class MovieRemoteImpl(private val movieApi: IMovieApi) : IMovieDataSource.Remote {

    val db = Firebase.firestore
    val auth = FirebaseAuth.getInstance()

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

    override suspend fun <T : Media> getMovieInfo(id: String): MediaDetails<T> {
        return movieApi.getMovieInfo(id)
    }

    override suspend fun getStreamingResource(
        episodeId: String,
        mediaId: String,
        server: String,
    ): StreamingResource {
        return movieApi.getStreamingUrl(episodeId, mediaId, server)
    }

    override suspend fun search(keyword: String, page: Int): BaseAPIResponse {
        return movieApi.search(keyword, page)
    }

    override suspend fun getRecentWatchingMedia(): List<Media> {
        //TODO implement later
        return emptyList()
    }

    override suspend fun getMyList(): List<MediaMyList> {
        var result = mutableListOf<MediaMyList>()

        db.collection(IMovieDataSource.FIREBASE_COLLECTION_MY_LIST)
            .document(auth.uid.toString())
            .collection(IMovieDataSource.FIREBASE_COLLECTION_MY_LIST)
            .get()
            .addOnSuccessListener {
                it.documents.forEach {
                    result.add(it.toMediaMyList())
                }
            }
            .addOnFailureListener {
                throw it
            }
            .await()

        return result
    }

    override suspend fun addToMyList(mediaMyList: MediaMyList): Boolean {
        if (!mediaMyList.isDataGood()) return false

        db.collection(IMovieDataSource.FIREBASE_COLLECTION_MY_LIST)
            .document(auth.uid.toString())
            .collection(IMovieDataSource.FIREBASE_COLLECTION_MY_LIST)
            .document(mediaMyList.id!!.removeIllegalChars())
            .set(mediaMyList.toMediaMyListFireBase())
            .await()

        return true
    }

    override suspend fun removeFromMyList(mediaMyList: MediaMyList): Boolean {
        if (!mediaMyList.isDataGood()) return false

        db.collection(IMovieDataSource.FIREBASE_COLLECTION_MY_LIST)
            .document(auth.uid.toString())
            .collection(IMovieDataSource.FIREBASE_COLLECTION_MY_LIST)
            .document(mediaMyList.id!!.removeIllegalChars())
            .delete()
            .await()

        return true
    }

    override suspend fun saveCurrentWatchingHistory(
        mediaDetailsWatchingHistory: MediaDetailsWatchingHistory,
        episodeWatchingHistory: EpisodeWatchingHistory,
    ): Boolean {
        var resultSavedMediaDetailsWatchingHistory = false
        val mediaDetailsWatchingHistoryData = mediaDetailsWatchingHistory.toMapObj()
        db.collection(IMovieDataSource.FIREBASE_COLLECTION_RECENT_WATCHING_HISTORY)
            .document(auth.uid.toString())
            .collection(IMovieDataSource.FIREBASE_COLLECTION_RECENT_WATCHING_HISTORY)
            .document(mediaDetailsWatchingHistory.id!!.removeIllegalChars())
            .set(mediaDetailsWatchingHistoryData, SetOptions.merge())
            .addOnSuccessListener {
                resultSavedMediaDetailsWatchingHistory = true
            }
            .addOnFailureListener {
                throw it
            }
            .await()

        var resultSavedEpisodeWatchingHistory = false
        episodeWatchingHistory.id?.let {
            db.collection(IMovieDataSource.FIREBASE_COLLECTION_RECENT_WATCHING_HISTORY)
                .document(auth.uid.toString())
                .collection(IMovieDataSource.FIREBASE_COLLECTION_RECENT_WATCHING_HISTORY)
                .document(mediaDetailsWatchingHistory.id!!.removeIllegalChars())
                .collection(MediaDetailsWatchingHistory.episodesHistory)
                .document(episodeWatchingHistory.id)
                .set(episodeWatchingHistory)
                .addOnSuccessListener {
                    resultSavedEpisodeWatchingHistory = true
                }
                .addOnFailureListener {
                    throw it
                }
                .await()
        }

        return resultSavedMediaDetailsWatchingHistory && resultSavedEpisodeWatchingHistory
    }

    override suspend fun isMediaInMyList(id: String): Boolean {
        return db.collection(IMovieDataSource.FIREBASE_COLLECTION_MY_LIST)
            .document(auth.uid.toString())
            .collection(IMovieDataSource.FIREBASE_COLLECTION_MY_LIST)
            .document(id.removeIllegalChars())
            .get()
            .await()
            .exists()
    }

    override suspend fun getMediaWatchingHistory(id: String): MediaDetailsWatchingHistory? {
        var result: MediaDetailsWatchingHistory? = null

        val snap1 = db.collection(IMovieDataSource.FIREBASE_COLLECTION_RECENT_WATCHING_HISTORY)
            .document(auth.uid.toString())
            .collection(IMovieDataSource.FIREBASE_COLLECTION_RECENT_WATCHING_HISTORY)
            .document(id.removeIllegalChars())
            .get()
            .await()
        result = snap1.toMediaDetailsWatchingHistory()

        val episodeHistory = getEpisodeHistory(id)
        result = result.copy(episodesHistory = episodeHistory)

        return result
    }

    override suspend fun getListContinueWatching(): List<MediaDetailsWatchingHistory>? {
        val result = mutableListOf<MediaDetailsWatchingHistory>()

        val snap = db.collection(IMovieDataSource.FIREBASE_COLLECTION_RECENT_WATCHING_HISTORY)
            .document(auth.uid.toString())
            .collection(IMovieDataSource.FIREBASE_COLLECTION_RECENT_WATCHING_HISTORY)
            .get()
            .await()
        snap.documents.forEach {
            val mediaWatchingHistory = it.toMediaDetailsWatchingHistory()
            val episodeHistory = getEpisodeHistory(it.id)
            result.add(mediaWatchingHistory.copy(episodesHistory = episodeHistory))
        }
        return result
    }

    private suspend fun getEpisodeHistory(id: String): MutableList<EpisodeWatchingHistory> {
        val episodeHistory = mutableListOf<EpisodeWatchingHistory>()
        val snap2 = db.collection(IMovieDataSource.FIREBASE_COLLECTION_RECENT_WATCHING_HISTORY)
            .document(auth.uid.toString())
            .collection(IMovieDataSource.FIREBASE_COLLECTION_RECENT_WATCHING_HISTORY)
            .document(id.removeIllegalChars())
            .collection(MediaDetailsWatchingHistory.episodesHistory)
            .get()
            .await()

        snap2.documents.forEach {
            episodeHistory.add(it.toEpisodeWatchingHistory())
        }
        return episodeHistory
    }
}
