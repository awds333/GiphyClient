package io.demo.fedchenko.gyphyclient.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import io.demo.fedchenko.gyphyclient.model.GifModel

class MainViewModel(application: Application) : AndroidViewModel(application) {

    var click : MutableLiveData<Boolean> = MutableLiveData()
    var gifModels : MutableLiveData<MutableList<GifModel>> = MutableLiveData()

    init {
        click.value = false
        gifModels.value = emptyList<GifModel>().toMutableList()
    }

    fun onClick() {
        var url = "https://media0.giphy.com/media/NjevnbNiUmeLm/giphy.gif?cid=bbea74ec8e969a46774116bdf5e8e61c7d206c5421500431&rid=giphy.gif"
        gifModels.value = gifModels.value.apply {
            this!!.add(GifModel("r",url))
        }
    }
}