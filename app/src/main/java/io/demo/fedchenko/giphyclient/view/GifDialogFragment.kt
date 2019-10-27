package io.demo.fedchenko.giphyclient.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.gson.Gson
import io.demo.fedchenko.giphyclient.R
import io.demo.fedchenko.giphyclient.model.GifModel
import kotlinx.android.synthetic.main.fragment_dialog_gif.*

class GifDialogFragment : DialogFragment() {

    companion object GifDialogFragmentBuilder {
        fun create(model: GifModel): GifDialogFragment {
            val gifDialog = GifDialogFragment()
            val args = Bundle()

            args.putString("model", Gson().toJson(model).toString())

            gifDialog.arguments = args
            return gifDialog
        }
    }

    private lateinit var model: GifModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.AppTheme)

        arguments?.getString("model")?.also {
            model = Gson().fromJson(it, GifModel::class.java)
        } ?: return run {
            dismiss()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_dialog_gif, container, false)

    override fun onStart() {
        super.onStart()
        Glide.with(context)
            .load(model.original.url)
            .placeholder(
                CircularProgressDrawable(context!!).apply {
                    strokeWidth = 5f
                    centerRadius = 30f
                    start()
                })
            .error(android.R.drawable.ic_delete)
            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
            .into(bigGifView)


        userNameView.text = model.userName

        bigGifView.post {
            val viewHeight = bigGifView.height
            val viewWidth = bigGifView.width

            val fixedHeight = viewWidth * model.original.height / model.original.width

            if (fixedHeight > viewHeight) {
                bigGifView.layoutParams.width =
                    viewHeight * model.original.width / model.original.height
            } else {
                bigGifView.layoutParams.height = fixedHeight
            }
        }

        gifInfoButton.setOnClickListener {
            val infoDialog: GifInfoDialogFragment = GifInfoDialogFragment.create(model)
            infoDialog.show(fragmentManager, "info_dialog")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Glide.clear(bigGifView)
    }
}