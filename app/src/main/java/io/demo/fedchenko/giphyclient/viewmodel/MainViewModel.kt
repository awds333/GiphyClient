package io.demo.fedchenko.giphyclient.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import io.demo.fedchenko.giphyclient.model.GifModel
import io.demo.fedchenko.giphyclient.repository.GifProvider
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable

interface ExceptionListener {
    fun handleException()
}

class MainViewModel(application: Application, var gifProvider: GifProvider) :
    AndroidViewModel(application) {

    private val isLoadingLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val gifModelsLiveData: MutableLiveData<List<GifModel>> = MutableLiveData()
    private var exceptionListener: ExceptionListener? = null

    private var trending = true
    private var lustTerm = ""
    private val compositeDisposable = CompositeDisposable()

    init {
        isLoadingLiveData.value = false
        gifModelsLiveData.value = emptyList()
        getTrending()
    }

    fun search(term: String) {
        if (term.trim() == "")
            return
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
                    }
                    , {
                        isLoadingLiveData.value = false
                        exceptionListener?.handleException()
                    }
                )
        )
        gifModelsLiveData.value = emptyList()

    }

    fun getTrending() {
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
                    }
                    , {
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
                }
                , {
                    isLoadingLiveData.value = false
                    exceptionListener?.handleException()
                }
            )
        )
    }


    fun observeIsLoading(lifecycleOwner: LifecycleOwner, observer: Observer<Boolean>) {
        isLoadingLiveData.observe(lifecycleOwner, observer)
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

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}