package io.demo.fedchenko.giphyclient.view

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import io.demo.fedchenko.giphyclient.R
import io.demo.fedchenko.giphyclient.adapter.GifListAdapter
import io.demo.fedchenko.giphyclient.databinding.ActivityMainBinding
import io.demo.fedchenko.giphyclient.model.GifModel
import io.demo.fedchenko.giphyclient.viewmodel.ExceptionListener
import io.demo.fedchenko.giphyclient.viewmodel.KeyboardListener
import io.demo.fedchenko.giphyclient.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject


class MainActivity : AppCompatActivity() {

    private val mainViewModel: MainViewModel by inject()
    private lateinit var adapter: GifListAdapter
    private lateinit var layoutManager: StaggeredGridLayoutManager
    private val exceptionListener = object : ExceptionListener {
        override fun handleException() {
            Toast.makeText(
                this@MainActivity,
                R.string.request_failed,
                Toast.LENGTH_LONG
            ).show()
        }
    }
    private val keyboardListener = object : KeyboardListener {
        override fun hideKeyboard() {
            val view = currentFocus
            view?.clearFocus()
            (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
                view?.windowToken,
                0
            )
        }
    }

    private var scrollPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityMainBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        binding.mainViewModel = mainViewModel

        val spanCount =
            if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) 2 else 3
        layoutManager = StaggeredGridLayoutManager(
            spanCount,
            LinearLayoutManager.VERTICAL
        )
        recycler.layoutManager = layoutManager

        adapter = GifListAdapter(this, spanCount)
        adapter.setOnItemClickListener(object : GifListAdapter.GifOnItemClickListener {
            override fun onItemClick(item: GifModel) {
                val dialog = GifDialogFragment.create(item)
                dialog.show(supportFragmentManager, "dialog")
            }
        })

        recycler.adapter = adapter
        recycler.post {
            mainViewModel.observeGifModels(this, adapter.gifModelsObserver)
            recycler.scrollToPosition(scrollPosition)
        }

        recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val count = layoutManager.itemCount - 1
                val lastVisible = layoutManager.findLastVisibleItemPositions(IntArray(3))
                lastVisible.forEach {
                    if (it == count)
                        mainViewModel.getMoreGifs()
                }
            }
        })
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        scrollPosition = savedInstanceState!!.getInt("first_visible", 0)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        val firstVisible = layoutManager.findFirstVisibleItemPositions(IntArray(3))
        if (firstVisible.isNotEmpty())
            outState!!.putInt("first_visible", firstVisible[0])
        super.onSaveInstanceState(outState)
    }

    override fun onResume() {
        super.onResume()
        mainViewModel.registerExceptionsListener(exceptionListener)
        mainViewModel.registerKeyboardListener(keyboardListener)
    }

    override fun onPause() {
        super.onPause()
        mainViewModel.removeExceptionListener()
        mainViewModel.removeKeyboardListener()
    }
}
