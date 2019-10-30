package io.demo.fedchenko.giphyclient

import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.GlideDrawable
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget
import io.demo.fedchenko.giphyclient.model.GifProperties
import io.demo.fedchenko.giphyclient.viewmodel.Searcher
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

@BindingAdapter("gifInfo", "ratioWidth")
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
fun setSearchListener(view: EditText, searcher: Searcher) {
    view.setOnEditorActionListener { _, actionId, _ ->
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            searcher.search(view.text.toString())
        }
        true
    }
}