package io.demo.fedchenko.giphyclient.view

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.net.ConnectivityManager.CONNECTIVITY_ACTION
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.drawToBitmap
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import io.demo.fedchenko.giphyclient.adapter.GifListAdapter
import io.demo.fedchenko.giphyclient.databinding.ActivityMainBinding
import io.demo.fedchenko.giphyclient.receiver.NetworkStateBroadcastReceiver
import io.demo.fedchenko.giphyclient.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.gif_image_view.view.*
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf


class MainActivity : AppCompatActivity() {

    private val mainViewModel: MainViewModel by viewModel {
        parametersOf(
            getSharedPreferences(
                getString(io.demo.fedchenko.giphyclient.R.string.app_name),
                Context.MODE_PRIVATE
            )
        )
    }
    private lateinit var adapter: GifListAdapter
    private lateinit var layoutManager: StaggeredGridLayoutManager

    private lateinit var binding: ActivityMainBinding

    private var lastEmpty = false

    private val noConnectionDialog = NoConnectionDialog().apply {
        isCancelable = false
    }

    private var intentFilter = IntentFilter(CONNECTIVITY_ACTION)
    private val receiver = NetworkStateBroadcastReceiver(onConnected = {
        if (noConnectionDialog.isResumed)
            noConnectionDialog.dismiss()
    }, onDisconnected = {
        if (!noConnectionDialog.isResumed)
            noConnectionDialog.show(supportFragmentManager, "")
    })

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
            val bitmap =
                if (view.isLaidOut && view.circlePogressBar.visibility == View.GONE) view.drawToBitmap() else {
                    val bitmap = Bitmap.createBitmap(
                        model.preview.width,
                        model.preview.height,
                        Bitmap.Config.ARGB_8888
                    )
                    val canvas = Canvas(bitmap)
                    val paint = Paint()
                    paint.color = Color.GRAY
                    canvas.drawRect(
                        0f, 0f,
                        model.preview.width.toFloat(), model.preview.height.toFloat(), paint
                    )
                    bitmap
                }

            val activityOptionsCompat =
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                    this@MainActivity, view, "gifImageView"
                )


            binding.isActivityActive = false
            GifViewActivity.start(
                model,
                bitmap,
                this@MainActivity,
                activityOptionsCompat.toBundle()
            )
        }

        recycler.adapter = adapter
        recycler.post {
            mainViewModel.observeGifModels(this, adapter.gifModelsObserver)
            recycler.scrollToPosition(scrollPosition)
        }

        registerReceiver(receiver, intentFilter)

        mainViewModel.observeGifModels(this,
            Observer { t ->
                if (t?.isEmpty() != false) {
                    lastEmpty = true
                } else {
                    if (lastEmpty) {
                        recycler.post {
                            recycler.scrollToPosition(0)
                            recycler.scrollBy(0, Int.MIN_VALUE)
                        }
                        lastEmpty = false
                    }
                }
            })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == GifViewActivity.REQUEST_CODE) {
            binding.isActivityActive = true
        }
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

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }
}
