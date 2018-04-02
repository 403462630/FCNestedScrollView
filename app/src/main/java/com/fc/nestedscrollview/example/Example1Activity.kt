package com.fc.nestedscrollview.example

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_example1.*

class Example1Activity : AppCompatActivity() {

    private var myAdapter1 : MyRecyclerAdapter? = null
    private var myAdapter2 : MyRecyclerAdapter? = null
    private var myAdapter3 : MyRecyclerAdapter? = null
    private var myAdapter4 : MyRecyclerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_example1)
        myAdapter1 = MyRecyclerAdapter("我先滚动")
        myAdapter2 = MyRecyclerAdapter("我最后滚动")
        myAdapter3 = MyRecyclerAdapter("我先向下滚")
        myAdapter4 = MyRecyclerAdapter("我先向上滚")

        recycler_view1.apply {
            layoutManager = LinearLayoutManager(this@Example1Activity)
            adapter = myAdapter1
        }

        recycler_view2.apply {
            layoutManager = LinearLayoutManager(this@Example1Activity)
            adapter = myAdapter2
        }

        recycler_view3.apply {
            layoutManager = LinearLayoutManager(this@Example1Activity)
            adapter = myAdapter3
        }

        recycler_view4.apply {
            layoutManager = LinearLayoutManager(this@Example1Activity)
            adapter = myAdapter4
        }
    }
}
