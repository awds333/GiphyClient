package io.demo.fedchenko.giphyclient.viewmodel

import androidx.lifecycle.*
import io.demo.fedchenko.giphyclient.model.GifModel
import io.demo.fedchenko.giphyclient.repository.GifProvider
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

class MainViewModel(var gifProvider: GifProvider) :
    ViewModel(), Searcher {

    private val isLoadingLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val gifModelsLiveData: MutableLiveData<List<GifModel>> = MutableLiveData()
    private val isCloseButtonVisibleLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private var exceptionListener: ExceptionListener? = null
    private var keyboardListener: KeyboardListener? = null

    private var trending = true
    private var lustTerm = ""
    private val compositeDisposable = CompositeDisposable()

    val searchText: MutableLiveData<String> = MutableLiveData()
    private val mediator = MediatorLiveData<String>()

    init {
        isLoadingLiveData.value = false
        gifModelsLiveData.value = emptyList()
        isCloseButtonVisibleLiveData.value = false
        mediator.apply {
            addSource(searchText) { value ->
                setValue(value)
                isCloseButtonVisibleLiveData.value = value.isNotEmpty()
            }
            observeForever {}
        }
        getTrending()
    }

    override fun search(term: String) {
        if (term.trim() == "")
            return
        keyboardListener?.hideKeyboard()
        compositeDisposable.clear()
        isLoadingLiveData.value = true
        compositeDisposable.add(
            gifProvider.getByTerm(term.trim())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        lustTerm = term.trim()
                        trending = false
                        isLoadingLiveData.value = false
                        gifModelsLiveData.value = it
                    },
                    {
                        isLoadingLiveData.value = false
                        exceptionListener?.handleException()
                    }
                )
        )
        gifModelsLiveData.value = emptyList()
    }

    fun clean() {
        searchText.value = ""
        if (!trending)
            getTrending()
    }

    fun getTrending() {
        if (!trending)
            searchText.value = ""
        keyboardListener?.hideKeyboard()
        compositeDisposable.clear()
        isLoadingLiveData.value = true
        compositeDisposable.add(
            gifProvider.getTrendingGifs()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        trending = true
                        isLoadingLiveData.value = false
                        gifModelsLiveData.value = it
                    },
                    {
                        isLoadingLiveData.value = false
                        exceptionListener?.handleException()
                    }
                )
        )
    }

    fun getMoreGifs() {
        if (isLoadingLiveData.value!!)
            return
        compositeDisposable.clear()
        isLoadingLiveData.value = true
        val observable = if (trending)
            gifProvider.getTrendingGifs(25, gifModelsLiveData.value!!.size)
        else
            gifProvider.getByTerm(lustTerm, 25, gifModelsLiveData.value!!.size)
        compositeDisposable.add(observable.observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { newModels: List<GifModel> ->
                    isLoadingLiveData.value = false
                    gifModelsLiveData.value = gifModelsLiveData.value!!.plus(newModels)
                },
                {
                    isLoadingLiveData.value = false
                    exceptionListener?.handleException()
                }
            )
        )
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

    fun getIsCloseButtonVisible(): LiveData<Boolean> = isCloseButtonVisibleLiveData

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}