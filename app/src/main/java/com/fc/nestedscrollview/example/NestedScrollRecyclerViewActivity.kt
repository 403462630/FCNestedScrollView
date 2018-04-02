package com.fc.nestedscrollview.example

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_recycler.*

class NestedScrollRecyclerViewActivity : AppCompatActivity() {

    private var myAdapter : MyRecyclerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycler)
        myAdapter = MyRecyclerAdapter("item")
        recycler_view.apply {
            layoutManager = LinearLayoutManager(this@NestedScrollRecyclerViewActivity)
            adapter = myAdapter
        }
    }
}
