package io.demo.fedchenko.giphyclient.view.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import io.demo.fedchenko.giphyclient.R
import io.demo.fedchenko.giphyclient.databinding.SearchFragmentBinding
import io.demo.fedchenko.giphyclient.viewmodel.GifObservable
import io.demo.fedchenko.giphyclient.viewmodel.SearchViewModel
import kotlinx.android.synthetic.main.search_fragment.*
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class SearchFragment : GifRecyclerFragment() {

    private val searchViewModel: SearchViewModel by viewModel { parametersOf(context!!.applicationContext) }

    private lateinit var binding: SearchFragmentBinding

    private var lastEmpty = false

    private val exceptionListener = {
        Toast.makeText(
            context,
            R.string.request_failed,
            Toast.LENGTH_LONG
        ).show()
    }
    private val keyboardListener: () -> Unit = {
        val view = activity?.currentFocus
        view?.clearFocus()
        (context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
            view?.windowToken,
            0
        )
    }

    override fun getRecyclerView(): RecyclerView = recycler

    override fun getGifObservable(): GifObservable = searchViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.search_fragment, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.lifecycleOwner = this
        binding.searchViewModel = searchViewModel

        searchViewModel.observeGifModels(this,
            Observer { t ->
                if (t.isNullOrEmpty())
                    lastEmpty = true
                else {
                    if (lastEmpty) {
                        recycler.post {
                            recycler.scrollToPosition(0)
                            recycler.scrollBy(0, Int.MIN_VALUE)
                        }
                        lastEmpty = false
                    }
                }
            })
    }

    override fun onResume() {
        super.onResume()
        searchViewModel.registerExceptionsListener(exceptionListener)
        searchViewModel.registerKeyboardListener(keyboardListener)
    }

    override fun onPause() {
        super.onPause()
        searchViewModel.removeExceptionListener()
        searchViewModel.removeKeyboardListener()
    }
}