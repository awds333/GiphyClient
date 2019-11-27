package io.demo.fedchenko.giphyclient.repository.loader

import io.demo.fedchenko.giphyclient.model.GifModel
import io.demo.fedchenko.giphyclient.repository.SearchGifProvider

class SearchGifLoader(private val provider: SearchGifProvider, private val term: String, private val count: Int = 50) :
    GifLoader() {
    override fun buildRequest(offset: Int): suspend () -> List<GifModel>? =
        { provider.getByTerm(term, count, offset) }
}