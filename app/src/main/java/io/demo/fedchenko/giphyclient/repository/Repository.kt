package io.demo.fedchenko.giphyclient.repository

import io.demo.fedchenko.giphyclient.model.*
import io.demo.fedchenko.giphyclient.retrofit.GiphyAPI
import io.reactivex.Single


class Repository(private val giphyKey: String, private val giphyAPI: GiphyAPI) : GifProvider {

    private fun fromRaw(notParsedModel: GifNotParsedModel): GifModel? {

        val original = notParsedModel.images?.gifInfo?.properties() ?: return null
        val preview = notParsedModel.images.previewGifInfo?.properties() ?: original
        val name = notParsedModel.user?.name ?: ""

        return GifModel(
            original,
            preview,
            name,
            notParsedModel.title,
            notParsedModel.importDateTime
        )
    }

    private fun GifInfo.properties(): GifProperties? {
        if (width <= 0 || height <= 0 || url.isBlank())
            return null
        return GifProperties(width, height, url, size)
    }

    override fun getByTerm(term: String, count: Int, offset: Int): Single<List<GifModel>> =
        giphyAPI.findGifsByTerm(term, count, offset, giphyKey).toGifModelsList()


    override fun getTrendingGifs(count: Int, offset: Int): Single<List<GifModel>> =
        giphyAPI.getTrendingGifs(count, offset, giphyKey).toGifModelsList()

    private fun Single<ResponseModel>.toGifModelsList(): Single<List<GifModel>> =
        this.map { it.gifModels }
            .map {
                it.mapNotNull { nonParsed ->
                    fromRaw(nonParsed)
                }
            }
}