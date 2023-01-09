package vn.ztech.software.movie_streaming

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import vn.ztech.software.movie_streaming.di.allModules

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(allModules())
        }
    }
}
