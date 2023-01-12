package vn.ztech.software.movie_streaming.utils

object Constant {

    const val BASE_URL = "https://api.consumet.org/movies/flixhq/"

    const val NETWORK_CONNECT_TIMEOUT = 30L
    const val NETWORK_READ_TIMEOUT = 30L
    const val NETWORK_WRITE_TIMEOUT = 30L

    enum class ScreenNumber {
        Home,
        Search,
        Profile,
    }

    const val SPLASH_DELAY_TIME = 2000L

    const val FORMAT_SEASON_STRING = "Season: %1s"
    const val FORMAT_EPISODE_STRING = "\t%1s"
}
