package vn.ztech.software.movie_streaming.data.model

data class MediaMyList(
    val id: String?,
    val image: String?,
    val type: String?
) : Media() {

    fun isDataGood(): Boolean {
        val x = !id.isNullOrEmpty() && !image.isNullOrEmpty()
        return x
    }

    fun toMediaMyListFireBase(): MediaMyListFireBase {
        return MediaMyListFireBase(id, image, type)
    }

    companion object {
        const val id = "id"
        const val image = "image"
        const val type = "type"
    }

    override fun getImg(): String? {
        return image
    }

    override fun mGetId(): String? {
        return id
    }

    override fun getObj(): Any? {
        return null
    }

    override fun mGetType(): String? {
        return type
    }
}

data class MediaMyListFireBase(
    val id: String?,
    val image: String?,
    val type: String?
)
