package io.demo.fedchenko.gyphyclient.view

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import io.demo.fedchenko.gyphyclient.R
import io.demo.fedchenko.gyphyclient.adapter.GifListAdapter
import io.demo.fedchenko.gyphyclient.repository.Repository
import io.demo.fedchenko.gyphyclient.viewmodel.MainViewModel
import io.demo.fedchenko.gyphyclient.viewmodel.MainViewModelFactory
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var mainViewModel: MainViewModel
    lateinit var adapter: GifListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainViewModel = ViewModelProviders.of(this, MainViewModelFactory(application, Repository()))
            .get(MainViewModel::class.java)

        mainViewModel.loading.observe(
            this,
            Observer { progressBar.visibility = if (it) View.VISIBLE else View.GONE })
        searchField.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                mainViewModel.search(searchField.text.toString())
                if (searchField.text.toString().trim() != "")
                    hideKeyboard()
            }
            true
        }

        trendingImage.setOnClickListener {
            mainViewModel.trending()
        }

        adapter = GifListAdapter(this)
        adapter.gifModels = mainViewModel.gifModels

        val spans =
            if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) 2 else 3
        recycler.layoutManager = StaggeredGridLayoutManager(spans, LinearLayoutManager.VERTICAL)
        recycler.adapter = adapter

        mainViewModel.gifModels.observe(this, Observer { adapter.notifyDataSetChanged() })


    }

    private fun hideKeyboard() {
        val view = currentFocus
        if (view != null)
            (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
                view.windowToken,
                0
            )
    }
}
