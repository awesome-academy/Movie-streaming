package vn.ztech.software.movie_streaming.di

import org.koin.core.scope.Scope
import org.koin.dsl.module
import vn.ztech.software.movie_streaming.data.source.remote.IMovieApi

fun apiModule() = module {
    fun <T> Scope.createApi(clazz: Class<T>): T = get<IApiGenerator>().api(clazz)

    single { createApi(IMovieApi::class.java)}
}
