package io.demo.fedchenko.giphyclient.viewmodel

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import io.demo.fedchenko.giphyclient.model.GifModel
import io.demo.fedchenko.giphyclient.repository.FavoriteManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class FavoriteViewModel(private val favoriteManager: FavoriteManager) : ViewModel() {

    private val gifModelsLiveData: MutableLiveData<List<GifModel>> = MutableLiveData()
    private val scope = CoroutineScope(Dispatchers.Main)


    init {
        gifModelsLiveData.value = emptyList()

        scope.launch {
            favoriteManager.getGifsFlow().collect {
                gifModelsLiveData.value = it
            }
        }
    }

    fun changeFavorite(model: GifModel) {
        scope.launch {
            try {
                if (gifModelsLiveData.value!!.map { it.id }.contains(model.id))
                    favoriteManager.delete(model)
                else
                    favoriteManager.addGif(model)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun observeGifModels(lifecycleOwner: LifecycleOwner, observer: Observer<List<GifModel>>) {
        gifModelsLiveData.observe(lifecycleOwner, observer)
    }

    override fun onCleared() {
        super.onCleared()
        scope.cancel()
    }
}