package io.demo.fedchenko.gyphyclient.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import io.demo.fedchenko.gyphyclient.R
import io.demo.fedchenko.gyphyclient.model.GifModel

class GifListAdapter(var context: Context) : RecyclerView.Adapter<GifViewHolder>() {
    var gifModels: MutableLiveData<MutableList<GifModel>> = MutableLiveData()

    init {
        gifModels.value = emptyList<GifModel>().toMutableList()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GifViewHolder {
        val gifView = LayoutInflater.from(parent.context)
                .inflate(R.layout.gif_view, parent, false)
        return GifViewHolder(gifView)
    }

    override fun getItemCount(): Int {
        return gifModels.value!!.size
    }

    override fun onBindViewHolder(holder: GifViewHolder, position: Int) {

        Glide.with(context)
                .load(gifModels.value!![position].url)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(holder.imageView)
    }
}