package io.demo.fedchenko.giphyclient.viewmodel

import androidx.lifecycle.*
import io.demo.fedchenko.giphyclient.model.GifModel
import io.demo.fedchenko.giphyclient.repository.GifLoader
import io.demo.fedchenko.giphyclient.repository.GifProvider
import io.demo.fedchenko.giphyclient.repository.SearchGifLoader
import io.demo.fedchenko.giphyclient.repository.TrendingGifLoader

class MainViewModel(private var gifProvider: GifProvider) :
    ViewModel() {

    private val isLoadingLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val isLoading: LiveData<Boolean> = isLoadingLiveData
    private val gifModelsLiveData: MutableLiveData<List<GifModel>> = MutableLiveData()
    private val isCloseButtonVisibleLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val isCloseButtonVisible: LiveData<Boolean> = isCloseButtonVisibleLiveData
    private var exceptionListener: (() -> Unit)? = null
    private var keyboardListener: (() -> Unit)? = null

    private val loaderExceptionListener = { exception: Throwable ->
        exceptionListener?.invoke()
        isLoadingLiveData.value = false
    }
    private val loaderModelsListListener = { models: List<GifModel> ->
        gifModelsLiveData.value = models
        isLoadingLiveData.value = false

    }

    private var lastTerm = ""

    private var gifLoader: GifLoader = TrendingGifLoader(gifProvider)

    val searchText: MutableLiveData<String> = MutableLiveData()

    init {
        isLoadingLiveData.value = false
        gifModelsLiveData.value = emptyList()
        isCloseButtonVisibleLiveData.value = false
        searchText.observeForever {
            isCloseButtonVisibleLiveData.value = it.isNotEmpty()
        }
        subscribeToLoader()
    }

    fun search() {
        val trimTerm = searchText.value?.trim() ?: return
        if (trimTerm == "" || trimTerm == lastTerm)
            return
        lastTerm = trimTerm
        keyboardListener?.invoke()
        gifLoader.close()
        gifLoader = SearchGifLoader(gifProvider, trimTerm)
        subscribeToLoader()
    }

    fun clean() {
        searchText.value = ""
        if (lastTerm.isNotEmpty())
            getTrending()
    }

    fun getTrending() {
        if (lastTerm.isNotEmpty())
            searchText.value = ""
        lastTerm = ""
        keyboardListener?.invoke()
        gifLoader.close()
        gifLoader = TrendingGifLoader(gifProvider)
        subscribeToLoader()
    }

    private fun subscribeToLoader() {
        gifLoader.setGifModelsListListener(loaderModelsListListener)
        gifLoader.setExceptionsListener(loaderExceptionListener)
        isLoadingLiveData.value = true
        gifModelsLiveData.value = emptyList()
        gifLoader.loadMoreGifs()
    }

    fun getMoreGifs() {
        if (isLoadingLiveData.value != true) {
            isLoadingLiveData.value = true
            gifLoader.loadMoreGifs()
        }
    }

    fun observeGifModels(lifecycleOwner: LifecycleOwner, observer: Observer<List<GifModel>>) {
        gifModelsLiveData.observe(lifecycleOwner, observer)
    }

    fun registerExceptionsListener(listener: (() -> Unit)) {
        exceptionListener = listener
    }

    fun removeExceptionListener() {
        exceptionListener = null
    }

    fun registerKeyboardListener(listener: (() -> Unit)) {
        keyboardListener = listener
    }

    fun removeKeyboardListener() {
        keyboardListener = null
    }

    override fun onCleared() {
        super.onCleared()
        gifLoader.close()
    }
}