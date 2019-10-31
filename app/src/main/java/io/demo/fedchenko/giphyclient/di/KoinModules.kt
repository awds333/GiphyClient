package io.demo.fedchenko.giphyclient.di

import io.demo.fedchenko.giphyclient.repository.Repository
import io.demo.fedchenko.giphyclient.viewmodel.MainViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

val viewModelModule: Module = module {
    viewModel { MainViewModel( gifProvider = get( ) as Repository) }
}

val repositoryModule: Module = module {
    single { Repository(getProperty("qiphy_key")) }
}
