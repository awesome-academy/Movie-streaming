package vn.ztech.software.movie_streaming.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import vn.ztech.software.movie_streaming.ui.MainViewModel
import vn.ztech.software.movie_streaming.ui.home.HomeViewModel

fun viewModelModules() = module {
    viewModel { MainViewModel() }
    viewModel { HomeViewModel(get()) }
}
