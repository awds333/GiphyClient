package io.demo.fedchenko.giphyclient.viewmodel

import androidx.lifecycle.*
import io.demo.fedchenko.giphyclient.model.GifModel
import io.demo.fedchenko.giphyclient.repository.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

interface ScrollListener {
    fun onScrollEnd()
    fun onScrollHalf()
}
//BindingAdapter не хочет работать получая на вход два ()->Unit по двум разеным пораметрам.
//Потому, сделал интерфейс.

class MainViewModel(
    private var gifProvider: GifProvider,
    private val termsRepo: SharedPreferencesTermsRepo
) :
    ViewModel(), ScrollListener {

    private val isLoadingLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val isLoading: LiveData<Boolean> = isLoadingLiveData
    private val isLoadingMoreLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val isLoadingMore: LiveData<Boolean> = isLoadingMoreLiveData
    private val gifModelsLiveData: MutableLiveData<List<GifModel>> = MutableLiveData()
    private val isCloseButtonVisibleLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val isCloseButtonVisible: LiveData<Boolean> = isCloseButtonVisibleLiveData
    private val previousTermsLiveData: MutableLiveData<List<String>> = MutableLiveData()
    val previousTerms: LiveData<List<String>> = previousTermsLiveData
    private val isScrollEndLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val isScrollEnd: LiveData<Boolean> = isScrollEndLiveData

    private var exceptionListener: (() -> Unit)? = null
    private var keyboardListener: (() -> Unit)? = null

    private var lastTerm = ""

    private var gifLoader: GifLoader = TrendingGifLoader(gifProvider)

    val searchText: MutableLiveData<String> = MutableLiveData()

    private val scope = CoroutineScope(Dispatchers.Main)
    private var job: Job? = null

    init {
        isLoadingMoreLiveData.value = false
        isLoadingLiveData.value = false
        gifModelsLiveData.value = emptyList()
        isCloseButtonVisibleLiveData.value = false
        searchText.observeForever {
            isCloseButtonVisibleLiveData.value = it.isNotEmpty()
        }
        previousTermsLiveData.value = termsRepo.getTerms()
        subscribeToLoader()
    }

    override fun onScrollEnd() {
        isScrollEndLiveData.value = true
    }

    override fun onScrollHalf() = getMoreGifs()

    fun search() {
        val trimTerm = searchText.value?.trim() ?: return
        if (trimTerm.isEmpty())
            return
        lastTerm = trimTerm
        keyboardListener?.invoke()
        gifLoader = SearchGifLoader(gifProvider, trimTerm)
        if (previousTermsLiveData.value?.contains(trimTerm) != true) {
            previousTermsLiveData.value = (previousTermsLiveData.value ?: emptyList()) + trimTerm
            termsRepo.saveTerms(previousTermsLiveData.value ?: emptyList())
        }
        subscribeToLoader()
    }

    fun clean() {
        searchText.value = ""
        if (lastTerm.isNotEmpty())
            getTrending()
    }

    fun refresh() {
        gifLoader = if (lastTerm.isEmpty()) TrendingGifLoader(gifProvider)
        else SearchGifLoader(gifProvider, lastTerm)
        subscribeToLoader()
    }

    private fun getTrending() {
        if (lastTerm.isNotEmpty())
            searchText.value = ""
        lastTerm = ""
        keyboardListener?.invoke()
        gifLoader = TrendingGifLoader(gifProvider)
        subscribeToLoader()
    }


    private fun subscribeToLoader() {
        isLoadingLiveData.value = false
        isLoadingMoreLiveData.value = false
        job?.cancel()
        gifModelsLiveData.value = emptyList()
        getMoreGifs()
    }

    private fun getMoreGifs() {
        if (isLoadingLiveData.value != true && isLoadingMoreLiveData.value != true) {
            if (gifModelsLiveData.value.isNullOrEmpty())
                isLoadingLiveData.value = true
            else
                isLoadingMoreLiveData.value = true
            job = scope.launch {
                val gifs = gifLoader.loadMoreGifs()
                gifModelsLiveData.value = gifs
                isLoadingLiveData.value = false
                isLoadingMoreLiveData.value = false
                isScrollEndLiveData.value = false
            }
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
        job?.cancel()
    }
}