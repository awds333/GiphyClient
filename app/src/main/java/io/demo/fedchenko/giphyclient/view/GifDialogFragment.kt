package io.demo.fedchenko.giphyclient.view

import android.os.Build
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ShareCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.google.gson.Gson
import io.demo.fedchenko.giphyclient.databinding.FragmentDialogGifBinding
import io.demo.fedchenko.giphyclient.model.GifModel
import kotlinx.android.synthetic.main.gif_image_view.*



class GifDialogFragment : DialogFragment() {

    interface GifInfoViewer {
        fun showInfo()
    }

    interface GifDistributor {
        fun share()
    }

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
    private lateinit var binding: FragmentDialogGifBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, io.demo.fedchenko.giphyclient.R.style.AppTheme)

        val transition = TransitionInflater.from(context)
            .inflateTransition(io.demo.fedchenko.giphyclient.R.transition.image_shared_element_transition)
        sharedElementEnterTransition = transition
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        }

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
    ): View {
        binding =
            DataBindingUtil.inflate(layoutInflater, io.demo.fedchenko.giphyclient.R.layout.fragment_dialog_gif, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        binding.progressDrawable = CircularProgressDrawable(context!!).apply {
            strokeWidth = 5f
            centerRadius = 30f
            start()
        }

        binding.distributor = object : GifDistributor {
            override fun share() {
                ShareCompat.IntentBuilder.from(activity)
                    .setType("text/plain")
                    .setChooserTitle("Share Gif")
                    .setText(model.original.url)
                    .startChooser()
            }
        }

        binding.gifModel = model

        binding.infoViewer = object : GifInfoViewer {
            override fun showInfo() {
                val infoDialog: GifInfoDialogFragment = GifInfoDialogFragment.create(model)
                infoDialog.show(fragmentManager, "info_dialog")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Glide.clear(gifImageView)
    }
}