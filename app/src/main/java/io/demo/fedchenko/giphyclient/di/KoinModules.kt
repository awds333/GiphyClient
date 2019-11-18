package io.demo.fedchenko.giphyclient.di

import android.content.SharedPreferences
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import io.demo.fedchenko.giphyclient.repository.GifRepository
import io.demo.fedchenko.giphyclient.repository.SharedPreferencesTermsRepo
import io.demo.fedchenko.giphyclient.retrofit.GiphyAPI
import io.demo.fedchenko.giphyclient.viewmodel.MainViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val viewModelModule: Module = module {
    viewModel { (preferences: SharedPreferences) -> MainViewModel(get<GifRepository>(), get<SharedPreferencesTermsRepo>(){ parametersOf(preferences)}) }
}

val repositoryModule: Module = module {
    single {
        GifRepository(
            getProperty("qiphy_key"),
            get()
        )
    }
    single {
        Retrofit.Builder()
            .baseUrl(getProperty("base_url", ""))
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GiphyAPI::class.java)
    }
    single { (preferences: SharedPreferences) ->
        SharedPreferencesTermsRepo(preferences)
    }
}
