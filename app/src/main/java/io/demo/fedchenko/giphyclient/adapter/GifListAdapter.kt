package io.demo.fedchenko.giphyclient.adapter

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

    val gifModelsObserver = Observer<List<GifModel>> {
        val oldModels = gifModels
        gifModels = it
        val diff = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldModels[oldItemPosition].original.url == gifModels[newItemPosition].original.url
            }

            override fun getOldListSize(): Int {
                return oldModels.size
            }

            override fun getNewListSize(): Int {
                return gifModels.size
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldModels[oldItemPosition] == gifModels[newItemPosition]
            }
        })
        diff.dispatchUpdatesTo(this)
    }

    fun setOnItemClickListener(listener: (View, GifModel) -> Unit) {
        gifOnItemClickListener = listener
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
        holder.binding.gifModel = model
        holder.binding.executePendingBindings()
    }
}