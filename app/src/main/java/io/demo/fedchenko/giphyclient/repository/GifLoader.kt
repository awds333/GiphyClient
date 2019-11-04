package io.demo.fedchenko.giphyclient.repository

import io.demo.fedchenko.giphyclient.model.GifModel
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.Disposables
import io.reactivex.schedulers.Schedulers

abstract class GifLoader {

    interface ExceptionsListener {
        fun handleException(exception: Throwable)
    }

    interface GifModelsListListener {
        fun updateList(models: List<GifModel>)
    }

    private var gifModelsListListener: GifModelsListListener? = null

    private var exceptionsListener: ExceptionsListener? = null

    private var disposable: Disposable = Disposables.empty()

    private var loadedGifModels: List<GifModel> = emptyList()

    fun loadMoreGifs() {
        disposable = buildRequest(loadedGifModels.size)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    loadedGifModels = loadedGifModels + it
                    gifModelsListListener?.updateList(loadedGifModels)
                }, {
                    exceptionsListener?.handleException(it)
                }
            )
    }

    protected abstract fun buildRequest(offset: Int): Single<List<GifModel>>

    fun close() {
        disposable.dispose()
    }

    fun setExceptionsListener(listener: ExceptionsListener){
        exceptionsListener = listener
    }

    fun setGifModelsListListener(listener: GifModelsListListener){
        gifModelsListListener = listener
    }
}