package io.demo.fedchenko.giphyclient.repository.loader

import io.demo.fedchenko.giphyclient.model.GifModel
import io.demo.fedchenko.giphyclient.repository.TrendingGifProvider

class TrendingGifLoader(private val provider: TrendingGifProvider, private val count: Int = 50) :
    GifLoader() {
    override fun buildRequest(offset: Int): suspend () -> List<GifModel>? =
        { provider.getTrendingGifs(count, offset) }
}