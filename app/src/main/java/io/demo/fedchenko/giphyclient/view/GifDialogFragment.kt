package io.demo.fedchenko.giphyclient.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import io.demo.fedchenko.giphyclient.R
import io.demo.fedchenko.giphyclient.model.GifModel
import kotlinx.android.synthetic.main.fragment_dialog_gif.*

class GifDialogFragment : DialogFragment() {

    companion object GifDialogFragmentBuilder {
        fun create(model: GifModel): GifDialogFragment {
            val gifDialog = GifDialogFragment()
            val args = Bundle()

            args.putString("url", model.url)
            args.putInt("width", model.width)
            args.putInt("height", model.height)

            gifDialog.arguments = args
            return gifDialog
        }
    }

    private var url = ""
    private var height = 0
    private var width = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.AppTheme)

        url = arguments!!.getString("url")
        height = arguments!!.getInt("width")
        width = arguments!!.getInt("width")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_dialog_gif, container, false)

    override fun onStart() {
        super.onStart()
        Glide.with(context)
            .load(url)
            .placeholder(CircularProgressDrawable(context!!).apply {
                strokeWidth = 5f
                centerRadius = 30f
                start()
            })
            .error(android.R.drawable.ic_delete)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .into(bigGifView)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Glide.clear(bigGifView)
    }
}