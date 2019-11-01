package io.demo.fedchenko.giphyclient.repository

import io.demo.fedchenko.giphyclient.model.GifModel
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.Disposables
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

abstract class GifLoader {
    var isLoading = false
        private set

    private val gifsPublisher: PublishSubject<List<GifModel>> = PublishSubject.create()
    val gifsObservable: Observable<List<GifModel>> = gifsPublisher

    private val exceptionsPublisher: PublishSubject<Throwable> = PublishSubject.create()
    val exceptionsObservable: Observable<Throwable> = exceptionsPublisher

    private var disposable: Disposable = Disposables.empty()

    private var loadedGifModels: List<GifModel> = emptyList()
    init {
        gifsPublisher.onNext(emptyList())
    }

    fun loadMoreGifs() {
        if (isLoading)
            return
        isLoading = true
        disposable = buildRequest(loadedGifModels.size).subscribeOn(Schedulers.io()).subscribe(
            {
                isLoading = false
                loadedGifModels = loadedGifModels + it
                gifsPublisher.onNext(loadedGifModels)
            }, {
                exceptionsPublisher.onNext(it)
            }
        )
    }

    protected abstract fun buildRequest(offset: Int): Observable<List<GifModel>>

    fun close() {
        disposable.dispose()
    }
}