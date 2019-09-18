package io.demo.fedchenko.gyphyclient.viewModel

import android.app.Application
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel(application: Application) : AndroidViewModel(application) {

    var click : MutableLiveData<Boolean> = MutableLiveData()

    init {
        click.value = false
    }

    fun onClick() {
        click.value = !(click.value)!!
    }
}