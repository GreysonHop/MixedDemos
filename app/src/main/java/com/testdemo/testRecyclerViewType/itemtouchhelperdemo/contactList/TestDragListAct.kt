package com.testdemo.testRecyclerViewType.itemtouchhelperdemo.contactList

import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import androidx.recyclerview.widget.*
import com.testdemo.BaseActivity
import com.testdemo.R
import com.testdemo.testRecyclerViewType.gridpagersanphelper.ScreenUtils
import com.testdemo.testRecyclerViewType.gridpagersanphelper.gridpagersnaphelper.GridPagerSnapHelper
import com.testdemo.testRecyclerViewType.itemtouchhelperdemo.helper.ItemDragHelperCallback
import kotlinx.android.synthetic.main.act_test_drag_list.*

/**
 * Created by Greyson on 2021/02/25
 */
class TestDragListAct : BaseActivity() {

    override fun getLayoutResId(): Int {
        return R.layout.act_test_drag_list
    }

    override fun initView() {
        val items = ArrayList<String>()
        for (index in 0..150) {
            items.add("${index}.item")
        }
        rv_userList.layoutManager = GridLayoutManager(this, 5)

        val callback: ItemDragHelperCallback = object : ItemDragHelperCallback() {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                val movedView = viewHolder.itemView
                val targetView = target.itemView
                Log.d("greyson", "onMove() ${movedView.x}-${movedView.y}, ${targetView.x}-${targetView.y}")
                return super.onMove(recyclerView, viewHolder, target)
            }

            override fun isLongPressDragEnabled(): Boolean {
                // 长按拖拽打开
                return true
            }
        }
        val helper = ItemTouchHelper(callback)
        helper.attachToRecyclerView(rv_userList)
        rv_userList.adapter = ContactsAdapter(this, items)

        // MyPagerSnapHelper().attachToRecyclerView(this)
        // GridPagerSnapHelper().setRow(5).setColumn(5).attachToRecyclerView(rv_userList)

        var oneLineHeight = 50
        rv_userList.layoutManager?.apply {
            val child = getChildAt(0)
            if (child != null) {
                oneLineHeight = getDecoratedMeasuredHeight(child)
            }
        }

        var isCollapse = false
        iv_collapse.setOnClickListener {
            rv_userList.height
            if (isCollapse) {
                rv_userList.startAnimation(
                    ScaleAnimation(1f, 1f, 0.1f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0f).apply {
                        duration = 1500
                    }
                )
            } else {
                if (oneLineHeight != 0) {
                    rv_userList.startAnimation(
                        ScaleAnimation(1f, 1f, 1f, 0.1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0f).apply {
                            duration = 1500
                        }
                    )
                }
            }
            isCollapse = !isCollapse
        }
    }
}