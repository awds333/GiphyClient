package io.demo.fedchenko.giphyclient.viewmodel

import androidx.lifecycle.*
import io.demo.fedchenko.giphyclient.model.GifModel
import io.demo.fedchenko.giphyclient.repository.GifLoader
import io.demo.fedchenko.giphyclient.repository.GifProvider
import io.demo.fedchenko.giphyclient.repository.SearchGifLoader
import io.demo.fedchenko.giphyclient.repository.TrendingGifLoader

interface ExceptionListener {
    fun handleException()
}

interface KeyboardListener {
    fun hideKeyboard()
}

class MainViewModel(private var gifProvider: GifProvider) :
    ViewModel() {

    private val isLoadingLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val isLoading: LiveData<Boolean> = isLoadingLiveData
    private val gifModelsLiveData: MutableLiveData<List<GifModel>> = MutableLiveData()
    private val isCloseButtonVisibleLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val isCloseButtonVisible: LiveData<Boolean> = isCloseButtonVisibleLiveData
    private var exceptionListener: ExceptionListener? = null
    private var keyboardListener: KeyboardListener? = null

    private val loaderExceptionListener = object : GifLoader.ExceptionsListener {
        override fun handleException(exception: Throwable) {
            exceptionListener?.handleException()
            isLoadingLiveData.value = false
        }
    }
    private val loaderModelsListListener = object : GifLoader.GifModelsListListener {
        override fun updateList(models: List<GifModel>) {
            gifModelsLiveData.value = models
            isLoadingLiveData.value = false
        }
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
        keyboardListener?.hideKeyboard()
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
        keyboardListener?.hideKeyboard()
        gifLoader.close()
        gifLoader = TrendingGifLoader(gifProvider)
        subscribeToLoader()
    }

    private fun subscribeToLoader() {
        gifLoader.setGifModelsListListener(loaderModelsListListener)
        gifLoader.setExceptionsListener(loaderExceptionListener)
        gifLoader.loadMoreGifs()
        isLoadingLiveData.value = true
        gifModelsLiveData.value = emptyList()
    }

    fun getMoreGifs() {
        if (isLoadingLiveData.value != true) {
            gifLoader.loadMoreGifs()
            isLoadingLiveData.value = true
        }
    }

    fun observeGifModels(lifecycleOwner: LifecycleOwner, observer: Observer<List<GifModel>>) {
        gifModelsLiveData.observe(lifecycleOwner, observer)
    }

    fun registerExceptionsListener(listener: ExceptionListener) {
        exceptionListener = listener
    }

    fun removeExceptionListener() {
        exceptionListener = null
    }

    fun registerKeyboardListener(listener: KeyboardListener) {
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