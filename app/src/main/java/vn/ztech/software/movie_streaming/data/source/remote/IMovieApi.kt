package vn.ztech.software.movie_streaming.data.source.remote

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import vn.ztech.software.movie_streaming.data.model.*

interface IMovieApi {

    @GET("trending")
    suspend fun getTrendingMovies(): BaseAPIResponse

    @GET("recent-movies")
    suspend fun getRecentMovies(): List<Media.Movie>

    @GET("recent-shows")
    suspend fun getRecentShows(): List<Media.Show>

    @GET("{keyword}")
    suspend fun search(@Path("keyword") keyword: String): BaseAPIResponse

    @GET("info")
    suspend fun <T : Media> getMovieInfo(@Query("id") id: String): MediaDetails<T>

    @GET("watch")
    suspend fun getStreamingUrl(
        @Query("episodeId") episodeId: String,
        @Query("mediaId") mediaId: String,
        @Query("server") server: String
    ): StreamingResource

    @GET("{keyword}")
    suspend fun search(@Path("keyword") keyword: String, @Query("page") page: Int): BaseAPIResponse
}
