package vn.ztech.software.movie_streaming.data.model

import vn.ztech.software.movie_streaming.data.model.StreamingStates.Companion.DEFAULT_STREAMING_LANG
import vn.ztech.software.movie_streaming.data.model.StreamingStates.Companion.SUBTITLE_DEFAULT_MAYBE

data class StreamingResource(
    val headers: Headers?,
    val sources: List<Source>?,
    val subtitles: List<Subtitle>?
) {
    fun findResourceByQuality(quality: String = StreamingStates.DEFAULT_STREAMING_QUALITY): Source? {
        return if (sources.isNullOrEmpty()) null
        else {
            sources.firstOrNull { it.quality == quality } ?: sources.last()
        }
    }

    fun findSubtitle(lang: String? = DEFAULT_STREAMING_LANG): Subtitle? {
        return if (subtitles.isNullOrEmpty()) null
        else {
            subtitles.firstOrNull { it.lang == lang } ?: subtitles.last()
        }
    }

    fun filterOutAuto(): List<Subtitle> {
        val subs = subtitles?.filter { it.lang?.contains(SUBTITLE_DEFAULT_MAYBE) == false }
        return subs ?: emptyList()
    }

    companion object {
        val listServers = listOf<String>("vidcloud", "mixdrop", "upcloud")
    }
}

data class StreamingStates(
    var position: Long = DEFAULT_STREAMING_POSITION,
    var quality: String = DEFAULT_STREAMING_QUALITY,
    var lang: String = DEFAULT_STREAMING_LANG,
    var brightnessProgress: Int = 0
) {
    fun clearStates() {
        this.position = DEFAULT_STREAMING_POSITION
    }

    companion object {
        const val DEFAULT_STREAMING_QUALITY = "auto"
        const val DEFAULT_STREAMING_POSITION = 0L
        const val DEFAULT_STREAMING_LANG = "English"
        const val STREAMING_LANG_TURN_OFF = "Turn off"
        const val SUBTITLE_DEFAULT_MAYBE = "Default (maybe)"
    }
}
