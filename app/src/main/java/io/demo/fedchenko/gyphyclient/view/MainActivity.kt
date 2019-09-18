package io.demo.fedchenko.gyphyclient.view

import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import io.demo.fedchenko.gyphyclient.R
import io.demo.fedchenko.gyphyclient.adapter.GifListAdapter
import io.demo.fedchenko.gyphyclient.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var mainViewModel: MainViewModel
    lateinit var adapter: GifListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainViewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        mainViewModel.click.observe(this, Observer {c:Boolean -> button.text = c.toString()})
        button.setOnClickListener{
            mainViewModel.onClick()}

        adapter = GifListAdapter(this)
        adapter.gifModels = mainViewModel.gifModels

        var spans = if(resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) 2 else 3
        recycler.layoutManager = StaggeredGridLayoutManager(spans,LinearLayoutManager.VERTICAL)
        recycler.adapter = adapter

        mainViewModel.gifModels.observe(this, Observer { adapter.notifyDataSetChanged() })


    }
}
