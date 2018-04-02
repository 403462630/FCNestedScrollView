package com.fc.nestedscrollview.example

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_three.*

class ThreeNestedActivity : AppCompatActivity() {

    private var myAdapter : MyRecyclerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_three)
        myAdapter = MyRecyclerAdapter("item")
        recycler_view.apply {
            layoutManager = LinearLayoutManager(this@ThreeNestedActivity)
            adapter = myAdapter
        }
        nested_scroll_view.setLinkChildView(nested_scroll_view_child)
        nested_scroll_view_child.setLinkChildView(recycler_view)
    }
}
