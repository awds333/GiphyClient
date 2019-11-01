package io.demo.fedchenko.giphyclient.repository

import io.demo.fedchenko.giphyclient.model.GifModel
import io.reactivex.Observable

class SearchGifLoader(private val provider: SearchGifProvider, private val term: String) :
    GifLoader() {
    override fun buildRequest(offset: Int): Observable<List<GifModel>> =
        provider.getByTerm(term, 25, offset)
}