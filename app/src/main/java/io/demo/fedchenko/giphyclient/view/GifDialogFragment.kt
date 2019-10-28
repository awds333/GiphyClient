package io.demo.fedchenko.giphyclient.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.google.gson.Gson
import io.demo.fedchenko.giphyclient.R
import io.demo.fedchenko.giphyclient.databinding.FragmentDialogGifBinding
import io.demo.fedchenko.giphyclient.model.GifModel
import kotlinx.android.synthetic.main.fragment_dialog_gif.*

class GifDialogFragment : DialogFragment() {

    interface GifInfoViewer {
        fun showInfo()
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
    ): View {
        binding =
            DataBindingUtil.inflate(layoutInflater, R.layout.fragment_dialog_gif, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        binding.progressDrawable = CircularProgressDrawable(context!!).apply {
            strokeWidth = 5f
            centerRadius = 30f
            start()
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
        Glide.clear(bigGifView)
    }
}