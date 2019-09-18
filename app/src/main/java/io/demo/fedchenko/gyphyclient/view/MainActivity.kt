package io.demo.fedchenko.gyphyclient.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import io.demo.fedchenko.gyphyclient.R
import io.demo.fedchenko.gyphyclient.viewModel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainViewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        mainViewModel.click.observe(this, Observer {c:Boolean -> button.text = c.toString()})
        button.setOnClickListener{mainViewModel.onClick()}
    }
}
