package io.demo.fedchenko.giphyclient.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.demo.fedchenko.giphyclient.databinding.GifViewBinding
import io.demo.fedchenko.giphyclient.model.GifModel


class GifListAdapter(private val span: Int) :
    RecyclerView.Adapter<GifViewHolder>() {

    private var gifModels: List<GifModel> = emptyList()
    private var gifOnItemClickListener: ((View, GifModel) -> Unit)? = null
    private var gifOnItemFavoriteClickListener: ((View, GifModel) -> Unit)? = null

    val gifModelsObserver = Observer<List<GifModel>> {
        val oldModels = gifModels
        gifModels = it
        val diff = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldModels[oldItemPosition].id == gifModels[newItemPosition].id
                        && oldModels[oldItemPosition].isFavorite == gifModels[newItemPosition].isFavorite
            }

            override fun getOldListSize(): Int {
                return oldModels.size
            }

            override fun getNewListSize(): Int {
                return gifModels.size
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldModels[oldItemPosition] == gifModels[newItemPosition]
                        && oldModels[oldItemPosition].isFavorite == gifModels[newItemPosition].isFavorite
            }
        })
        diff.dispatchUpdatesTo(this)
    }

    fun setOnItemClickListener(listener: (View, GifModel) -> Unit) {
        gifOnItemClickListener = listener
    }

    fun setOnItemFavoriteClickListener(listener: (View, GifModel) -> Unit) {
        gifOnItemFavoriteClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GifViewHolder {
        val binding: GifViewBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            io.demo.fedchenko.giphyclient.R.layout.gif_view,
            parent,
            false
        )
        binding.ratioWidth = parent.width / span

        return GifViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return gifModels.size
    }

    override fun onBindViewHolder(holder: GifViewHolder, position: Int) {
        val model = gifModels[position]
        holder.binding.clickListener = View.OnClickListener {
            gifOnItemClickListener?.invoke(it, model)
        }
        holder.binding.favoriteClickListener = View.OnClickListener {
            gifOnItemFavoriteClickListener?.invoke(it, model)
        }
        holder.binding.gifModel = model
        holder.binding.executePendingBindings()
        Log.d("taggg", model.isFavorite.toString())
    }
}