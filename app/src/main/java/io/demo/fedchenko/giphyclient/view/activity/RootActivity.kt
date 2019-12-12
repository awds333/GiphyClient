package io.demo.fedchenko.giphyclient.view.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import io.demo.fedchenko.giphyclient.R
import io.demo.fedchenko.giphyclient.view.fregment.FavoriteFragment
import io.demo.fedchenko.giphyclient.view.fregment.SearchFragment
import kotlinx.android.synthetic.main.activity_root.*

class RootActivity : AppCompatActivity() {

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
            ) {}

            override fun onPageSelected(position: Int) {
                bottomNavigationView.selectedItemId = when (position) {
                    0 -> R.id.search_menu_item
                    1 -> R.id.favorite_menu_item
                    else -> throw Exception("No such element Exception")
                }
            }

        })
    }
}

class RootPageAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> SearchFragment()
            1 -> FavoriteFragment()
            else -> throw Exception("No such element Exception")
        }
    }

    override fun getCount(): Int {
        return 2
    }
}
