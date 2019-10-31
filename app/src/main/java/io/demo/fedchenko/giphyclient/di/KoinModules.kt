package io.demo.fedchenko.giphyclient.di

import io.demo.fedchenko.giphyclient.repository.Repository
import io.demo.fedchenko.giphyclient.retrofit.GiphyAPI
import io.demo.fedchenko.giphyclient.viewmodel.MainViewModel
import io.reactivex.schedulers.Schedulers
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

val viewModelModule: Module = module {
    viewModel { MainViewModel(gifProvider = get() as Repository) }
}

val repositoryModule: Module = module {
    single {
        Repository(
            getProperty("qiphy_key"),
            Retrofit.Builder()
                .baseUrl("https://api.giphy.com/v1/gifs/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(GiphyAPI::class.java)
        )
    }
}
