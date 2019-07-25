package com.fc.nestedscrollview.example.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.fc.nestedscrollview.example.R
import kotlinx.android.synthetic.main.fragment_tab2.*

/**
 * Created by fangcan on 2018/4/2.
 */
class Tab2Fragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_tab2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycler_view.logId = "child"
        recycler_view.enableLog = true
    }
}