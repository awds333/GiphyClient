package io.demo.fedchenko.giphyclient.repository

import io.demo.fedchenko.giphyclient.model.GifModel
import io.reactivex.Single

class SearchGifLoader(private val provider: SearchGifProvider, private val term: String, private val count: Int = 25) :
    GifLoader() {
    override fun buildRequest(offset: Int): Single<List<GifModel>> =
        provider.getByTerm(term, count, offset)
}