package vn.ztech.software.movie_streaming.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import vn.ztech.software.movie_streaming.utils.Constant
import java.util.concurrent.TimeUnit

val API = named("api")

fun networkModules() = module {

    single<IApiGenerator> { ApiGenerator(get(API)) }

    single<Retrofit.Builder>(API) {
        Retrofit.Builder()
            .baseUrl(Constant.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(get()))
            .client(get())
    }

    single {
        OkHttpClient.Builder()
            .connectTimeout(Constant.NETWORK_CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(Constant.NETWORK_READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(Constant.NETWORK_WRITE_TIMEOUT, TimeUnit.SECONDS)
            .build()
    }

    single<Gson> { GsonBuilder().create() }

}

interface IApiGenerator {
    fun <T> api(clazz: Class<T>): T
}

class ApiGenerator(val apiBuilder: Retrofit.Builder) : IApiGenerator {

    override fun <T> api(clazz: Class<T>): T {
        return apiBuilder.baseUrl(Constant.BASE_URL).build().create(clazz)
    }
}
