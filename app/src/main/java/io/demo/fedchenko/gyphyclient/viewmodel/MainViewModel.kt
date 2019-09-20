package io.demo.fedchenko.gyphyclient.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import io.demo.fedchenko.gyphyclient.model.GifModel
import io.demo.fedchenko.gyphyclient.repository.GifProvider
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

    private var compositeDisposable = CompositeDisposable()

    init {
        loading.value = false
        gifModels.value = emptyList<GifModel>().toMutableList()
        state.value = State.NORMAL
        trending()
    }

    fun search(term: String) {
        if (term.trim() == "")
            return
        /*compositeDisposable.clear()
        loading.value = true
        compositeDisposable.add(
            gifProvider.getByTerm(term.trim())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        loading.value = false
                        gifModels.value = it.toMutableList()
                    }
                    , {
                        loading.value = false
                        state.value = State.REQUEST_FAILED
                    }
                )
        )*/
        gifModels.value = emptyList<GifModel>().toMutableList()

    }

    fun trending() {
        compositeDisposable.clear()
        loading.value = true
        compositeDisposable.add(
            gifProvider.getTrendingGifs()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        loading.value = false
                        gifModels.value = it.toMutableList()
                    }
                    , {
                        loading.value = false
                        state.value = State.REQUEST_FAILED
                    }
                )
        )
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}