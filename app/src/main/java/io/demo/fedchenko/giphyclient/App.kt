package io.demo.fedchenko.giphyclient

import android.app.Application
import io.demo.fedchenko.giphyclient.di.repositoryModule
import io.demo.fedchenko.giphyclient.di.viewModelModule
import org.koin.core.context.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            modules(listOf(viewModelModule, repositoryModule))
            properties(
                mapOf(
                    Pair("qiphy_key", "g0huuU56R74KkSQCYLdzfqCDyr4JmssE"),
                    Pair("base_url", "https://api.giphy.com/v1/gifs/")
                )
            )
        }

    }
}