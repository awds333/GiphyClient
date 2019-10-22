package io.demo.fedchenko.giphyclient.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import io.demo.fedchenko.giphyclient.R
import io.demo.fedchenko.giphyclient.model.GifModel

class GifListAdapter(private val context: Context, lifecycleOwner: LifecycleOwner, private val gifModels: LiveData<List<GifModel>>) :
    RecyclerView.Adapter<GifViewHolder>() {

    private var latestElement: GifModel? = null

    init {
        gifModels.observe(lifecycleOwner, Observer {
            if ((it.isEmpty() && latestElement != null) || (it.isNotEmpty() && latestElement == null)) {
                notifyDataSetChanged()
                latestElement = if (it.isEmpty()) null else it[0]
                return@Observer
            }
            if (latestElement != null) {
                if (it[0] != latestElement) {
                    notifyDataSetChanged()
                    latestElement = it[0]
                }
            }
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GifViewHolder {
        val gifView = LayoutInflater.from(parent.context)
            .inflate(R.layout.gif_view, parent, false)

        //gifView.measure(View.MeasureSpec.EXACTLY,View.MeasureSpec.UNSPECIFIED)
        return GifViewHolder(gifView,
            CircularProgressDrawable(context).apply {
                strokeWidth = 5f
                centerRadius = 30f
                start()
            })
    }

    override fun getItemCount(): Int {
        return gifModels.value!!.size
    }

    override fun onBindViewHolder(holder: GifViewHolder, position: Int) {
        Glide.with(context)
            .load(gifModels.value!![position].url)
            .placeholder(holder.progressDrawable)
            .error(android.R.drawable.ic_delete)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .into(holder.imageView)
        holder.imageView.layoutParams = FrameLayout.LayoutParams(
            gifModels.value!![position].width,
            gifModels.value!![position].height
        )
        holder.imageView.post {
            holder.imageView.layoutParams = FrameLayout.LayoutParams(
                (holder.imageView.parent as View).width
                , (holder.imageView.parent as View).width * gifModels.value!![position].height
                        / gifModels.value!![position].width)
        }
    }

    override fun onViewRecycled(holder: GifViewHolder) {
        super.onViewRecycled(holder)
        Glide.clear(holder.imageView)
    }
}