package io.demo.fedchenko.giphyclient.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.Observer
import io.demo.fedchenko.giphyclient.model.GifModel
import io.demo.fedchenko.giphyclient.model.GifProperties
import io.demo.fedchenko.giphyclient.repository.FavoriteManager
import io.demo.fedchenko.giphyclient.repository.GifProvider
import io.demo.fedchenko.giphyclient.repository.TermsManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.withTimeout
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import java.io.IOException

class MainViewModelTest {
    @get:Rule
    val rule = InstantTaskExecutorRule()

    private lateinit var favoriteManager: FavoriteManager
    private lateinit var termsManager: TermsManager
    private lateinit var gifProvider: GifProvider
    private lateinit var gifPublisher: ConflatedBroadcastChannel<List<GifModel>>
    private lateinit var termPublisher: ConflatedBroadcastChannel<List<String>>
    private lateinit var viewModel: MainViewModel

    private val gifModel1 = GifModel(
        "i1",
        GifProperties(200, 100, "u1", 232),
        GifProperties(150, 70, "pu1", 123),
        "n1",
        "t1",
        "d1",
        false
    )
    private val gifModel2 = GifModel(
        "i2",
        GifProperties(220, 120, "u2", 248),
        GifProperties(190, 90, "pu2", 153),
        "n2",
        "t2",
        "d2",
        false
    )

    private fun createLifecycleOwner(): LifecycleOwner {
        val lifecycleOwner = Mockito.mock(LifecycleOwner::class.java)
        val lifecycle = LifecycleRegistry(lifecycleOwner)
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        Mockito.`when`(lifecycleOwner.lifecycle).then { lifecycle }
        return lifecycleOwner
    }

    @Before
    fun init() {
        favoriteManager = Mockito.mock(FavoriteManager::class.java)
        gifPublisher = ConflatedBroadcastChannel(emptyList())
        Mockito.`when`(favoriteManager.getGifsFlow()).then { gifPublisher.asFlow() }

        termsManager = Mockito.mock(TermsManager::class.java)
        termPublisher = ConflatedBroadcastChannel(emptyList())
        Mockito.`when`(termsManager.getTerms()).then { termPublisher.asFlow() }

        Dispatchers.setMain(Dispatchers.Default)

        gifProvider = Mockito.mock(GifProvider::class.java)
    }

    @Test
    fun observeGifModels() {
        runBlocking {
            Mockito.`when`(gifProvider.getTrendingGifs(50, 0))
                .then { listOf(gifModel1) }
        }
        viewModel = MainViewModel(gifProvider, termsManager, favoriteManager)
        val mutex = Mutex(true)
        var step = 0

        val lifecycleOwner = createLifecycleOwner()
        viewModel.observeGifModels(lifecycleOwner, Observer {
            assert(it == listOf(gifModel1))
            mutex.unlock()
            step++
        })
        mutex.lockLaunch()
        assert(step == 1)
    }

    @Test
    fun search() {
        runBlocking {
            Mockito.`when`(gifProvider.getTrendingGifs(50, 0))
                .then { listOf(gifModel1) }
            Mockito.`when`(gifProvider.getByTerm("term", 50, 0))
                .then { listOf(gifModel1, gifModel2) }
        }
        viewModel = MainViewModel(gifProvider, termsManager, favoriteManager)

        val lifecycleOwner = createLifecycleOwner()
        var step = 0
        val mutex = Mutex(true)

        viewModel.observeGifModels(lifecycleOwner, Observer {
            when (step) {
                0 -> {
                    assert(it == listOf(gifModel1))
                    step++
                }
                1 -> {
                    assert(it == emptyList<GifModel>())
                    step++
                }
                2 -> {
                    assert(it == listOf(gifModel1, gifModel2))
                    step++
                    mutex.unlock()
                }
            }
        })
        viewModel.searchText.value = "term"
        viewModel.search()

        mutex.lockLaunch()
        assert(step == 3)
    }

    @Test
    fun exceptionListenerRegisterRemove() {
        runBlocking {
            Mockito.`when`(gifProvider.getByTerm("term", 50, 0))
                .then { throw IOException() }
        }
        viewModel = MainViewModel(gifProvider, termsManager, favoriteManager)

        var step = 0
        var mutex = Mutex(true)

        val exceptionListener: () -> Unit = {
            step++
            mutex.unlock()
        }
        viewModel.registerExceptionsListener(exceptionListener)
        viewModel.searchText.value = "term"
        viewModel.search()

        mutex.lockLaunch()

        viewModel.removeExceptionListener()
        viewModel.refresh()

        assert(step == 1)
    }

