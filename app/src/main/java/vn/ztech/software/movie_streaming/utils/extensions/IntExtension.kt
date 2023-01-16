package vn.ztech.software.movie_streaming.utils.extensions

fun Int.toBrightness(): Int {
    return this * 255 / 100
}

fun Int.toPercentageString(): String {
    return "Zoom: $this%"
}
