package io.demo.fedchenko.giphyclient.viewmodel

import androidx.lifecycle.*
import io.demo.fedchenko.giphyclient.model.GifModel
import io.demo.fedchenko.giphyclient.repository.GifLoader
import io.demo.fedchenko.giphyclient.repository.GifProvider
import io.demo.fedchenko.giphyclient.repository.SearchGifLoader
import io.demo.fedchenko.giphyclient.repository.TrendingGifLoader
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable

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

    private var trending = true
    private var lastTerm = ""
    private val compositeDisposable = CompositeDisposable()

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
        if (term.trim() == "" || term == lastTerm)
            return
        lastTerm = term
        trending = false
        keyboardListener?.hideKeyboard()
        compositeDisposable.clear()
        gifLoader.close()
        gifLoader = SearchGifLoader(gifProvider, term)
        subscribeToLoader()
    }

    fun clean() {
        searchText.value = ""
        if (!trending)
            getTrending()
    }

    fun getTrending() {
        if (!trending)
            searchText.value = ""
        trending = true
        keyboardListener?.hideKeyboard()
        compositeDisposable.clear()
        gifLoader.close()
        gifLoader = TrendingGifLoader(gifProvider)
        subscribeToLoader()
    }

    private fun subscribeToLoader() {
        compositeDisposable.add(gifLoader.gifsObservable
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                gifModelsLiveData.value = it
                isLoadingLiveData.value = gifLoader.isLoading
            })
        compositeDisposable.add(gifLoader.exceptionsObservable
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                exceptionListener?.handleException()
                isLoadingLiveData.value = gifLoader.isLoading
            })
        getMoreGifs()
    }

    fun getMoreGifs() {
        gifLoader.loadMoreGifs()
        isLoadingLiveData.value = gifLoader.isLoading
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
        compositeDisposable.dispose()
        gifLoader.close()
    }
}