    @Test
    fun scrollTrending() {
        runBlocking {
            Mockito.`when`(gifProvider.getTrendingGifs(50, 0))
                .then {
                    return@then listOf(gifModel1, gifModel2)
                }
            Mockito.`when`(gifProvider.getTrendingGifs(50, 2))
                .then {
                    return@then listOf(gifModel2, gifModel1)
                }
            Mockito.`when`(gifProvider.getTrendingGifs(50, 4))
                .then {
                    return@then listOf(gifModel1)
                }
        }
        viewModel = MainViewModel(gifProvider, termsManager, favoriteManager)

        var step = 0
        val mutex = Mutex(true)

        viewModel.observeGifModels(createLifecycleOwner(), Observer {
            when (step) {
                0 -> {
                    assert(it == listOf(gifModel1, gifModel2))
                    step++
                }
                1 -> {
                    assert(it == listOf(gifModel1, gifModel2, gifModel2, gifModel1))
                    step++
                    mutex.unlock()
                }
                2 -> {
                    assert(it == listOf(gifModel1, gifModel2, gifModel2, gifModel1, gifModel1))
                    step++
                    mutex.unlock()
                }
            }
        })
        viewModel.onScroll(1)

        mutex.lockLaunch()
        assert(step == 2)

        viewModel.onScroll(2)

        mutex.lockLaunch()
        assert(step == 3)
    }

    @Test
    fun refreshTrending() {
        runBlocking {
            Mockito.`when`(gifProvider.getTrendingGifs(50, 0))
                .then {
                    return@then listOf(gifModel1, gifModel2)
                }
        }
        viewModel = MainViewModel(gifProvider, termsManager, favoriteManager)

        var step = 0
        val mutex = Mutex(true)

        viewModel.observeGifModels(createLifecycleOwner(), Observer {
            when (step) {
                0 -> {
                    assert(it == listOf(gifModel1, gifModel2))
                    step++
                    mutex.unlock()
                }
                1 -> {
                    assert(it == emptyList<GifModel>())
                    step++
                }
                2 -> {
                    assert(it == listOf(gifModel1, gifModel2))
                    step++
                    mutex.unlock()
                }
            }
        })


        mutex.lockLaunch()
        viewModel.refresh()

        mutex.lockLaunch()
        assert(step == 3)
    }

    @Test
    fun cleanSearch() {
        runBlocking {
            Mockito.`when`(gifProvider.getTrendingGifs(50, 0))
                .then {
                    return@then listOf(gifModel1, gifModel2)
                }
            Mockito.`when`(gifProvider.getByTerm("term", 50, 0))
                .then {
                    return@then listOf(gifModel2)
                }
        }
        viewModel = MainViewModel(gifProvider, termsManager, favoriteManager)

        var step = 0
        val mutex = Mutex(true)

        viewModel.observeGifModels(createLifecycleOwner(), Observer {
            when (step) {
                0 -> {
                    assert(it == listOf(gifModel1, gifModel2))
                    step++
                }
                1 -> {
                    assert(it == emptyList<GifModel>())
                    step++
                }
                2 -> {
                    step++
                    assert(it == listOf(gifModel2))
                    mutex.unlock()
                }
                3 -> {
                    assert(it == emptyList<GifModel>())
                    step++
                }
                4 -> {
                    assert(it == listOf(gifModel1, gifModel2))
                    step++
                    mutex.unlock()
                }
            }
        })
        viewModel.searchText.value = "term"
        viewModel.search()

        mutex.lockLaunch()
        viewModel.clean()
        assert(viewModel.searchText.value!!.isEmpty())

        mutex.lockLaunch()
        assert(step == 5)
    }

    @Test
    fun keyboardListener() {
        runBlocking {
            Mockito.`when`(gifProvider.getTrendingGifs(50, 0))
                .then {
                    return@then emptyList<GifModel>()
                }
            Mockito.`when`(gifProvider.getByTerm("term", 50, 0))
                .then {
                    return@then emptyList<GifModel>()
                }
        }
        viewModel = MainViewModel(gifProvider, termsManager, favoriteManager)

        var step = 0
        val listener: () -> Unit = { step++ }

        viewModel.registerKeyboardListener(listener)
        viewModel.searchText.value = "term"

        viewModel.search()

        viewModel.removeKeyboardListener()

        viewModel.search()

        assert(step == 1)
    }

    private fun Mutex.lockLaunch(timeOut: Long = 1000L) {
        runBlocking {
            withTimeout(timeOut) {
                lock()
            }
        }
    }
}