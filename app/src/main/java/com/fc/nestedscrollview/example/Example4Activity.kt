package com.fc.nestedscrollview.example

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_example4.*

class Example4Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_example4)

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
