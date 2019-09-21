package io.demo.fedchenko.gyphyclient.repository

import io.demo.fedchenko.gyphyclient.model.GifModel
import io.demo.fedchenko.gyphyclient.retrofit.GyphyAPI
import io.demo.fedchenko.gyphyclient.retrofit.RetrofitClient
import io.reactivex.Observable

class Repository : GifProvider {
    private val gyphyAPI: GyphyAPI = RetrofitClient.instance.create(GyphyAPI::class.java)

    override fun getByTerm(term: String, count: Int, offset: Int): Observable<List<GifModel>> {
        return gyphyAPI.fingGifsByTerm(term, count, offset)
            .map { it.GifModels }
            .map {
                val gifList = emptyList<GifModel>().toMutableList()
                it.forEach { notParsedModel ->
                    val gifModel = GifModel(
                        notParsedModel.title,
                        notParsedModel.images.gifInfo.width,
                        notParsedModel.images.gifInfo.height,
                        notParsedModel.images.gifInfo.url,
                        notParsedModel.images.gifStill.url
                    )
                    gifList.add(gifModel)
                }
                return@map gifList
            }
    }

    override fun getTrendingGifs(count: Int, offset: Int): Observable<List<GifModel>> {
        return gyphyAPI.getTrendingGifs(count, offset)
            .map { it.GifModels }
            .map {
                val gifList = emptyList<GifModel>().toMutableList()
                it.forEach { notParsedModel ->
                    val gifModel = GifModel(
                        notParsedModel.title,
                        notParsedModel.images.gifInfo.width,
                        notParsedModel.images.gifInfo.height,
                        notParsedModel.images.gifInfo.url,
                        notParsedModel.images.gifStill.url
                    )
                    gifList.add(gifModel)
                }
                return@map gifList
            }
    }


}