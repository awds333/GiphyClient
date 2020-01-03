package io.demo.fedchenko.giphyclient.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import io.demo.fedchenko.giphyclient.R
import io.demo.fedchenko.giphyclient.databinding.FavoriteFragmentBinding
import kotlinx.android.synthetic.main.favorite_fragment.*

class FavoriteFragment : GifRecyclerFragment() {

    private lateinit var binding: FavoriteFragmentBinding

    override fun getRecyclerView(): RecyclerView = favoriteRecycler

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.favorite_fragment, container, false)
        return binding.root
    }
}