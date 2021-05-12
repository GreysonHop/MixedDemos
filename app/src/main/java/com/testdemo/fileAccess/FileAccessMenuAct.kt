package com.testdemo.fileAccess

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.testdemo.BaseCommonActivity
import com.testdemo.testCenterRefresh.MyAdapter

/**
 * Create by Greyson on 2021/05/13
 */
class FileAccessMenuAct : BaseCommonActivity() {
    lateinit var actList:RecyclerView

    override fun getLayoutView(): View? {
        actList = RecyclerView(this)
        return actList
    }

    override fun initView() {
        actList.apply {
            layoutManager = LinearLayoutManager(this@FileAccessMenuAct)
            actList.adapter = MyAdapter(arrayListOf("hi", "you"))
        }
    }


}