package io.demo.fedchenko.giphyclient.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.google.gson.Gson
import io.demo.fedchenko.giphyclient.R
import io.demo.fedchenko.giphyclient.databinding.FragmentDialogGifInfoBinding
import io.demo.fedchenko.giphyclient.model.GifModel

class GifInfoDialogFragment : DialogFragment() {
    companion object GifInfoDialogFragmentBuilder {
        fun create(model: GifModel): GifInfoDialogFragment {
            val gifInfoDialog = GifInfoDialogFragment()
            val args = Bundle()

            args.putString("model", Gson().toJson(model).toString())

            gifInfoDialog.arguments = args
            return gifInfoDialog
        }
    }

    private lateinit var model: GifModel
    private lateinit var binding: FragmentDialogGifInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
        binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.fragment_dialog_gif_info,
            container,
            false
        )
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        binding.gifModel = model
    }
}