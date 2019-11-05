package io.demo.fedchenko.giphyclient

import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.GlideDrawable
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget
import io.demo.fedchenko.giphyclient.adapter.GifListAdapter
import io.demo.fedchenko.giphyclient.model.GifModel
import io.demo.fedchenko.giphyclient.model.GifProperties
import kotlinx.android.synthetic.main.gif_image_view.view.*

@BindingAdapter("customUrl")
fun loadGif(view: View, url: String) {
    if (view.gifImageView == null || view.circlePogressBar == null)
        return
    Glide.clear(view.gifImageView)
    view.circlePogressBar.visibility = View.VISIBLE
    Glide.with(view.context)
        .load(url)
        .error(android.R.drawable.ic_delete)
        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
        .into(object : GlideDrawableImageViewTarget(view.gifImageView) {
            override fun onResourceReady(
                resource: GlideDrawable?,
                animation: GlideAnimation<in GlideDrawable>?
            ) {
                super.onResourceReady(resource, animation)
                view.circlePogressBar.visibility = View.GONE
            }
        })
}

@BindingAdapter("gifInfo", "ratioWidth", requireAll = true)
fun setHeight(view: View, properties: GifProperties, width: Int) {
    view.layoutParams.height = width * properties.height / properties.width
}

@BindingAdapter("ratioSize")
fun setSize(view: View, properties: GifProperties) {
    view.post {
        val viewHeight = view.height
        val viewWidth = view.width

        val fixedHeight = viewWidth * properties.height / properties.width

        if (fixedHeight > viewHeight) {
            view.layoutParams.width =
                viewHeight * properties.width / properties.height
        } else {
            view.layoutParams.height = fixedHeight
        }
    }
}

@BindingAdapter("searchListener")
fun setSearchListener(view: EditText, action: () -> Unit) {
    view.setOnEditorActionListener { _, actionId, _ ->
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            action()
        }
        true
    }
}

@BindingAdapter("onScrollEndListener")
fun setScrollEndListener(recyclerView: RecyclerView, action: () -> Unit) {
    recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            (recyclerView.layoutManager as StaggeredGridLayoutManager).also {
                val count = it.itemCount - 1
                val lastVisible = it.findLastVisibleItemPositions(IntArray(it.spanCount))
                lastVisible.forEach {
                    if (it == count) {
                        action()
                        return@forEach
                    }
                }
            }
        }
    })
}

@BindingAdapter("onItemClickModel", "onItemClickListener")
fun setOpenGifViewDialog(
    view: View,
    model: GifModel,
    listener: GifListAdapter.GifOnItemClickListener
) {
    view.setOnClickListener {
        listener.onItemClick(view, model)
    }
}