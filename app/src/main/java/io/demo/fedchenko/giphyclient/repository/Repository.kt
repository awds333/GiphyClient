package io.demo.fedchenko.giphyclient.repository

import io.demo.fedchenko.giphyclient.model.GifInfo
import io.demo.fedchenko.giphyclient.model.GifModel
import io.demo.fedchenko.giphyclient.model.GifNotParsedModel
import io.demo.fedchenko.giphyclient.model.GifProperties
import io.demo.fedchenko.giphyclient.retrofit.GiphyAPI
import io.reactivex.Observable


class Repository(private val giphyKey: String, private val giphyAPI: GiphyAPI) : GifProvider{

    private fun fromRaw(notParsedModel: GifNotParsedModel): GifModel {
        val original = propertiesFromRaw(notParsedModel.images.gifInfo)
        val preview = if (notParsedModel.images.previewGifInfo.url.isNotEmpty())
            propertiesFromRaw(notParsedModel.images.previewGifInfo)
        else
            original
        return GifModel(
            original,
            preview,
            notParsedModel.user.name,
            notParsedModel.title,
            notParsedModel.importDateTime
        )
    }

    private fun propertiesFromRaw(info: GifInfo) = GifProperties(
        info.width,
        info.height,
        info.url,
        info.size
    )

    override fun getByTerm(term: String, count: Int, offset: Int): Observable<List<GifModel>> {
        return giphyAPI.findGifsByTerm(term, count, offset, giphyKey)
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