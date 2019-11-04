package io.demo.fedchenko.giphyclient.repository

import io.demo.fedchenko.giphyclient.model.GifModel
import io.reactivex.Single

class TrendingGifLoader(private val provider: TrendingGifProvider) : GifLoader() {
    override fun buildRequest(offset: Int): Single<List<GifModel>> =
        provider.getTrendingGifs(25, offset)
}