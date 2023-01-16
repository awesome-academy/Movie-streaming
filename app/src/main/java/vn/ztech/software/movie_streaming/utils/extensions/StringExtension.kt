package vn.ztech.software.movie_streaming.utils.extensions

fun String.removeIllegalChars(): String {
    return this.replace("/", "_")
}
