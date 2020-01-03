package io.demo.fedchenko.giphyclient.view.fregment

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
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import io.demo.fedchenko.giphyclient.R
import io.demo.fedchenko.giphyclient.adapter.GifListAdapter
import io.demo.fedchenko.giphyclient.databinding.FavoriteFragmentBinding
import io.demo.fedchenko.giphyclient.view.activity.GifViewActivity
import io.demo.fedchenko.giphyclient.viewmodel.FavoriteViewModel
import kotlinx.android.synthetic.main.favorite_fragment.*
import kotlinx.android.synthetic.main.gif_image_view.view.*
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class FavoriteFragment : GifRecyclerFragment() {

    private lateinit var binding: FavoriteFragmentBinding

    override fun bind() {
        favoriteRecycler.layoutManager = layoutManager

        favoriteRecycler.adapter = adapter

        favoriteRecycler.post {
            favoriteViewModel.observeGifModels(this, adapter.gifModelsObserver)
            favoriteRecycler.scrollToPosition(scrollPosition)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.favorite_fragment, container, false)
        return binding.root
    }
}