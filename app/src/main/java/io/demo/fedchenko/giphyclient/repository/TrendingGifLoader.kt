package io.demo.fedchenko.giphyclient.repository

import io.demo.fedchenko.giphyclient.model.GifModel
import io.reactivex.Observable

class TrendingGifLoader(private val provider: TrendingGifProvider) : GifLoader() {
    override fun buildRequest(offset: Int): Observable<List<GifModel>> =
        provider.getTrendingGifs(25, offset)
}