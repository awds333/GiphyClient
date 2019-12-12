package io.demo.fedchenko.giphyclient.viewmodel

import androidx.lifecycle.*
import io.demo.fedchenko.giphyclient.model.GifModel
import io.demo.fedchenko.giphyclient.repository.GifManager
import io.demo.fedchenko.giphyclient.repository.GifProvider
import io.demo.fedchenko.giphyclient.repository.SharedPreferencesTermsRepo
import io.demo.fedchenko.giphyclient.repository.loader.GifLoader
import io.demo.fedchenko.giphyclient.repository.loader.SearchGifLoader
import io.demo.fedchenko.giphyclient.repository.loader.TrendingGifLoader
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect

interface OnScrollListener {
    fun onScroll(position: Int)
}

class MainViewModel(
    private val gifProvider: GifProvider,
    private val termsRepo: SharedPreferencesTermsRepo,
    private val gifManager: GifManager
) : ViewModel(), OnScrollListener {

    private val isLoadingLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val isLoading: LiveData<Boolean>
        get() = isLoadingLiveData

    private val gifModelsLiveData: MutableLiveData<List<GifModel>> = MutableLiveData()
    val gifModelsCount: LiveData<Int>
        get() = Transformations.map(gifModelsLiveData) { it.size }

    private val isCloseButtonVisibleLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val isCloseButtonVisible: LiveData<Boolean>
        get() = isCloseButtonVisibleLiveData

    private val previousTermsLiveData: MutableLiveData<List<String>> = MutableLiveData()
    val previousTerms: LiveData<List<String>>
        get() = previousTermsLiveData

    private val isScrollEndLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val isScrollEnd: LiveData<Boolean>
        get() = isScrollEndLiveData

    private var exceptionListener: (() -> Unit)? = null
    private var keyboardListener: (() -> Unit)? = null

    private var lastTerm = ""

    private var gifLoader: GifLoader =
        TrendingGifLoader(
            gifProvider
        )

    private var favoriteGifsIds: List<String> = emptyList()

    val searchText: MutableLiveData<String> = MutableLiveData()

    private var requestJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Main)

    init {
        isLoadingLiveData.value = false
        gifModelsLiveData.value = emptyList()
        isCloseButtonVisibleLiveData.value = false
        isScrollEndLiveData.value = false
        searchText.observeForever {
            isCloseButtonVisibleLiveData.value = it.isNotEmpty()
        }
        previousTermsLiveData.value = termsRepo.getTerms()

        scope.launch {
            gifManager.getGifsFlow().collect {
                favoriteGifsIds = it.map { gifModel -> gifModel.id }
                showGifsWithFavorite(gifModelsLiveData.value ?: emptyList())
            }
        }

        subscribeToLoader()
    }

    override fun onScroll(position: Int) {
        if (position > (gifModelsLiveData.value?.size ?: Int.MAX_VALUE) - 20) {
            getMoreGifs()
            if (position == (gifModelsLiveData.value?.size ?: 0) - 1)
                isScrollEndLiveData.value = true
        }
    }

    fun search() {
        val trimTerm = searchText.value?.trim() ?: return
        if (trimTerm.isEmpty())
            return
        lastTerm = trimTerm
        keyboardListener?.invoke()
        gifLoader =
            SearchGifLoader(
                gifProvider,
                trimTerm
            )
        if (previousTermsLiveData.value?.contains(trimTerm) != true) {
            previousTermsLiveData.value = (previousTermsLiveData.value ?: emptyList()) + trimTerm
            termsRepo.saveTerms(previousTermsLiveData.value ?: emptyList())
        }
        subscribeToLoader()
    }

    fun clean() {
        searchText.value = ""
        if (lastTerm.isNotEmpty()) {
            getTrending()
        }
    }

    fun refresh() {
        gifLoader = if (lastTerm.isEmpty()) TrendingGifLoader(
            gifProvider
        )
        else SearchGifLoader(
            gifProvider,
            lastTerm
        )
        subscribeToLoader()
    }

    private fun getTrending() {
        if (lastTerm.isNotEmpty())
            searchText.value = ""
        lastTerm = ""
        keyboardListener?.invoke()
        gifLoader =
            TrendingGifLoader(
                gifProvider
            )
        subscribeToLoader()
    }


    private fun subscribeToLoader() {
        isLoadingLiveData.value = false
        requestJob?.cancel()
        gifModelsLiveData.value = emptyList()
        getMoreGifs()
    }

    private fun getMoreGifs() {
        if (isLoadingLiveData.value != true) {
            isLoadingLiveData.value = true
            requestJob = scope.launch {
                try {
                    val models = gifLoader.loadMoreGifs()
                    showGifsWithFavorite(models)
                } catch (e: Throwable) {
                    exceptionListener?.invoke()
                }
                isLoadingLiveData.value = false
                isScrollEndLiveData.value = false
            }
        }
    }

    private fun showGifsWithFavorite(gifs: List<GifModel>) {
        gifModelsLiveData.value = gifs.map {
            it.copy(isFavorite = favoriteGifsIds.contains(it.id))
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
        scope.cancel()
    }
}