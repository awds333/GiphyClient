package io.demo.fedchenko.giphyclient.view.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import io.demo.fedchenko.giphyclient.ConnectivityLiveData
import io.demo.fedchenko.giphyclient.R
import io.demo.fedchenko.giphyclient.view.dialog.NoConnectionDialog
import io.demo.fedchenko.giphyclient.view.fragment.FavoriteFragment
import io.demo.fedchenko.giphyclient.view.fragment.MainFragment
import kotlinx.android.synthetic.main.activity_root.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class RootActivity : AppCompatActivity() {

    companion object Tags {
        const val NO_CONNECTION_DIALOG_TAG = "noConnectionTag"
    }

    private val connectivityLiveData: LiveData<Boolean> by inject<ConnectivityLiveData> {
        parametersOf(
            application
        )
    }

    private val noConnectionDialog = NoConnectionDialog().apply {
        isCancelable = false
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_root)
        viewPager.adapter =
            RootPageAdapter(
                supportFragmentManager
            )
        bottomNavigationView.setOnNavigationItemSelectedListener {
            viewPager.setCurrentItem(it.order, true)
            true
        }
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                bottomNavigationView.selectedItemId = when (position) {
                    0 -> R.id.search_menu_item
                    1 -> R.id.favorite_menu_item
                    else -> throw Exception("No such element Exception")
                }
            }

        })
        connectivityLiveData.observe(this, Observer {
            if (it) {
                if (isConnectionDialogAdded())
                    noConnectionDialog.dismiss()
            } else {
                if (!isConnectionDialogAdded())
                    noConnectionDialog.show(supportFragmentManager, NO_CONNECTION_DIALOG_TAG)
            }
        })
    }

    override fun onStop() {
        if (isConnectionDialogAdded())
            noConnectionDialog.dismiss()
        super.onStop()
    }

    private fun isConnectionDialogAdded(): Boolean {
        return supportFragmentManager.findFragmentByTag(NO_CONNECTION_DIALOG_TAG) != null
    }
}

class RootPageAdapter(fm: FragmentManager) :
    FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> MainFragment()
            1 -> FavoriteFragment()
            else -> throw Exception("No such element Exception")
        }
    }

    override fun getCount(): Int {
        return 2
    }
}
