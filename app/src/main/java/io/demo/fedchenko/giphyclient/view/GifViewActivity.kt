package io.demo.fedchenko.giphyclient.view

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ShareCompat
import androidx.core.app.SharedElementCallback
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.google.gson.Gson
import io.demo.fedchenko.giphyclient.R
import io.demo.fedchenko.giphyclient.databinding.FragmentDialogGifBinding
import io.demo.fedchenko.giphyclient.model.GifModel
import kotlinx.android.synthetic.main.fragment_dialog_gif.*
import kotlinx.android.synthetic.main.gif_image_view.*

class GifViewActivity : AppCompatActivity() {
    private lateinit var model: GifModel
    private lateinit var binding: FragmentDialogGifBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setEnterSharedElementCallback(object : SharedElementCallback() {
            override fun onMapSharedElements(
                names: MutableList<String>?,
                sharedElements: MutableMap<String, View>?
            ) {
                if (names == null || sharedElements == null || gifImageViewInclude == null)
                    return
                gifImageViewInclude.transitionName = model.original.url
                sharedElements[names[0]] = gifImageViewInclude
            }

            override fun onSharedElementEnd(
                sharedElementNames: MutableList<String>?,
                sharedElements: MutableList<View>?,
                sharedElementSnapshots: MutableList<View>?
            ) {
                super.onSharedElementEnd(sharedElementNames, sharedElements, sharedElementSnapshots)
                binding.isTransitionEnded = true
            }
        })

        intent.extras?.getString("model")?.also {
            model = Gson().fromJson(it, GifModel::class.java)
        } ?: return run {
            finish()
        }

        binding = DataBindingUtil.setContentView(this, R.layout.fragment_dialog_gif)
        binding.distributor = object : GifDialogFragment.GifDistributor {
            override fun share() {
                ShareCompat.IntentBuilder.from(this@GifViewActivity)
                    .setType("text/plain")
                    .setChooserTitle("Share Gif")
                    .setText(model.original.url)
                    .startChooser()
            }
        }

        binding.gifModel = model

        binding.isTransitionEnded = false

        binding.infoViewer = object : GifDialogFragment.GifInfoViewer {
            override fun showInfo() {
                val infoDialog: GifInfoDialogFragment = GifInfoDialogFragment.create(model)
                infoDialog.show(supportFragmentManager, "info_dialog")
            }
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        binding.isTransitionEnded = true
    }

    override fun onDestroy() {
        if (gifImageView != null)
            Glide.clear(gifImageView)
        super.onDestroy()
    }
}