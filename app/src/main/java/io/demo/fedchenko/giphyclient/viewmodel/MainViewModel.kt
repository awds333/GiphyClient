package io.demo.fedchenko.giphyclient.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import io.demo.fedchenko.giphyclient.model.GifModel
import io.demo.fedchenko.giphyclient.repository.GifProvider
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable

class MainViewModel(application: Application, var gifProvider: GifProvider) :
    AndroidViewModel(application) {

    enum class State {
        NORMAL, REQUEST_FAILED
    }

    var loading: MutableLiveData<Boolean> = MutableLiveData()
    var gifModels: MutableLiveData<MutableList<GifModel>> = MutableLiveData()
    var state: MutableLiveData<State> = MutableLiveData()

    private var trending = true
    private var lustTerm = ""
    private var compositeDisposable = CompositeDisposable()
    private var app: Application = application

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
                        clearCache()
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
                        clearCache()
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
                    gifModels.value = gifModels.value.apply { this!!.addAll(newModels) }
                }
                , {
                    loading.value = false
                    performException()
                    Log.d("awds", it.toString())
                }
            )
        )
    }

    private fun performException(){
        state.value = State.REQUEST_FAILED
        state.value = State.NORMAL
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
        clearCache()
    }


    private fun clearCache() {
        Thread(Runnable {
            Glide.get(app).clearDiskCache()
        }).start()
    }
}