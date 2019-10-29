package io.demo.fedchenko.giphyclient

import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import io.demo.fedchenko.giphyclient.model.GifProperties
import io.demo.fedchenko.giphyclient.viewmodel.Searcher

@BindingAdapter("customUrl", "progressDrawable")
fun loadGif(view: ImageView, url: String, progressDrawable: CircularProgressDrawable) {
    Glide.clear(view)
    Glide.with(view.context)
        .load(url)
        .placeholder(progressDrawable)
        .error(android.R.drawable.ic_delete)
        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
        .into(view)
}

@BindingAdapter("gifInfo", "ratioWidth")
fun setHeight(view: ImageView, properties: GifProperties, width: Int) {
    view.layoutParams.height = width * properties.height / properties.width
}

@BindingAdapter("ratioSize")
fun setSize(view: ImageView, properties: GifProperties) {
    //Без post view.height и view.width == 0
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