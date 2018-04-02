package com.fc.nestedscrollview.example.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fc.nestedscrollview.example.MyRecyclerAdapter
import com.fc.nestedscrollview.example.R
import kotlinx.android.synthetic.main.fragment_tab1.*

/**
 * Created by fangcan on 2018/4/2.
 */
class Tab1Fragment : Fragment() {
    private var myAdapter : MyRecyclerAdapter? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return return inflater!!.inflate(R.layout.fragment_tab1, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        myAdapter = MyRecyclerAdapter("item_")
        recycler_view.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = myAdapter
        }
    }
}