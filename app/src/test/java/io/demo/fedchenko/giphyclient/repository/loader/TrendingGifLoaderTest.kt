package io.demo.fedchenko.giphyclient.repository.loader

import io.demo.fedchenko.giphyclient.model.GifModel
import io.demo.fedchenko.giphyclient.repository.TrendingGifProvider
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.mockito.Mockito
import org.mockito.internal.verification.VerificationModeFactory

class TrendingGifLoaderTest {
    @Test
    fun buildRequestDefault() {
        val provider = Mockito.mock(TrendingGifProvider::class.java)
        val loader = TrendingGifLoader(provider, 25)
        val buildRequest =
            loader.javaClass.getDeclaredMethod("buildRequest", Int::class.java).apply {
                isAccessible = true
            }
        val result = buildRequest.invoke(loader, 4) as suspend () -> List<GifModel>
        runBlocking {
            result()

            Mockito.verify(provider, VerificationModeFactory.times(1)).getTrendingGifs(25, 4)
        }
    }
}