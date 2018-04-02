package com.fc.nestedscrollview.example

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.LinearLayout
import com.fc.nestedscrollview.example.fragment.Tab1Fragment
import com.fc.nestedscrollview.example.fragment.Tab2Fragment
import com.fc.nestedscrollview.example.fragment.Tab3Fragment
import kotlinx.android.synthetic.main.activity_example5.*

class Example5Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_example5)

        nested_scroll_view.viewTreeObserver.addOnGlobalLayoutListener (object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                var height = nested_scroll_view.height
                if (height != 0) {
                    ll_page_container.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height)
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        nested_scroll_view.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    } else {
                        nested_scroll_view.viewTreeObserver.removeGlobalOnLayoutListener(this)
                    }
                }
            }
        })
        initViewPage()
    }

    private fun initViewPage() {
        view_pager.adapter = MyViewPageAdapter(supportFragmentManager)
        tab_layout.setupWithViewPager(view_pager)
        view_pager.offscreenPageLimit = 3
    }

    private class MyViewPageAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {

        private val titles = listOf("tab1", "tab2", "tab3")
        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> Tab1Fragment()
                1 -> Tab2Fragment()
                else -> Tab3Fragment()
            }
        }

        override fun getCount(): Int {
            return 3
        }

        override fun getPageTitle(position: Int): CharSequence {
            return titles[position]
        }
    }
}
