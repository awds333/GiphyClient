package io.demo.fedchenko.giphyclient.view.activity

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.transition.Transition
import android.transition.TransitionListenerAdapter
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ShareCompat
import androidx.core.app.SharedElementCallback
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.toDrawable
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import io.demo.fedchenko.giphyclient.R
import io.demo.fedchenko.giphyclient.databinding.ActivityGifViewBinding
import io.demo.fedchenko.giphyclient.model.GifModel
import io.demo.fedchenko.giphyclient.okhttp.FileDownloader
import io.demo.fedchenko.giphyclient.okhttp.OkHttpFileDownloader
import io.demo.fedchenko.giphyclient.view.dialog.GifInfoDialogFragment
import io.demo.fedchenko.giphyclient.viewmodel.FavoriteViewModel
import kotlinx.android.synthetic.main.activity_gif_view.*
import kotlinx.android.synthetic.main.gif_image_view.*
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.io.IOException


class GifViewActivity : AppCompatActivity() {
    private lateinit var model: GifModel
    private lateinit var binding: ActivityGifViewBinding

    private val favoriteViewModel: FavoriteViewModel by viewModel { parametersOf(this) }

    private val fileDownloader by inject<OkHttpFileDownloader> { parametersOf(this) }

    private val scope = CoroutineScope(Dispatchers.Main)

    companion object {
        const val MODEL = "model"
        private const val REQUEST_CODE = 375

        private var bitmap: Bitmap? = null

        fun start(model: GifModel, bitmap: Bitmap, context: Context, bundle: Bundle?) {
            Companion.bitmap = bitmap
            val intent = Intent(context, GifViewActivity::class.java)
                .putExtra(MODEL, model)

            (context as Activity).startActivityForResult(
                intent,
                REQUEST_CODE, bundle
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE)
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                shareGif()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        intent.extras?.getParcelable<GifModel>(MODEL)?.also {
            model = it
        } ?: return run {
            finish()
        }

        setEnterSharedElementCallback(object : SharedElementCallback() {
            override fun onMapSharedElements(
                names: MutableList<String>?,
                sharedElements: MutableMap<String, View>?
            ) {
                if (names == null || sharedElements == null || gifImageViewInclude == null)
                    return
                val sharedElementEnterTransition = window.sharedElementEnterTransition
                sharedElementEnterTransition.addListener(object : TransitionListenerAdapter() {
                    override fun onTransitionEnd(transition: Transition) {
                        super.onTransitionEnd(transition)
                        binding.areButtonsVisible = true
                    }
                })
            }
        })

        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_gif_view
        )

        binding.apply {
            onShareClick = View.OnClickListener {
                val dialogBuilder = AlertDialog.Builder(this@GifViewActivity)
                dialogBuilder.setItems(R.array.share_options) { _, which ->
                    when (which) {
                        0 -> {//url
                            shareUrl()
                        }
                        1 -> {//gif
                            checkPermissionFileShare()
                        }
                    }
                }
                dialogBuilder.show()
            }

            binding.placeHolder = bitmap?.toDrawable(resources)

            gifModel = model

            areButtonsVisible = false

            onInfoClick = View.OnClickListener {
                val infoDialog: GifInfoDialogFragment =
                    GifInfoDialogFragment.create(
                        model
                    )
                infoDialog.show(supportFragmentManager, "info_dialog")
            }

            onBackgroundClick = View.OnClickListener {
                onBackPressed()
            }

            favoriteClickListener = View.OnClickListener {
                favoriteViewModel.changeFavorite(model)
            }
        }

        favoriteViewModel.observeGifModels(this, Observer {
            binding.isFavorite = it.map { model -> model.id }.contains(model.id)
        })
    }

    private fun checkPermissionFileShare() {
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            shareGif()
        } else {
            ActivityCompat.requestPermissions(
                this@GifViewActivity,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_CODE
            )
        }
    }

    private fun shareGif() {
        scope.launch {
            binding.isShareProgressVisible = true
            try {
                val file = fileDownloader.getFile(model.original.url)
                ShareCompat.IntentBuilder.from(this@GifViewActivity)
                    .setChooserTitle(R.string.share_gif)
                    .setType("image/gif")
                    .setStream(
                        FileProvider.getUriForFile(
                            this@GifViewActivity,
                            "io.demo.fedchenko.giphyclient",
                            file
                        )
                    )
                    .startChooser()
            } catch (e: IOException) {
                Toast.makeText(this@GifViewActivity, R.string.request_failed, Toast.LENGTH_LONG)
                    .show()
            }
            delay(1000L)
            binding.isShareProgressVisible = false
        }
    }

    private fun shareUrl() {
        scope.launch {
            binding.isShareProgressVisible = true

            ShareCompat.IntentBuilder.from(this@GifViewActivity)
                .setChooserTitle(R.string.share_gif)
                .setType("plain/text")
                .setText(model.original.url)
                .startChooser()

            delay(1000L)
            binding.isShareProgressVisible = false
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        binding.areButtonsVisible = true
    }

    override fun onDestroy() {
        scope.cancel()
        if (gifImageView != null)
            Glide.clear(gifImageView)
        super.onDestroy()
    }
}