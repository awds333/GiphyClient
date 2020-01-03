package io.demo.fedchenko.giphyclient.viewmodel

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import io.demo.fedchenko.giphyclient.model.GifModel

interface GifObservable {
    fun observeGifModels(lifecycleOwner: LifecycleOwner, observer: Observer<List<GifModel>>)
}