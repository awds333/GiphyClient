package io.demo.fedchenko.giphyclient.repository.loader

import io.demo.fedchenko.giphyclient.model.GifModel
import io.demo.fedchenko.giphyclient.model.GifProperties
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.io.IOException

class GifLoaderTest {
    private val gifModel1 = GifModel(
        "i1",
        GifProperties(200, 100, "u1", 232),
        GifProperties(150, 70, "pu1", 123),
        "n1",
        "t1",
        "d1"
    )
    private val gifModel2 = GifModel(
        "i2",
        GifProperties(220, 120, "u2", 248),
        GifProperties(190, 90, "pu2", 153),
        "n2",
        "t2",
        "d2"
    )

    @Test
    fun loadMoreSuccess() {
        val loader = object : GifLoader() {
            override fun buildRequest(offset: Int): suspend () -> List<GifModel>? {
                return { listOf(gifModel1, gifModel2) }
            }
        }
        runBlocking {
            val result = loader.loadMoreGifs()
            assert(result == listOf(gifModel1, gifModel2))
        }
    }

    @Test
    fun loadMoreError() {
        val loader = object : GifLoader() {
            override fun buildRequest(offset: Int): suspend () -> List<GifModel>? {
                throw IOException()
            }
        }
        runBlocking {
            try {
                loader.loadMoreGifs()
                assert(false)
            } catch (e: IOException) {
            }
        }
    }
}