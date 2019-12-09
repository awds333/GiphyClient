package io.demo.fedchenko.giphyclient.view

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import io.demo.fedchenko.giphyclient.R
import io.demo.fedchenko.giphyclient.adapter.GifListAdapter
import io.demo.fedchenko.giphyclient.databinding.FavoriteFragmentBinding
import kotlinx.android.synthetic.main.favorite_fragment.*

class FavoriteFragment : Fragment() {

    private lateinit var binding: FavoriteFragmentBinding
    private lateinit var layoutManager: StaggeredGridLayoutManager
    private lateinit var adapter: GifListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.favorite_fragment, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val spanCount =
            if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) 2 else 3
        layoutManager = StaggeredGridLayoutManager(
            spanCount,
            LinearLayoutManager.VERTICAL
        )
        favoriteRecycler.layoutManager = layoutManager

        adapter = GifListAdapter(spanCount)
    }
}