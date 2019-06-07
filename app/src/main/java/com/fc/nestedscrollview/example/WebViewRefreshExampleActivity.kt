package com.fc.nestedscrollview.example

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_example_webview_refresh.*

class WebViewRefreshExampleActivity : AppCompatActivity() {

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_example_webview_refresh)
        web_view.webChromeClient = WebChromeClient()
        web_view.webViewClient = WebViewClient()
        web_view.settings.builtInZoomControls = true
        web_view.settings.javaScriptEnabled = true
        web_view.settings.domStorageEnabled = true
        web_view.loadUrl("http://www.baidu.com/")

        swipe_refresh.setOnRefreshListener {
            swipe_refresh.postDelayed({
                swipe_refresh.isRefreshing = false
            }, 5_000)
        }
    }
}
