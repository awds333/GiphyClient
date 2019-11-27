package io.demo.fedchenko.giphyclient.repository.loader

import io.demo.fedchenko.giphyclient.model.GifModel

abstract class GifLoader {

    private var loadedGifModels: List<GifModel> = emptyList()

    suspend fun loadMoreGifs(): List<GifModel> {
        val models = buildRequest(loadedGifModels.size).invoke()
        loadedGifModels = loadedGifModels + (models ?: emptyList())
        return  loadedGifModels
    }

    protected abstract fun buildRequest(offset: Int): suspend () -> List<GifModel>?
}