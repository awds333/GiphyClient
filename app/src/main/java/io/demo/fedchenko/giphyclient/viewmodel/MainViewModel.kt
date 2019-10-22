package io.demo.fedchenko.giphyclient.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.demo.fedchenko.giphyclient.model.GifModel
import io.demo.fedchenko.giphyclient.repository.GifProvider
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable

class MainViewModel(application: Application, var gifProvider: GifProvider) :
    AndroidViewModel(application) {

    enum class State {
        NORMAL, REQUEST_FAILED
    }

    private val loading: MutableLiveData<Boolean> = MutableLiveData()
    private val gifModels: MutableLiveData<List<GifModel>> = MutableLiveData()
    private val state: MutableLiveData<State> = MutableLiveData()

    private var trending = true
    private var lustTerm = ""
    private val compositeDisposable = CompositeDisposable()

    init {
        loading.value = false
        gifModels.value = emptyList<GifModel>().toMutableList()
        state.value = State.NORMAL
        getTrending()
    }

    fun search(term: String) {
        if (term.trim() == "")
            return
        compositeDisposable.clear()
        loading.value = true
        compositeDisposable.add(
            gifProvider.getByTerm(term.trim())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        lustTerm = term.trim()
                        trending = false
                        loading.value = false
                        gifModels.value = it.toMutableList()
                    }
                    , {
                        loading.value = false
                        performException()
                        Log.d("awds", it.toString())
                    }
                )
        )
        gifModels.value = emptyList<GifModel>().toMutableList()

    }

    fun getTrending() {
        compositeDisposable.clear()
        loading.value = true
        compositeDisposable.add(
            gifProvider.getTrendingGifs()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        trending = true
                        loading.value = false
                        gifModels.value = it.toMutableList()
                    }
                    , {
                        loading.value = false
                        performException()
                        Log.d("awds", it.toString())
                    }
                )
        )
    }

    fun getMoreGifs() {
        if (loading.value!!)
            return
        compositeDisposable.clear()
        loading.value = true
        val observable = if (trending)
            gifProvider.getTrendingGifs(25, gifModels.value!!.size)
        else
            gifProvider.getByTerm(lustTerm, 25, gifModels.value!!.size)
        compositeDisposable.add(observable.observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { newModels: List<GifModel> ->
                    loading.value = false
                    gifModels.value = gifModels.value!!.plus(newModels)
                }
                , {
                    loading.value = false
                    performException()
                    Log.d("awds", it.toString())
                }
            )
        )
    }

    fun getLoading():LiveData<Boolean>{
        return loading
    }

    fun getGifModels():LiveData<List<GifModel>>{
        return gifModels
    }

    fun getState():LiveData<State>{
        return state
    }

    private fun performException(){
        state.value = State.REQUEST_FAILED
        state.value = State.NORMAL
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}