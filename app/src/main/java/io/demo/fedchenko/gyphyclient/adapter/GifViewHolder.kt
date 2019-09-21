package io.demo.fedchenko.gyphyclient.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import kotlinx.android.synthetic.main.gif_view.view.*

class GifViewHolder(itemView: View,val progressDrawable: CircularProgressDrawable) : RecyclerView.ViewHolder(itemView) {
    val imageView = itemView.imageView
}