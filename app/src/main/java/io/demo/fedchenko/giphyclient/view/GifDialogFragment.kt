package io.demo.fedchenko.giphyclient.view

import android.os.Bundle
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import io.demo.fedchenko.giphyclient.R
import io.demo.fedchenko.giphyclient.model.GifModel

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dialog_gif,container,false)


        return view
    }
}