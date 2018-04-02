package com.fc.nestedscrollview.example

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun gotoExample1(view: View) {
        startActivity(Intent(this, Example1Activity::class.java))
    }

    fun gotoExample2(view: View) {
        startActivity(Intent(this, Example2Activity::class.java))
    }

    fun gotoExample3(view: View) {
        startActivity(Intent(this, Example3Activity::class.java))
    }

    fun gotoExample4(view: View) {
        startActivity(Intent(this, Example4Activity::class.java))
    }

    fun gotoExample5(view: View) {
        startActivity(Intent(this, Example5Activity::class.java))
    }

    fun gotoWebviewExample(view: View) {
        startActivity(Intent(this, WebViewExampleActivity::class.java))
    }

    fun gotoWebviewRefreshExample(view: View) {
        startActivity(Intent(this, WebViewRefreshExampleActivity::class.java))
    }
}
