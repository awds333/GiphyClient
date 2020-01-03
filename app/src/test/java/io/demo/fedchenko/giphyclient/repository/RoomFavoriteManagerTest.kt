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

class RoomFavoriteManagerTest {
    private var gifDao = Mockito.mock(GifDao::class.java)
    private var publisher = ConflatedBroadcastChannel(emptyList<DbGif>())
    private var roomFavoriteManager = RoomFavoriteManager(gifDao)

    private fun initRoomFavoriteManager() {
        gifDao = Mockito.mock(GifDao::class.java)
        publisher = ConflatedBroadcastChannel(emptyList())
        Mockito.`when`(gifDao.getAll()).thenReturn(publisher.asFlow())
        roomFavoriteManager = RoomFavoriteManager(gifDao)
    }

    @Test
    fun getNotEmptyFavoriteGifs() {
        initRoomFavoriteManager()

        val dbGifsList1 = listOf(
            DbGif(
                10, "10", "title1", "u1", "t1", 20, 30,
                "org1", 100, 15, 25, "prev1", 85
            ),
            DbGif(
                13, "13", "title2", "u2", "t2", 22, 32,
                "org2", 120, 17, 27, "prev2", 95
            )
        )
        val dbGifsList2 = listOf(
            DbGif(
                14, "14", "title3", "u3", "t3", 23, 33,
                "org3", 130, 18, 28, "prev3", 78
            )
        )
        publisher.offer(dbGifsList1)

        runBlocking {
            roomFavoriteManager.getGifsFlow().take(1).collect {
                assert(
                    it == listOf(
                        GifModel(
                            dbGifsList1[0].id_str!!,
                            GifProperties(
                                dbGifsList1[0].originalWidth!!,
                                dbGifsList1[0].originalHeight!!,
                                dbGifsList1[0].originalUrl!!,
                                dbGifsList1[0].originalSize!!
                            ),
                            GifProperties(
                                dbGifsList1[0].previewWidth!!,
                                dbGifsList1[0].previewHeight!!,
                                dbGifsList1[0].previewUrl!!,
                                dbGifsList1[0].previewSize!!
                            ),
                            dbGifsList1[0].userName!!,
                            dbGifsList1[0].title!!,
                            dbGifsList1[0].importDateTime!!,
                            true
                        ),
                        GifModel(
                            dbGifsList1[1].id_str!!,
                            GifProperties(
                                dbGifsList1[1].originalWidth!!,
                                dbGifsList1[1].originalHeight!!,
                                dbGifsList1[1].originalUrl!!,
                                dbGifsList1[1].originalSize!!
                            ),
                            GifProperties(
                                dbGifsList1[1].previewWidth!!,
                                dbGifsList1[1].previewHeight!!,
                                dbGifsList1[1].previewUrl!!,
                                dbGifsList1[1].previewSize!!
                            ),
                            dbGifsList1[1].userName!!,
                            dbGifsList1[1].title!!,
                            dbGifsList1[1].importDateTime!!,
                            true
                        )
                    )
                )
            }
            publisher.offer(dbGifsList2)
            roomFavoriteManager.getGifsFlow().take(1).collect {
                assert(
                    it == listOf(
                        GifModel(
                            dbGifsList2[0].id_str!!,
                            GifProperties(
                                dbGifsList2[0].originalWidth!!,
                                dbGifsList2[0].originalHeight!!,
                                dbGifsList2[0].originalUrl!!,
                                dbGifsList2[0].originalSize!!
                            ),
                            GifProperties(
                                dbGifsList2[0].previewWidth!!,
                                dbGifsList2[0].previewHeight!!,
                                dbGifsList2[0].previewUrl!!,
                                dbGifsList2[0].previewSize!!
                            ),
                            dbGifsList2[0].userName!!,
                            dbGifsList2[0].title!!,
                            dbGifsList2[0].importDateTime!!,
                            true
                        )
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
            roomFavoriteManager.getGifsFlow().take(1).collect{
                assert(it == emptyList<GifModel>())
            }
        }
    }


}