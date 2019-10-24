package io.demo.fedchenko.giphyclient.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import io.demo.fedchenko.giphyclient.model.GifModel


class GifListAdapter(private val context: Context, val span: Int) :
    RecyclerView.Adapter<GifViewHolder>() {

    interface GifOnItemClickListener {
        fun onItemClick(item: GifModel)
    }

    private var gifModels: List<GifModel> = emptyList()
    private var gifOnItemClickListener: GifOnItemClickListener? = null

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

    fun setOnItemClickListener(listener: GifOnItemClickListener) {
        gifOnItemClickListener = listener
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GifViewHolder {
        val gifView = LayoutInflater.from(parent.context)
            .inflate(io.demo.fedchenko.giphyclient.R.layout.gif_view, parent, false)

        return GifViewHolder(
            gifView,
            CircularProgressDrawable(context).apply {
                strokeWidth = 5f
                centerRadius = 30f
                start()
            }, parent.width / span
        )
    }

    override fun getItemCount(): Int {
        return gifModels.size
    }

    override fun onBindViewHolder(holder: GifViewHolder, position: Int) {
        val model = gifModels[position]
        Glide.with(context)
            .load(model.preview.url)
            .placeholder(holder.progressDrawable)
            .error(android.R.drawable.ic_delete)
            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
            .into(holder.imageView)

        holder.imageView.setOnClickListener {
            gifOnItemClickListener?.onItemClick(model)
        }

        holder.imageView.layoutParams = FrameLayout.LayoutParams(
            holder.width,
            holder.width * model.preview.height / model.preview.width
        )
    }

    override fun onViewRecycled(holder: GifViewHolder) {
        super.onViewRecycled(holder)
        Glide.clear(holder.imageView)
    }
}