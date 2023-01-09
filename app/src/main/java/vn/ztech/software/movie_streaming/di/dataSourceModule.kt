package vn.ztech.software.movie_streaming.di

import org.koin.dsl.module
import vn.ztech.software.movie_streaming.data.source.IMovieDataSource
import vn.ztech.software.movie_streaming.data.source.remote.MovieRemoteImpl

fun dataSourceModules() = module {
    single<IMovieDataSource.Remote> { MovieRemoteImpl(get()) }
}
