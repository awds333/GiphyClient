package io.demo.fedchenko.giphyclient.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import io.demo.fedchenko.giphyclient.R
import io.demo.fedchenko.giphyclient.model.GifModel

class GifListAdapter(private val context: Context) :
    RecyclerView.Adapter<GifViewHolder>() {

    private var gifModels: List<GifModel> = emptyList()

    val gifModelsObserver = Observer<List<GifModel>> {
        val oldModels = gifModels
        gifModels = it
        val diff = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldModels[oldItemPosition].url == gifModels[newItemPosition].url
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GifViewHolder {
        val gifView = LayoutInflater.from(parent.context)
            .inflate(R.layout.gif_view, parent, false)

        return GifViewHolder(gifView,
            CircularProgressDrawable(context).apply {
                strokeWidth = 5f
                centerRadius = 30f
                start()
            })
    }

    override fun getItemCount(): Int {
        return gifModels.size
    }

    override fun onBindViewHolder(holder: GifViewHolder, position: Int) {
        val model = gifModels[position]
        Glide.with(context)
            .load(model.url)
            .placeholder(holder.progressDrawable)
            .error(android.R.drawable.ic_delete)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .into(holder.imageView)
        holder.imageView.post {
            val view = (holder.imageView.parent as View)
            holder.imageView.layoutParams = FrameLayout.LayoutParams(
                view.width,
                view.width * model.height / model.width
            )
        }
    }

    override fun onViewRecycled(holder: GifViewHolder) {
        super.onViewRecycled(holder)
        Glide.clear(holder.imageView)
    }
}