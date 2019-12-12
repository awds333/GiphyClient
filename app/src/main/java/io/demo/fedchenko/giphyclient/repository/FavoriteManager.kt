package io.demo.fedchenko.giphyclient.repository

import io.demo.fedchenko.giphyclient.model.GifModel
import kotlinx.coroutines.flow.Flow

interface FavoriteManager {
    fun getGifsFlow():Flow<List<GifModel>>

    suspend fun addGif(gifModel: GifModel)
    suspend fun delete(gifModel: GifModel)
}