package io.demo.fedchenko.giphyclient.repository

import io.demo.fedchenko.giphyclient.model.GifModel
import io.demo.fedchenko.giphyclient.model.GifNotParsedModel
import io.demo.fedchenko.giphyclient.model.GifProperties
import io.demo.fedchenko.giphyclient.retrofit.GiphyAPI
import io.demo.fedchenko.giphyclient.retrofit.RetrofitClient
import io.reactivex.Observable


class Repository(private val giphyKey: String) : GifProvider {
    private val giphyAPI: GiphyAPI = RetrofitClient.instance.create(GiphyAPI::class.java)

    private fun fromRaw(notParsedModel: GifNotParsedModel): GifModel {
        val original = GifProperties(
            notParsedModel.images.gifInfo.width,
            notParsedModel.images.gifInfo.height,
            notParsedModel.images.gifInfo.url,
            notParsedModel.images.gifInfo.size
        )
        val preview = if (notParsedModel.images.previewGifInfo.url.isNotEmpty()) GifProperties(
            notParsedModel.images.previewGifInfo.width,
            notParsedModel.images.previewGifInfo.height,
            notParsedModel.images.previewGifInfo.url,
            notParsedModel.images.previewGifInfo.size
        ) else original
        return GifModel(
            original,
            preview,
            notParsedModel.user.name,
            notParsedModel.title,
            notParsedModel.importDateTime
        )
    }

    override fun getByTerm(term: String, count: Int, offset: Int): Observable<List<GifModel>> {
        return giphyAPI.findGifsByTerm(term, count, offset,giphyKey)
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
        return giphyAPI.getTrendingGifs(count, offset, giphyKey)
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