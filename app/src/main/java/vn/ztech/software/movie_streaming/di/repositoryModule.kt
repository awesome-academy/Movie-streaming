package vn.ztech.software.movie_streaming.di

import org.koin.dsl.module
import vn.ztech.software.movie_streaming.data.repository.IMovieRepository
import vn.ztech.software.movie_streaming.data.repository.MovieRepositoryImpl

fun repositoryModules() = module {
    single<IMovieRepository> { MovieRepositoryImpl(get()) }
}
