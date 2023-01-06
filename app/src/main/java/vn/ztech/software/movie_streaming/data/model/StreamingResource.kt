package vn.ztech.software.movie_streaming.data.model

data class StreamingResource(
    val headers: Headers,
    val sources: List<Source>,
    val subtitles: List<Subtitle>
)
