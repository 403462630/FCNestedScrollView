package com.fc.nestedscrollview.example.fragment

import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.callback.ItemDragAndSwipeCallback
import com.chad.library.adapter.base.listener.OnItemDragListener
import com.fc.nestedscrollview.example.MyDraggerRecyclerAdapter
import com.fc.nestedscrollview.example.R
import kotlinx.android.synthetic.main.fragment_tab1.*

/**
 * Created by fangcan on 2018/4/2.
 */
class Tab1Fragment : Fragment() {
    private var itemDragAdapter: MyDraggerRecyclerAdapter? = null
    private var mItemTouchHelper: ItemTouchHelper? = null
    private var mItemDragAndSwipeCallback: ItemDragAndSwipeCallback? = null
    private var nestedScrollView: NestedScrollView? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return return inflater!!.inflate(R.layout.fragment_tab1, container, false)
    }

    private fun getViewLocation(view: View) : Rect {
        val location = intArrayOf(0, 0)
        view.getLocationInWindow(location)
        val height = view.height
        val width = view.width
        val rect = Rect()
        rect.left = location[0]
        rect.top = location[1]
        rect.bottom = rect.top + height
        rect.right = rect.left + width
        return rect
    }

    fun ifNeedScrollToBottomOrTop(recyclerView: RecyclerView, source: RecyclerView.ViewHolder, y: Int) {
        nestedScrollView?.let {
            val rect = getViewLocation(recyclerView)
            val scrollViewRect = getViewLocation(it)
            Log.i("ifNeedScrollToBottom", "height: ${recyclerView.height}, itemView.height: ${source.itemView.height}, scrollViewRect.top: ${scrollViewRect.top}, scrollViewRect.bottom: ${scrollViewRect.bottom}, y: $y, rect.top: ${rect.top} rect.bottom: ${rect.bottom}")
            if (y + source.itemView.height + rect.top > scrollViewRect.bottom) {
                if (it.canScrollVertically(1)) {
                    Log.i("ifNeedScrollToBottom", "scroll to bottom")
                    // 1. scroll to recyclerView bottom position
                    val bottomOffset = rect.bottom - scrollViewRect.bottom
                    it.smoothScrollBy(0, bottomOffset)
                    // 2. scroll itemView height distance
//                    it.smoothScrollBy(0, source.itemView.height)
                    // 3. scroll to scrollview bottom
//                    it.smoothScrollTo(0, it.getChildAt(0).height)
                }
            } else if (y + rect.top < scrollViewRect.top) {
                if (it.canScrollVertically(-1)) {
                    Log.i("ifNeedScrollToBottom", "scroll to top")
                    // 1. scroll to recyclerView top position
                    val topOffset = rect.top - scrollViewRect.top
                    it.scrollBy(0, topOffset)
                    // 2. scroll itemView height distance
//                    it.smoothScrollBy(0, -source.itemView.height)
                    // 3. scroll to scrollview top
//                    it.smoothScrollTo(0, 0)
                }
            }
        }
    }

    private fun findNestedScrollView(recyclerView: RecyclerView) : NestedScrollView? {
        var view = recyclerView.parent
        while (view != null) {
            if (view is NestedScrollView) {
                return view
            }
            view = view.parent
        }
        return null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        nestedScrollView = findNestedScrollView(recycler_view)
        itemDragAdapter = MyDraggerRecyclerAdapter(generateData(50))
        mItemDragAndSwipeCallback = object: ItemDragAndSwipeCallback(itemDragAdapter) {
            override fun onMoved(recyclerView: RecyclerView, source: RecyclerView.ViewHolder, fromPos: Int, target: RecyclerView.ViewHolder, toPos: Int, x: Int, y: Int) {
                super.onMoved(recyclerView, source, fromPos, target, toPos, x, y)
                ifNeedScrollToBottomOrTop(recyclerView!!, source!!, y)
            }
        }
        mItemTouchHelper = ItemTouchHelper(mItemDragAndSwipeCallback!!)
        mItemTouchHelper!!.attachToRecyclerView(recycler_view)
        itemDragAdapter!!.enableDragItem(mItemTouchHelper!!)
        itemDragAdapter!!.setOnItemDragListener(onItemDragListener)
        itemDragAdapter!!.setToggleDragOnLongPress(true)
        recycler_view.apply {
            layoutManager = LinearLayoutManager(activity)
            recycler_view.adapter = itemDragAdapter
        }
    }

    private fun generateData(size: Int): ArrayList<String> {
        val data = ArrayList<String>()
        for (i in 0 until size) {
            data.add("item $i")
        }
        return data
    }

    private var onItemDragListener: OnItemDragListener = object : OnItemDragListener {
        private var startPosition = 0

        override fun onItemDragStart(viewHolder: RecyclerView.ViewHolder, pos: Int) {
            startPosition = pos
            Log.d("Tab1Fragment", "drag start")
        }

        override fun onItemDragMoving(source: RecyclerView.ViewHolder, from: Int, target: RecyclerView.ViewHolder, to: Int) {
//            Log.d("Tab1Fragment", "onItemDragMoving")
        }

        override fun onItemDragEnd(viewHolder: RecyclerView.ViewHolder, endPosition: Int) {
            Log.d("Tab1Fragment", "onItemDragEnd")
        }
    }
}