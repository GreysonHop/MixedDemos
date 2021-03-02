package com.testdemo.testRecyclerViewType.itemtouchhelperdemo.contactList

import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.recyclerview.widget.*
import com.testdemo.BaseActivity
import com.testdemo.testRecyclerViewType.gridpagersanphelper.ScreenUtils
import com.testdemo.testRecyclerViewType.gridpagersanphelper.gridpagersnaphelper.GridPagerSnapHelper
import com.testdemo.testRecyclerViewType.itemtouchhelperdemo.helper.ItemDragHelperCallback

/**
 * Created by Greyson on 2021/02/25
 */
class TestDragListAct : BaseActivity() {

    override fun getLayoutView(): View? {
        return RecyclerView(this).apply {

            layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, ScreenUtils.dip2px(this@TestDragListAct, 260f))
            val items = ArrayList<String>()
            for (index in 0..200) {
                items.add("${index}.item")
            }
            layoutManager = GridLayoutManager(this@TestDragListAct, 5)

            val callback: ItemDragHelperCallback = object : ItemDragHelperCallback() {
                override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                    return super.onMove(recyclerView, viewHolder, target)
                }

                override fun isLongPressDragEnabled(): Boolean {
                    // 长按拖拽打开
                    return true
                }
            }
            val helper = ItemTouchHelper(callback)
            helper.attachToRecyclerView(this)
            adapter = ContactsAdapter(this@TestDragListAct, items)

            // MyPagerSnapHelper().attachToRecyclerView(this)
            GridPagerSnapHelper().setRow(5).setColumn(5).attachToRecyclerView(this)
        }
    }

    override fun initView() {

    }
}