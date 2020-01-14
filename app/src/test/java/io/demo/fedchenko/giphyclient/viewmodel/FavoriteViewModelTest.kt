package io.demo.fedchenko.giphyclient.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.Observer
import io.demo.fedchenko.giphyclient.model.GifModel
import io.demo.fedchenko.giphyclient.model.GifProperties
import io.demo.fedchenko.giphyclient.repository.FavoriteManager
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.internal.verification.VerificationModeFactory.times
import java.io.IOException

class FavoriteViewModelTest {
    @get:Rule
    val rule = InstantTaskExecutorRule()

    private lateinit var favoriteManager: FavoriteManager
    private lateinit var publisher: ConflatedBroadcastChannel<List<GifModel>>
    private lateinit var viewModel: FavoriteViewModel

    private val gifModel1 = GifModel(
        "i1",
        GifProperties(200, 100, "u1", 232),
        GifProperties(150, 70, "pu1", 123),
        "n1",
        "t1",
        "d1",
        true
    )
    private val gifModel2 = GifModel(
        "i2",
        GifProperties(220, 120, "u2", 248),
        GifProperties(190, 90, "pu2", 153),
        "n2",
        "t2",
        "d2",
        true
    )

    @Before
    fun init() {
        favoriteManager = mock(FavoriteManager::class.java)
        publisher = ConflatedBroadcastChannel(emptyList())
        Mockito.`when`(favoriteManager.getGifsFlow()).then { publisher.asFlow() }
        Dispatchers.setMain(Dispatchers.Default)
        viewModel = FavoriteViewModel(favoriteManager)
    }

    @Test
    fun changeFavorite() {
        runBlocking {
            viewModel.changeFavorite(gifModel1)
            Mockito.verify(favoriteManager, times(1)).addGif(gifModel1)
            publisher.offer(listOf(gifModel1))
            delay(50)
            viewModel.changeFavorite(gifModel1)
            Mockito.verify(favoriteManager, times(1)).delete(gifModel1)
        }
    }

    @Test
    fun changeFavoriteError() {
        runBlocking {
            Mockito.`when`(favoriteManager.addGif(gifModel1)).then { throw IOException() }
            viewModel.changeFavorite(gifModel1)
            Mockito.verify(favoriteManager, times(1)).addGif(gifModel1)
        }
    }

    @Test
    fun observeGifModels() {
        var list = emptyList<GifModel>()
        var step = 0
        val mutex = Mutex(true)

        val observer = Observer<List<GifModel>> {
            assert(list == it)
            step++
            mutex.unlock()
        }
        val lifecycleOwner = mock(LifecycleOwner::class.java)
        val lifecycle = LifecycleRegistry(lifecycleOwner)
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        Mockito.`when`(lifecycleOwner.lifecycle).then { lifecycle }

        viewModel.observeGifModels(lifecycleOwner, observer)

        list = listOf(gifModel1, gifModel2)

        publisher.offer(list)
        runBlocking {
            withTimeout(1000L) {
                mutex.lock()
            }
        }
        assert(step == 2)
    }
}