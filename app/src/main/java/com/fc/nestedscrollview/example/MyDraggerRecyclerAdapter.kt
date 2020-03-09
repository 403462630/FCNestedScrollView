package com.fc.nestedscrollview.example

import android.view.View
import com.chad.library.adapter.base.BaseItemDraggableAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kotlinx.android.extensions.ContainerOptions
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_dragger.*

/**
 * Created by fangcan on 2018/3/28.
 */
class MyDraggerRecyclerAdapter(data: List<String>) : BaseItemDraggableAdapter<String, MyDraggerRecyclerAdapter.MyViewHolder>(R.layout.item_dragger, data) {

    override fun convert(helper: MyViewHolder, item: String?) {
        helper!!.setText(R.id.tv_content, item)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        (holder as MyViewHolder).bindData(position, getItem(position)!!)
    }


    @ContainerOptions
    class MyViewHolder(override val containerView: View?) : BaseViewHolder(containerView), LayoutContainer {

        fun bindData(position: Int, text: String) {
            tv_content.text = text
        }
    }
}