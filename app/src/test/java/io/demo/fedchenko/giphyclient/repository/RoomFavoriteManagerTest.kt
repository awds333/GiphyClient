package io.demo.fedchenko.giphyclient.repository

import io.demo.fedchenko.giphyclient.model.GifModel
import io.demo.fedchenko.giphyclient.model.GifProperties
import io.demo.fedchenko.giphyclient.room.DbGif
import io.demo.fedchenko.giphyclient.room.GifDao
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.mockito.Mockito
import org.mockito.internal.verification.VerificationModeFactory.times
import kotlin.text.Typography.times

class RoomFavoriteManagerTest {
    private var gifDao = Mockito.mock(GifDao::class.java)
    private var publisher = ConflatedBroadcastChannel(emptyList<DbGif>())
    private var roomFavoriteManager = RoomFavoriteManager(gifDao)

    private val dbGifsList1 = listOf(
        DbGif(
            "10".hashCode().toLong(), "10", "title1", "u1", "t1", 20, 30,
            "org1", 100, 15, 25, "prev1", 85
        ),
        DbGif(
            "13".hashCode().toLong(), "13", "title2", "u2", "t2", 22, 32,
            "org2", 120, 17, 27, "prev2", 95
        )
    )
    private val dbGifsList2 = listOf(
        DbGif(
            "14".hashCode().toLong(), "14", "title3", "u3", "t3", 23, 33,
            "org3", 130, 18, 28, "prev3", 78
        )
    )

    private fun gifModelFromDbGif(dbGif: DbGif): GifModel = GifModel(
        dbGif.id_str!!,
        GifProperties(
            dbGif.originalWidth!!,
            dbGif.originalHeight!!,
            dbGif.originalUrl!!,
            dbGif.originalSize!!
        ),
        GifProperties(
            dbGif.previewWidth!!,
            dbGif.previewHeight!!,
            dbGif.previewUrl!!,
            dbGif.previewSize!!
        ),
        dbGif.userName!!,
        dbGif.title!!,
        dbGif.importDateTime!!,
        true
    )

    private fun initRoomFavoriteManager() {
        gifDao = Mockito.mock(GifDao::class.java)
        publisher = ConflatedBroadcastChannel(emptyList())
        Mockito.`when`(gifDao.getAll()).thenReturn(publisher.asFlow())
        roomFavoriteManager = RoomFavoriteManager(gifDao)
    }

    private fun initEmptyRoomFavoriteManager() {
        gifDao = Mockito.mock(GifDao::class.java)
        roomFavoriteManager = RoomFavoriteManager(gifDao)
    }

    @Test
    fun getNotEmptyFavoriteGifs() {
        initRoomFavoriteManager()

        runBlocking {
            publisher.offer(dbGifsList1)
            roomFavoriteManager.getGifsFlow().take(1).collect {
                assert(
                    it == listOf(
                        gifModelFromDbGif(dbGifsList1[0]),
                        gifModelFromDbGif(dbGifsList1[1])
                    )
                )
            }
            publisher.offer(dbGifsList2)
            roomFavoriteManager.getGifsFlow().take(1).collect {
                assert(
                    it == listOf(
                        gifModelFromDbGif(dbGifsList2[0])
                    )
                )
            }
        }
    }

    @Test
    fun getEmptyFavoriteGifs() {
        initRoomFavoriteManager()

        publisher.offer(emptyList())

        runBlocking {
            roomFavoriteManager.getGifsFlow().take(1).collect {
                assert(it == emptyList<GifModel>())
            }
        }
    }

    @Test
    fun getInvalidFavoriteGifs() {
        initRoomFavoriteManager()

        val model = DbGif(
            10, "10", "title1", "u1", "t1", 20, 30,
            "org1", 100, 15, 25, "prev1", 85
        )
        val invalidList = listOf(
            model.copy(id_str = null),
            model.copy(title = null),
            model.copy(userName = null),
            model.copy(importDateTime = null),
            model.copy(originalWidth = null),
            model.copy(originalHeight = null),
            model.copy(originalUrl = null),
            model.copy(originalSize = null),
            model.copy(previewWidth = null),
            model.copy(previewHeight = null),
            model.copy(previewUrl = null),
            model.copy(previewSize = null)
        )

        publisher.offer(invalidList)

        runBlocking {
            roomFavoriteManager.getGifsFlow().take(1).collect {
                assert(it == emptyList<GifModel>())
            }
        }
    }

    @Test
    fun addGif() {
        initEmptyRoomFavoriteManager()
        runBlocking {
            roomFavoriteManager.addGif(gifModelFromDbGif(dbGifsList1[0]))
            Mockito.verify(gifDao, times(1)).insertAll(dbGifsList1[0])
        }
    }

    @Test
    fun deleteGif() {
        initEmptyRoomFavoriteManager()
        runBlocking {
            roomFavoriteManager.delete(gifModelFromDbGif(dbGifsList1[0]))
            Mockito.verify(gifDao, times(1)).delete(dbGifsList1[0])
        }
    }
}