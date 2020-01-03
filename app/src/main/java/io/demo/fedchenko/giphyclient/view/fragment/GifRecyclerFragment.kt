package io.demo.fedchenko.giphyclient.view.fragment

import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.drawToBitmap
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import io.demo.fedchenko.giphyclient.adapter.GifListAdapter
import io.demo.fedchenko.giphyclient.model.GifModel
import io.demo.fedchenko.giphyclient.view.activity.GifViewActivity
import io.demo.fedchenko.giphyclient.viewmodel.FavoriteViewModel
import io.demo.fedchenko.giphyclient.viewmodel.GifObservable
import kotlinx.android.synthetic.main.gif_image_view.view.*
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

abstract class GifRecyclerFragment : Fragment() {

    private val favoriteViewModel: FavoriteViewModel by viewModel { parametersOf(context!!.applicationContext) }

    private lateinit var layoutManager: StaggeredGridLayoutManager
    private lateinit var adapter: GifListAdapter

    private var scrollPosition = 0

    abstract override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?

    protected abstract fun getRecyclerView(): RecyclerView

    protected open fun getGifObservable(): GifObservable = favoriteViewModel

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val spanCount =
            if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) 2 else 3
        layoutManager = StaggeredGridLayoutManager(spanCount, LinearLayoutManager.VERTICAL)

        adapter = GifListAdapter(spanCount)
        adapter.setOnItemClickListener { view, model ->
            val bitmap = getBitmap(view, model)

            val activityOptionsCompat =
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                    activity!!, view, "gifImageView"
                )

            GifViewActivity.start(model, bitmap, activity!!, activityOptionsCompat.toBundle())
        }
        adapter.setOnItemFavoriteClickListener { _, gifModel ->
            favoriteViewModel.changeFavorite(gifModel)
        }

        savedInstanceState?.also {
            scrollPosition = it.getInt("first_visible", 0)
        }

        val recycler = getRecyclerView()

        recycler.layoutManager = layoutManager

        recycler.adapter = adapter

        recycler.post {
            getGifObservable().observeGifModels(this, adapter.gifModelsObserver)
            recycler.scrollToPosition(scrollPosition)
        }
    }

    private fun getBitmap(view: View, model: GifModel): Bitmap =
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
                0f,
                0f,
                model.preview.width.toFloat(),
                model.preview.height.toFloat(), paint
            )
            bitmap
        }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.apply {
            val firstVisible = layoutManager.findFirstVisibleItemPositions(IntArray(3))
            if (firstVisible.isNotEmpty())
                this.putInt("first_visible", firstVisible[0])
        }
        super.onSaveInstanceState(outState)
    }
}