package io.demo.fedchenko.giphyclient.repository

import io.demo.fedchenko.giphyclient.model.GifModel

class TrendingGifLoader(private val provider: TrendingGifProvider, private val count: Int = 25) :
    GifLoader() {
    override fun buildRequest(offset: Int): suspend () -> List<GifModel>? =
        { provider.getTrendingGifs(count, offset) }
}