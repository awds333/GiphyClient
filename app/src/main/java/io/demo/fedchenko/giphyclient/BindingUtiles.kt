package io.demo.fedchenko.giphyclient

import android.R
import android.graphics.drawable.Drawable
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import androidx.core.view.GestureDetectorCompat
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.GlideDrawable
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget
import io.demo.fedchenko.giphyclient.model.GifProperties
import io.demo.fedchenko.giphyclient.viewmodel.OnScrollListener
import kotlinx.android.synthetic.main.gif_image_view.view.*

@BindingAdapter("customUrl", "placeHolder")
fun loadGif(view: View, url: String?, placeholder: Drawable? = null) {
    if (view.gifImageView == null || view.circlePogressBar == null)
        return
    if (url == null) {
        view.gifImageView.setImageDrawable(placeholder)
        return
    }
    Glide.clear(view.gifImageView)
    view.circlePogressBar.visibility = View.VISIBLE
    val request = Glide.with(view.context)
        .load(url)
        .error(R.drawable.ic_delete)
        .diskCacheStrategy(DiskCacheStrategy.SOURCE)

    placeholder?.also { request.placeholder(it) }

    request.into(object : GlideDrawableImageViewTarget(view.gifImageView) {
        override fun onResourceReady(
            resource: GlideDrawable?,
            animation: GlideAnimation<in GlideDrawable>?
        ) {
            super.onResourceReady(resource, animation)
            view.circlePogressBar.visibility = View.GONE
        }

        override fun onLoadFailed(e: Exception?, errorDrawable: Drawable?) {
            super.onLoadFailed(e, errorDrawable)
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

@BindingAdapter("onScrollListener")
fun setScrollListener(recyclerView: RecyclerView, listener: OnScrollListener) {
    recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            (recyclerView.layoutManager as StaggeredGridLayoutManager).also { layoutManager ->
                val lastVisible =
                    layoutManager.findLastVisibleItemPositions(null)
                listener.onScroll(lastVisible.max() ?: 0)
            }
        }
    })
}

@BindingAdapter("arrayAdapter")
fun setArrayAdapter(textView: AutoCompleteTextView, array: List<String>) {
    textView.setAdapter(
        ArrayAdapter<String>(
            textView.context,
            R.layout.simple_list_item_1, array
        )
    )
}

@BindingAdapter("onItemClick")
fun setOnItemClick(textView: AutoCompleteTextView, action: () -> Unit) {
    textView.setOnItemClickListener { _, _, _, _ ->
        action()
    }
}

@BindingAdapter("onRefresh")
fun setOnRefresh(view: SwipeRefreshLayout, action: () -> Unit) {
    view.setOnRefreshListener {
        action()
    }
}

@BindingAdapter("refreshing")
fun setRefreshing(view: SwipeRefreshLayout, refreshing: Boolean) {
    view.isRefreshing = refreshing
}

@BindingAdapter("bind:onLongPress","bind:customOnClick", requireAll = false)
fun setOnLongPress(view: View, onLongPress: View.OnClickListener? = null, onClick: View.OnClickListener? = null){
    val gestureDetector = GestureDetectorCompat(view.context,object : GestureDetector.SimpleOnGestureListener(){
        override fun onLongPress(e: MotionEvent?) {
            onLongPress?.onClick(view)
        }

        override fun onSingleTapUp(e: MotionEvent?): Boolean {
            onClick?.onClick(view)
            return true
        }


    })
    view.setOnTouchListener { _, event ->
        gestureDetector.onTouchEvent(event)
        return@setOnTouchListener true
    }
}