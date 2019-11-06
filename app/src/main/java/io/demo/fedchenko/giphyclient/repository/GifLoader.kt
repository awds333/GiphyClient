package io.demo.fedchenko.giphyclient.repository

import io.demo.fedchenko.giphyclient.model.GifModel
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.Disposables
import io.reactivex.schedulers.Schedulers

abstract class GifLoader {

    private var gifModelsListListener: ((List<GifModel>)->Unit)? = null

    private var exceptionsListener: ((Throwable)->Unit)? = null

    private var disposable: Disposable = Disposables.empty()

    private var loadedGifModels: List<GifModel> = emptyList()

    fun loadMoreGifs() {
        disposable = buildRequest(loadedGifModels.size)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    loadedGifModels = loadedGifModels + it
                    gifModelsListListener?.invoke(loadedGifModels)
                }, {
                    exceptionsListener?.invoke(it)
                }
            )
    }

    protected abstract fun buildRequest(offset: Int): Single<List<GifModel>>

    fun close() {
        disposable.dispose()
    }

    fun setExceptionsListener(listener: (Throwable)->Unit){
        exceptionsListener = listener
    }

    fun setGifModelsListListener(listener: (List<GifModel>)->Unit){
        gifModelsListListener = listener
    }
}