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

interface Searcher {
    fun search(term: String)
}

class MainViewModel(private var gifProvider: GifProvider) :
    ViewModel(), Searcher {

    private val isLoadingLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val gifModelsLiveData: MutableLiveData<List<GifModel>> = MutableLiveData()
    private val isCleanButtonVisibleLiveData: MutableLiveData<Boolean> = MutableLiveData()
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
        isCleanButtonVisibleLiveData.value = false
        searchText.observeForever {
            isCleanButtonVisibleLiveData.value = it.isNotEmpty()
        }
        subscribeToLoader()
    }

    override fun search(term: String) {
        val trimedTerm = term.trim()
        if (trimedTerm == "" || trimedTerm == lastTerm)
            return
        lastTerm = trimedTerm
        keyboardListener?.hideKeyboard()
        gifLoader.close()
        gifLoader = SearchGifLoader(gifProvider, trimedTerm)
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

    fun getIsLoading(): LiveData<Boolean> = isLoadingLiveData

    fun getIsCloseButtonVisible(): LiveData<Boolean> = isCleanButtonVisibleLiveData

    override fun onCleared() {
        super.onCleared()
        gifLoader.close()
    }
}