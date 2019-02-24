package com.fc.nestedscrollview.example.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import com.fc.nestedscrollview.example.R
import kotlinx.android.synthetic.main.fragment_tab3.*

/**
 * Created by fangcan on 2018/4/2.
 */
class Tab3Fragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_tab3, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        web_view.webChromeClient = WebChromeClient()
        web_view.webViewClient = WebViewClient()
        web_view.settings.builtInZoomControls = true
        web_view.settings.javaScriptEnabled = true
        web_view.settings.domStorageEnabled = true
        web_view.settings.setSupportZoom(false)
        web_view.setOnLongClickListener { true }
        web_view.loadUrl("http://news.baidu.com/")
    }
}