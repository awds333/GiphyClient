package io.demo.fedchenko.giphyclient.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import io.demo.fedchenko.giphyclient.ConnectivityLiveData
import io.demo.fedchenko.giphyclient.repository.GifRepository
import io.demo.fedchenko.giphyclient.repository.RoomFavoriteManager
import io.demo.fedchenko.giphyclient.repository.RoomTermsManager
import io.demo.fedchenko.giphyclient.retrofit.GiphyAPI
import io.demo.fedchenko.giphyclient.room.AppDataBase
import io.demo.fedchenko.giphyclient.viewmodel.FavoriteViewModel
import io.demo.fedchenko.giphyclient.viewmodel.MainViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val viewModelModule: Module = module {
    viewModel { (context: Context) ->
        MainViewModel(
            get<GifRepository>(),
            get<RoomTermsManager> { parametersOf(context) },
            get<RoomFavoriteManager> { parametersOf(context) })
    }
    viewModel { (context: Context) ->
        FavoriteViewModel(get<RoomFavoriteManager> {
            parametersOf(
                context
            )
        })
    }
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
    single { (context: Context) -> RoomFavoriteManager(get { parametersOf(context) }) }
    single { (context: Context) -> get<AppDataBase> { parametersOf(context) }.gifDao() }

    single { (context: Context) -> RoomTermsManager(get { parametersOf(context) }) }
    single { (context: Context) -> get<AppDataBase> { parametersOf(context) }.termDao() }

    single { (context: Context) ->
        Room.databaseBuilder(context, AppDataBase::class.java, getProperty("db_name")).build()
    }
}

val utilesModel: Module = module {
    single { (app: Application) -> ConnectivityLiveData(app) }
}
