package com.fc.nestedscrollview.example

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.extensions.ContainerOptions
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_my.*

/**
 * Created by fangcan on 2018/3/28.
 */
class MyRecyclerAdapter(var text: String) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(LayoutInflater.from(parent!!.context).inflate(R.layout.item_my, parent, false))
    }

    override fun getItemCount(): Int {
        return 20
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        (holder as MyViewHolder).bindData(position, text)
    }


    @ContainerOptions
    class MyViewHolder(override val containerView: View?) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun bindData(position: Int, text: String) {
            tv_text.text = text + "_" + position
        }
    }
}