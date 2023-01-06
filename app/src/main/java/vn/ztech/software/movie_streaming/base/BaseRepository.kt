package vn.ztech.software.movie_streaming.base

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import vn.ztech.software.movie_streaming.utils.DataResult
import kotlin.coroutines.CoroutineContext

abstract class BaseRepository {

    protected suspend fun <T> getResult(
        context: CoroutineContext = Dispatchers.IO,
        request: suspend CoroutineScope.() -> T
    ): DataResult<T> = withContext(context) {
        return@withContext try {
            val response = request()
            DataResult.Success(response)
        } catch (e: Exception) {
            e.printStackTrace()
            DataResult.Error(e)
        }
    }
}
