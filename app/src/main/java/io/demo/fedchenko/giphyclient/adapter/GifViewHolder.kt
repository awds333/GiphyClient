package io.demo.fedchenko.giphyclient.adapter

import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import io.demo.fedchenko.giphyclient.model.GifModel
import kotlinx.android.synthetic.main.gif_view.view.*

class GifViewHolder(
    itemView: View,
    val progressDrawable: CircularProgressDrawable,
    val width: Int
) :
    RecyclerView.ViewHolder(itemView) {
    val imageView: ImageView = itemView.imageView
}