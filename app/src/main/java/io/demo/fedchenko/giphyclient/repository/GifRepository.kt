package io.demo.fedchenko.giphyclient.repository

import io.demo.fedchenko.giphyclient.model.GifInfo
import io.demo.fedchenko.giphyclient.model.GifModel
import io.demo.fedchenko.giphyclient.model.GifNotParsedModel
import io.demo.fedchenko.giphyclient.model.GifProperties
import io.demo.fedchenko.giphyclient.retrofit.GiphyAPI


class GifRepository(private val giphyKey: String, private val giphyAPI: GiphyAPI) :
    BaseRepository(), GifProvider {

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

    override suspend fun getByTerm(term: String, count: Int, offset: Int): List<GifModel>? {
        val gifResponse = safeApiCall(
            call = { giphyAPI.findGifsByTerm(term, count, offset, giphyKey).await() },
            errorMessage = ""
        )

        return gifResponse?.gifModels?.mapNotNull { fromRaw(it) }
    }


    override suspend fun getTrendingGifs(count: Int, offset: Int): List<GifModel>? {
        val gifResponse = safeApiCall(
            call = { giphyAPI.getTrendingGifs(count, offset, giphyKey).await() },
            errorMessage = ""
        )

        return gifResponse?.gifModels?.mapNotNull { fromRaw(it) }
    }
}