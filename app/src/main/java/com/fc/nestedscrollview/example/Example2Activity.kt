package com.fc.nestedscrollview.example

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_example2.*

class Example2Activity : AppCompatActivity() {

    private var myAdapter1 : MyRecyclerAdapter? = null
    private var myAdapter2 : MyRecyclerAdapter? = null
    private var myAdapter3 : MyRecyclerAdapter? = null
    private var myAdapter4 : MyRecyclerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_example2)
        myAdapter1 = MyRecyclerAdapter("我先滚动")
        myAdapter2 = MyRecyclerAdapter("我最后滚动")
        myAdapter3 = MyRecyclerAdapter("我先向下滚")
        myAdapter4 = MyRecyclerAdapter("我先向上滚")

        recycler_view1.apply {
            layoutManager = LinearLayoutManager(this@Example2Activity)
            adapter = myAdapter1
        }

        recycler_view2.apply {
            layoutManager = LinearLayoutManager(this@Example2Activity)
            adapter = myAdapter2
        }

        recycler_view3.apply {
            layoutManager = LinearLayoutManager(this@Example2Activity)
            adapter = myAdapter3
        }

        recycler_view4.apply {
            layoutManager = LinearLayoutManager(this@Example2Activity)
            adapter = myAdapter4
        }

        swipe_refresh.setOnRefreshListener {
            swipe_refresh.postDelayed({
                swipe_refresh.isRefreshing = false
            }, 5_000)
        }

        swipe_refresh1.setOnRefreshListener {
            swipe_refresh1.postDelayed({
                swipe_refresh1.isRefreshing = false
            }, 5_000)
        }

        swipe_refresh2.setOnRefreshListener {
            swipe_refresh2.postDelayed({
                swipe_refresh2.isRefreshing = false
            }, 5_000)
        }

        swipe_refresh3.setOnRefreshListener {
            swipe_refresh3.postDelayed({
                swipe_refresh3.isRefreshing = false
            }, 5_000)
        }

        swipe_refresh4.setOnRefreshListener {
            swipe_refresh4.postDelayed({
                swipe_refresh4.isRefreshing = false
            }, 5_000)
        }
    }
}
