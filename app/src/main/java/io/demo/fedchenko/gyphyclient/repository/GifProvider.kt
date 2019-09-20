package io.demo.fedchenko.gyphyclient.repository

import io.demo.fedchenko.gyphyclient.model.GifModel
import io.demo.fedchenko.gyphyclient.model.GifNotParsedModel
import io.reactivex.Observable

interface GifProvider {
    fun getTrendingGifs(count: Int = 25, offset: Int = 0): Observable<List<GifModel>>
    fun getByTerm(term: String, count: Int = 25, offset: Int = 0): Observable<List<GifModel>>
}