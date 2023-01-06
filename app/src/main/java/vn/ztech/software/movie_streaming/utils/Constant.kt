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

}