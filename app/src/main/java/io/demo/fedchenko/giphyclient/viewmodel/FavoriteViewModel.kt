package io.demo.fedchenko.giphyclient.viewmodel

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import io.demo.fedchenko.giphyclient.model.GifModel
import io.demo.fedchenko.giphyclient.repository.GifManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class FavoriteViewModel(private val gifManager: GifManager) : ViewModel() {

    private val gifModelsLiveData: MutableLiveData<List<GifModel>> = MutableLiveData()
    private val scope = CoroutineScope(Dispatchers.Main)



    init {
        gifModelsLiveData.value = emptyList()

        scope.launch {
            gifManager.getGifsFlow().collect {
                gifModelsLiveData.value = it
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