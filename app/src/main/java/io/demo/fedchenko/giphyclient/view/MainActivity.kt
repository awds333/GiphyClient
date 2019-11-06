package io.demo.fedchenko.giphyclient.view

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.gson.Gson
import io.demo.fedchenko.giphyclient.adapter.GifListAdapter
import io.demo.fedchenko.giphyclient.databinding.ActivityMainBinding
import io.demo.fedchenko.giphyclient.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.viewmodel.ext.android.viewModel


class MainActivity : AppCompatActivity() {

    private val mainViewModel: MainViewModel by viewModel()
    private lateinit var adapter: GifListAdapter
    private lateinit var layoutManager: StaggeredGridLayoutManager

    private lateinit var binding: ActivityMainBinding

    private val exceptionListener = {
        Toast.makeText(
            this@MainActivity,
            io.demo.fedchenko.giphyclient.R.string.request_failed,
            Toast.LENGTH_LONG
        ).show()
    }
    private val keyboardListener: () -> Unit = {
        val view = currentFocus
        view?.clearFocus()
        (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
            view?.windowToken,
            0
        )
    }

    private var scrollPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            DataBindingUtil.setContentView(
                this,
                io.demo.fedchenko.giphyclient.R.layout.activity_main
            )
        binding.lifecycleOwner = this
        binding.mainViewModel = mainViewModel
        binding.isActivityActive = true

        val spanCount =
            if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) 2 else 3
        layoutManager = StaggeredGridLayoutManager(
            spanCount,
            LinearLayoutManager.VERTICAL
        )
        recycler.layoutManager = layoutManager

        adapter = GifListAdapter(spanCount)
        adapter.setOnItemClickListener { view, model ->
            val activityOptionsCompat =
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                    this@MainActivity, view, "t"
                )
            val intent = Intent(this@MainActivity, GifViewActivity::class.java)
                .putExtra(GifViewActivity.MODEL, Gson().toJson(model).toString())

            binding.isActivityActive = false

            startActivityForResult(intent, 0, activityOptionsCompat.toBundle())
        }

        recycler.adapter = adapter
        recycler.post {
            mainViewModel.observeGifModels(this, adapter.gifModelsObserver)
            recycler.scrollToPosition(scrollPosition)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 0)
            binding.isActivityActive = true
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        savedInstanceState?.also {
            binding.isActivityActive = it.getBoolean("isActivityActive", true)
            scrollPosition = it.getInt("first_visible", 0)
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.apply {
            val firstVisible = layoutManager.findFirstVisibleItemPositions(IntArray(3))
            if (firstVisible.isNotEmpty())
                this.putInt("first_visible", firstVisible[0])
            this.putBoolean("isActivityActive", binding.isActivityActive ?: true)
        }
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
