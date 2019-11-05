package io.demo.fedchenko.giphyclient.repository

import io.demo.fedchenko.giphyclient.model.GifModel
import io.reactivex.Single

class TrendingGifLoader(private val provider: TrendingGifProvider, private val count: Int = 25) :
    GifLoader() {
    override fun buildRequest(offset: Int): Single<List<GifModel>> =
        provider.getTrendingGifs(count, offset)
}