package com.testdemo.testRecyclerViewType.itemtouchhelperdemo.contactList

import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.recyclerview.widget.*
import com.testdemo.BaseActivity
import com.testdemo.testRecyclerViewType.itemtouchhelperdemo.helper.ItemDragHelperCallback

/**
 * Created by Greyson on 2021/02/25
 */
class TestDragListAct : BaseActivity() {

    // private lateinit var userList: RecyclerView

    override fun getLayoutView(): View? {
        return RecyclerView(this).apply {
            layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
            val items = ArrayList<String>()
            for (index in 0..200) {
                items.add("${index}.item")
            }
            layoutManager = GridLayoutManager(this@TestDragListAct, 5)

            val callback: ItemDragHelperCallback = object : ItemDragHelperCallback() {
                override fun isLongPressDragEnabled(): Boolean {
                    // 长按拖拽打开
                    return true
                }
            }
            val helper = ItemTouchHelper(callback)
            helper.attachToRecyclerView(this)
            adapter = ContactsAdapter(this@TestDragListAct, items)

            MyPagerSnapHelper().attachToRecyclerView(this)
        }
    }

    override fun initView() {

    }
}