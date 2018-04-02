package com.fc.nestedscrollview.example.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fc.nestedscrollview.example.R

/**
 * Created by fangcan on 2018/4/2.
 */
class Tab2Fragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_tab2, container, false)
    }
}