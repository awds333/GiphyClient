package io.demo.fedchenko.giphyclient.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import io.demo.fedchenko.giphyclient.R
import io.demo.fedchenko.giphyclient.model.GifModel

class GifListAdapter(var context: Context) : RecyclerView.Adapter<GifViewHolder>() {
    var gifModels: MutableLiveData<MutableList<GifModel>> = MutableLiveData()

    init {
        gifModels.value = emptyList<GifModel>().toMutableList()
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
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(holder.imageView)
        /*holder.imageView.layoutParams = FrameLayout.LayoutParams(
            (holder.imageView.parent as View).width
            , (holder.imageView.parent as View).width * gifModels.value!![position].images.gifInfo.height
                    / gifModels.value!![position].images.gifInfo.width
        )*/
        /*
        Размер imageView должен был задоваться пропорционально размеру гивки, с максимальной шириной.
        Однако, на момент binding-га первых элементов рамер view еще 0/0.
        И я просто не знаю, где взять ширину столбца, чтобы задать пропорционально размер imageView.
        В связи с этим, с размерами происходит ерунда, порой с выходом за границы экрана.
        Расскажите пожалуйста как такое решать https://github.com/awds333/GiphyClient
         */
        holder.imageView.layoutParams = FrameLayout.LayoutParams(
            gifModels.value!![position].width,
            gifModels.value!![position].height
        )

    }

    override fun onViewRecycled(holder: GifViewHolder) {
        super.onViewRecycled(holder)
        Glide.clear(holder.imageView)
    }
}