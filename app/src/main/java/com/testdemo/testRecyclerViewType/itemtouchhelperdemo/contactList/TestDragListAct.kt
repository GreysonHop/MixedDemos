package com.testdemo.testRecyclerViewType.itemtouchhelperdemo.contactList

import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.recyclerview.widget.RecyclerView
import com.testdemo.BaseActivity

/**
 * Created by Greyson on 2021/02/25
 */
class TestDragListAct : BaseActivity() {

    private lateinit var userList: RecyclerView

    override fun getLayoutView(): View? {
        return userList.also {
            it.layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
        }
    }

    override fun initView() {

    }
}