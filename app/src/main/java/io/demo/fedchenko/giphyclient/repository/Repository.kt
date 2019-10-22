package io.demo.fedchenko.giphyclient.repository

import io.demo.fedchenko.giphyclient.model.GifModel
import io.demo.fedchenko.giphyclient.model.GifNotParsedModel
import io.demo.fedchenko.giphyclient.retrofit.GyphyAPI
import io.demo.fedchenko.giphyclient.retrofit.RetrofitClient
import io.reactivex.Observable

fun fromRaw(notParsedModel: GifNotParsedModel): GifModel {
    return GifModel(
        notParsedModel.images.gifInfo.width,
        notParsedModel.images.gifInfo.height,
        notParsedModel.images.gifInfo.url
    )
}

class Repository : GifProvider {
    private val gyphyAPI: GyphyAPI = RetrofitClient.instance.create(GyphyAPI::class.java)

    override fun getByTerm(term: String, count: Int, offset: Int): Observable<List<GifModel>> {
        return gyphyAPI.findGifsByTerm(term, count, offset)
            .map { it.gifModels }
            .map {
                val gifList = emptyList<GifModel>().toMutableList()
                it.forEach { notParsedModel ->
                    gifList.add(fromRaw(notParsedModel))
                }
                return@map gifList
            }
    }

    override fun getTrendingGifs(count: Int, offset: Int): Observable<List<GifModel>> {
        return gyphyAPI.getTrendingGifs(count, offset)
            .map { it.gifModels }
            .map {
                val gifList = emptyList<GifModel>().toMutableList()
                it.forEach { notParsedModel ->
                    gifList.add(fromRaw(notParsedModel))
                }
                return@map gifList
            }
    }


}