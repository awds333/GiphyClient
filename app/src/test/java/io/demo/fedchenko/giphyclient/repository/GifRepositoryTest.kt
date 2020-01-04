package io.demo.fedchenko.giphyclient.repository

import io.demo.fedchenko.giphyclient.model.*
import io.demo.fedchenko.giphyclient.retrofit.GiphyAPI
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType
import okhttp3.ResponseBody
import org.junit.Test
import org.mockito.Mockito
import retrofit2.Response
import java.lang.Exception
import java.lang.RuntimeException

class GifRepositoryTest {
    private var giphyAPI = Mockito.mock(GiphyAPI::class.java)
    private var gifRepository = GifRepository("", giphyAPI)

    private val gifNonParsedModel1 = GifNotParsedModel(
        Images(
            GifInfo(300, 200, "ourl1", 100),
            GifInfo(100, 70, "purl1", 30)
        ),
        User("name1"),
        "t1",
        "d1",
        "id1"
    )
    private val gifNonParsedModel2 = GifNotParsedModel(
        Images(
            GifInfo(320, 220, "ourl2", 120),
            GifInfo(120, 72, "purl2", 32)
        ),
        User("name2"),
        "t2",
        "d2",
        "id2"
    )
    private val gifNonParsedModel3 = GifNotParsedModel(
        Images(
            GifInfo(330, 230, "ourl3", 130),
            GifInfo(130, 73, "purl3", 33)
        ),
        User("name3"),
        "t3",
        "d3",
        "id3"
    )

    private val fromRow =
        gifRepository.javaClass.getDeclaredMethod("fromRaw", GifNotParsedModel::class.java).apply {
            isAccessible = true
        }

    private fun initGifRepository() {
        giphyAPI = Mockito.mock(GiphyAPI::class.java)
        gifRepository = GifRepository("", giphyAPI)
    }

    @Test
    fun fromRowNormal() {
        initGifRepository()

        val gifModel = fromRow.invoke(gifRepository, gifNonParsedModel1) as GifModel

        assert(
            gifModel == GifModel(
                "id1",
                GifProperties(300, 200, "ourl1", 100),
                GifProperties(100, 70, "purl1", 30),
                "name1",
                "t1",
                "d1",
                false
            )
        )
    }

    @Test
    fun fromRowInvalid() {
        initGifRepository()

        val gifModels = listOfNotNull(
            fromRow.invoke(gifRepository, gifNonParsedModel1.copy(images = null)), //fatal
            fromRow.invoke(
                gifRepository, gifNonParsedModel1.copy( //fatal
                    images = gifNonParsedModel1.images!!.copy(gifInfo = null)
                )
            ),
            fromRow.invoke(
                gifRepository,
                gifNonParsedModel1.copy(user = null)
            ), //non fatal (userName = "")
            fromRow.invoke(
                gifRepository, gifNonParsedModel1.copy( //non fatal (preview = original)
                    images = gifNonParsedModel1.images!!.copy(previewGifInfo = null)
                )
            )
        )
        val gifModel = fromRow.invoke(gifRepository, gifNonParsedModel1) as GifModel

        assert(
            gifModels == listOf(
                gifModel.copy(userName = ""),
                gifModel.copy(preview = gifModel.original)
            )
        )
    }

    @Test
    fun getTrendingNotEmpty() {
        initGifRepository()

        Mockito.`when`(giphyAPI.getTrendingGifs(10, 0, ""))
            .thenReturn(
                CompletableDeferred(
                    Response.success(
                        ResponseModel(
                            listOf(
                                gifNonParsedModel1,
                                gifNonParsedModel2,
                                gifNonParsedModel3
                            )
                        )
                    )
                )
            )

        runBlocking {
            val list = gifRepository.getTrendingGifs(10, 0)
            assert(
                list == listOf(
                    fromRow.invoke(gifRepository, gifNonParsedModel1),
                    fromRow.invoke(gifRepository, gifNonParsedModel2),
                    fromRow.invoke(gifRepository, gifNonParsedModel3)
                )
            )
        }
    }

