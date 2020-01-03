package io.demo.fedchenko.giphyclient.repository

import io.demo.fedchenko.giphyclient.model.GifModel
import io.demo.fedchenko.giphyclient.model.GifProperties
import io.demo.fedchenko.giphyclient.room.AppDataBase
import io.demo.fedchenko.giphyclient.room.DbGif
import io.demo.fedchenko.giphyclient.room.GifDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomFavoriteManager(private val gifDao: GifDao) : FavoriteManager {

    override suspend fun addGif(gifModel: GifModel) {
        gifDao.insertAll(gifModel.toDbGif())
    }

    override suspend fun delete(gifModel: GifModel) {
        gifDao.delete(gifModel.toDbGif())
    }

    override fun getGifsFlow(): Flow<List<GifModel>> =
        gifDao.getAll().map { it.mapNotNull { dbGif -> dbGif.toGifModel() } }

    private fun DbGif.toGifModel(): GifModel? {
        return GifModel(
            original = GifProperties(
                originalWidth ?: return null,
                originalHeight ?: return null,
                originalUrl ?: return null,
                originalSize ?: return null
            ),
            preview = GifProperties(
                previewWidth ?: return null,
                previewHeight ?: return null,
                previewUrl ?: return null,
                previewSize ?: return null
            ),
            id = id_str ?: return null,
            userName = userName ?: return null,
            title = title ?: return null,
            importDateTime = importDateTime ?: return null,
            isFavorite = true
        )
    }

    private fun GifModel.toDbGif(): DbGif {
        return DbGif(
            id = id.hashCode().toLong(),
            id_str = id,
            title = title,
            userName = userName,
            importDateTime = importDateTime,

            originalWidth = original.width,
            originalHeight = original.height,
            originalUrl = original.url,
            originalSize = original.size,

            previewWidth = preview.width,
            previewHeight = preview.height,
            previewUrl = preview.url,
            previewSize = preview.size
        )
    }
}