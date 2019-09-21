package io.demo.fedchenko.gyphyclient.view

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import io.demo.fedchenko.gyphyclient.R
import io.demo.fedchenko.gyphyclient.adapter.GifListAdapter
import io.demo.fedchenko.gyphyclient.model.GifModel
import io.demo.fedchenko.gyphyclient.repository.Repository
import io.demo.fedchenko.gyphyclient.viewmodel.MainViewModel
import io.demo.fedchenko.gyphyclient.viewmodel.MainViewModelFactory
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var mainViewModel: MainViewModel
    lateinit var adapter: GifListAdapter
    lateinit var layoutManager: StaggeredGridLayoutManager
    private var previousList = listOf(GifModel("", 0, 0, "", ""))

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
            hideKeyboard()
            mainViewModel.getTrending()
        }

        val spans =
            if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) 2 else 3
        layoutManager = StaggeredGridLayoutManager(spans, LinearLayoutManager.VERTICAL)
        recycler.layoutManager = layoutManager

        adapter = GifListAdapter(applicationContext)
        adapter.gifModels = mainViewModel.gifModels
        recycler.adapter = adapter

        mainViewModel.gifModels.observe(this, Observer {
            if (it.isEmpty() || previousList.isEmpty() || it[0] != previousList[0]) {
                adapter.notifyDataSetChanged()
                previousList = it
            }
        })

        recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val count = layoutManager.itemCount - 1
                val lastVisible = layoutManager.findLastVisibleItemPositions(IntArray(3))
                lastVisible.forEach {
                    if (it == count)
                        mainViewModel.getMoreGifs()
                }
            }
        })

        mainViewModel.state.observe(this, Observer {
            if (it == MainViewModel.State.REQUEST_FAILED)
                Toast.makeText(this, R.string.request_failed, Toast.LENGTH_LONG).show()
        })
    }

    private fun hideKeyboard() {
        val view = currentFocus
        view.clearFocus()
        if (view != null)
            (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
                view.windowToken,
                0
            )
    }
}
