package vn.ztech.software.movie_streaming.di

fun allModules() = listOf(
    viewModelModules(),
    repositoryModules(),
    dataSourceModules(),
    apiModule(),
    networkModules()
)