    @Test
    fun getTrendingEmpty() {
        initGifRepository()

        Mockito.`when`(giphyAPI.getTrendingGifs(10, 0, ""))
            .thenReturn(
                CompletableDeferred(
                    Response.success(
                        ResponseModel(
                            emptyList()
                        )
                    )
                )
            )

        runBlocking {
            val list = gifRepository.getTrendingGifs(10, 0)
            assert(list == emptyList<GifModel>())
        }
    }

    @Test
    fun getTrendingInvalid() {
        initGifRepository()

        Mockito.`when`(giphyAPI.getTrendingGifs(10, 0, ""))
            .thenReturn(
                CompletableDeferred(
                    Response.success(
                        ResponseModel(
                            listOf(
                                gifNonParsedModel1.copy(images = null), //fatal
                                gifNonParsedModel1.copy( //fatal
                                    images = gifNonParsedModel1.images!!.copy(gifInfo = null)
                                ),
                                gifNonParsedModel1.copy(user = null), //non fatal (userName = "")
                                gifNonParsedModel1.copy( //non fatal (preview = original)
                                    images = gifNonParsedModel1.images!!.copy(previewGifInfo = null)
                                )
                            )
                        )
                    )
                )
            )

        runBlocking {
            val list = gifRepository.getTrendingGifs(10, 0)

            val gifModel = fromRow.invoke(gifRepository, gifNonParsedModel1) as GifModel

            assert(
                list == listOf(
                    gifModel.copy(userName = ""),
                    gifModel.copy(preview = gifModel.original)
                )
            )
        }
    }

    @Test
    fun getTrendingError() {
        initGifRepository()

        Mockito.`when`(giphyAPI.getTrendingGifs(10, 0, ""))
            .thenReturn(
                CompletableDeferred(
                    Response.error(
                        403, ResponseBody.create(
                            MediaType.parse("application/json"),
                            "{\"result\":[\"failed\"]}"
                        )
                    )
                )
            )

        runBlocking {
            try {
                gifRepository.getTrendingGifs(10, 0)
                assert(false)
            } catch (e: RuntimeException) {
            }
        }
    }

    @Test
    fun getByTermNotEmpty() {
        initGifRepository()

        Mockito.`when`(giphyAPI.findGifsByTerm("term", 10, 0, ""))
            .thenReturn(
                CompletableDeferred(
                    Response.success(
                        ResponseModel(
                            listOf(
                                gifNonParsedModel1,
                                gifNonParsedModel2,
                                gifNonParsedModel3
                            )
                        )
                    )
                )
            )

        runBlocking {
            val list = gifRepository.getByTerm("term", 10, 0)
            assert(
                list == listOf(
                    fromRow.invoke(gifRepository, gifNonParsedModel1),
                    fromRow.invoke(gifRepository, gifNonParsedModel2),
                    fromRow.invoke(gifRepository, gifNonParsedModel3)
                )
            )
        }
    }

    @Test
    fun getByTermEmpty() {
        initGifRepository()

        Mockito.`when`(giphyAPI.findGifsByTerm("term", 10, 0, ""))
            .thenReturn(
                CompletableDeferred(
                    Response.success(
                        ResponseModel(
                            emptyList()
                        )
                    )
                )
            )

        runBlocking {
            val list = gifRepository.getByTerm("term", 10, 0)
            assert(
                list == emptyList<GifModel>()
            )
        }
    }

    @Test
    fun getByTermError() {
        initGifRepository()

        Mockito.`when`(giphyAPI.findGifsByTerm("term", 10, 0, ""))
            .thenReturn(
                CompletableDeferred(
                    Response.error(
                        403, ResponseBody.create(
                            MediaType.parse("application/json"),
                            "{\"result\":[\"failed\"]}"
                        )
                    )
                )
            )

        runBlocking {
            try {
                gifRepository.getByTerm("term", 10, 0)
                assert(false)
            } catch (e: RuntimeException) {
            }
        }
    }
}