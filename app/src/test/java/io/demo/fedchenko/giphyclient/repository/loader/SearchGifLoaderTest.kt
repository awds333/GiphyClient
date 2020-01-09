package io.demo.fedchenko.giphyclient.repository.loader

import io.demo.fedchenko.giphyclient.model.GifModel
import io.demo.fedchenko.giphyclient.repository.SearchGifProvider
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.never
import org.mockito.internal.verification.VerificationModeFactory.times

class SearchGifLoaderTest {
    @Test
    fun buildRequestDefault() {
        val provider = Mockito.mock(SearchGifProvider::class.java)
        val loader = SearchGifLoader(provider, "term", 25)
        val buildRequest =
            loader.javaClass.getDeclaredMethod("buildRequest", Int::class.java).apply {
                isAccessible = true
            }
        val result = buildRequest.invoke(loader, 4) as suspend () -> List<GifModel>
        runBlocking {
            Mockito.verify(provider, never()).getByTerm("term", 25, 4)

            result()

            Mockito.verify(provider, times(1)).getByTerm("term", 25, 4)
        }
    }
}