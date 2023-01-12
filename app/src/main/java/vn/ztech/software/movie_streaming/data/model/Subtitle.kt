package vn.ztech.software.movie_streaming.data.model

data class Subtitle(
    val lang: String?,
    val url: String?
) {
    companion object {
        const val TURN_OFF_SUBTITLE = "Turn off subtitle"
    }
}
