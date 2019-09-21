package io.demo.fedchenko.giphyclient.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.demo.fedchenko.giphyclient.repository.GifProvider

class MainViewModelFactory(
    private var application: Application,
    private var gifProvider: GifProvider
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MainViewModel(application,gifProvider) as T
    }

}