package io.demo.fedchenko.giphyclient.view.fregment

import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.drawToBitmap
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import io.demo.fedchenko.giphyclient.ConnectivityLiveData
import io.demo.fedchenko.giphyclient.R
import io.demo.fedchenko.giphyclient.adapter.GifListAdapter
import io.demo.fedchenko.giphyclient.databinding.SearchFragmentBinding
import io.demo.fedchenko.giphyclient.view.activity.GifViewActivity
import io.demo.fedchenko.giphyclient.view.dialog.NoConnectionDialog
import io.demo.fedchenko.giphyclient.viewmodel.FavoriteViewModel
import io.demo.fedchenko.giphyclient.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.gif_image_view.view.*
import kotlinx.android.synthetic.main.search_fragment.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class SearchFragment : Fragment() {

    private val favoriteViewModel: FavoriteViewModel by viewModel { parametersOf(context!!.applicationContext) }
    private val searchViewModel: MainViewModel by viewModel {
        parametersOf(
            activity!!.getSharedPreferences(
                getString(R.string.app_name),
                Context.MODE_PRIVATE
            ),
            context!!.applicationContext
        )
    }
    private lateinit var adapter: GifListAdapter
    private lateinit var layoutManager: StaggeredGridLayoutManager

    private lateinit var binding: SearchFragmentBinding

    private var lastEmpty = false

    private val noConnectionDialog = NoConnectionDialog()
        .apply {
            isCancelable = false
        }

    private val connectivityLiveData: LiveData<Boolean> by inject<ConnectivityLiveData> {
        parametersOf(
            activity!!.application
        )
    }

    private val exceptionListener = {
        Toast.makeText(
            context,
            R.string.request_failed,
            Toast.LENGTH_LONG
        ).show()
    }
    private val keyboardListener: () -> Unit = {
        val view = activity?.currentFocus
        view?.clearFocus()
        (context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
            view?.windowToken,
            0
        )
    }

    private var scrollPosition = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.search_fragment, container, false)
        return binding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        binding.lifecycleOwner = this
        binding.searchViewModel = searchViewModel

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
                if (view.isLaidOut && view.circlePogressBar.visibility == View.GONE)
                    view.drawToBitmap()
                else {
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
                    activity!!, view, "gifImageView"
                )

            GifViewActivity.start(
                model,
                bitmap,
                activity!!,
                activityOptionsCompat.toBundle()
            )
        }
        adapter.setOnItemFavoriteClickListener { _, gifModel ->
            favoriteViewModel.changeFavorite(gifModel)
        }

        recycler.adapter = adapter
        recycler.post {
            searchViewModel.observeGifModels(this, adapter.gifModelsObserver)
            recycler.scrollToPosition(scrollPosition)
        }

        connectivityLiveData.observe(this, Observer {
            if (it) {
                if (noConnectionDialog.isResumed)
                    noConnectionDialog.dismiss()
            } else {
                if (!noConnectionDialog.isResumed)
                    noConnectionDialog.show(fragmentManager, "")
            }
        })

        searchViewModel.observeGifModels(this,
            Observer { t ->
                if (t.isNullOrEmpty())
                    lastEmpty = true
                else {
                    if (lastEmpty) {
                        recycler.post {
                            recycler.scrollToPosition(0)
                            recycler.scrollBy(0, Int.MIN_VALUE)
                        }
                        lastEmpty = false
                    }
                }
            })

        super.onActivityCreated(savedInstanceState)
        savedInstanceState?.also {
            scrollPosition = it.getInt("first_visible", 0)
        }
    }


    override fun onSaveInstanceState(outState: Bundle) {
        outState.apply {
            val firstVisible = layoutManager.findFirstVisibleItemPositions(IntArray(3))
            if (firstVisible.isNotEmpty())
                this.putInt("first_visible", firstVisible[0])
        }
        super.onSaveInstanceState(outState)
    }

    override fun onResume() {
        super.onResume()
        searchViewModel.registerExceptionsListener(exceptionListener)
        searchViewModel.registerKeyboardListener(keyboardListener)
    }

    override fun onPause() {
        super.onPause()
        searchViewModel.removeExceptionListener()
        searchViewModel.removeKeyboardListener()
    }
}