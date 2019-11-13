package io.demo.fedchenko.giphyclient.repository

import io.demo.fedchenko.giphyclient.model.GifModel

interface GifProvider : SearchGifProvider, TrendingGifProvider

interface SearchGifProvider {
    suspend fun getByTerm(term: String, count: Int = 25, offset: Int = 0): List<GifModel>?
}

interface TrendingGifProvider {
    suspend fun getTrendingGifs(count: Int = 25, offset: Int = 0): List<GifModel>?
}