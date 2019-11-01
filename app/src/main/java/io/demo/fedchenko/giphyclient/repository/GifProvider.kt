package io.demo.fedchenko.giphyclient.repository

import io.demo.fedchenko.giphyclient.model.GifModel
import io.reactivex.Observable

interface GifProvider : SearchGifProvider, TrendingGifProvider

interface SearchGifProvider {
    fun getByTerm(term: String, count: Int = 25, offset: Int = 0): Observable<List<GifModel>>
}

interface TrendingGifProvider {
    fun getTrendingGifs(count: Int = 25, offset: Int = 0): Observable<List<GifModel>>
}