package com.testdemo.testNestedScroll.raw

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.testdemo.BaseCommonActivity
import com.testdemo.R
import com.testdemo.testCenterRefresh.MyAdapter
import com.testdemo.testNestedScroll.behavior.MyImgAdapter

/**
 * Create by Greyson on 2020/03/21
 */
class CollapsableScrollAct : BaseCommonActivity() {

    private lateinit var rvVertical: RecyclerView
    private lateinit var rvHorizon: RecyclerView

    override fun getLayoutResId(): Int {
        return R.layout.act_collapsable_scroll
    }

    override fun initView() {
        rvVertical = findViewById(R.id.rv_vertical)
        rvHorizon = findViewById(R.id.rv_horizontal)

        val layoutManagerV = LinearLayoutManager(this)
        layoutManagerV.orientation = LinearLayoutManager.VERTICAL
        rvVertical.layoutManager = layoutManagerV

        val listData = ArrayList<String>()
        for (i in 0..17) {
            listData.add("item$i")
        }
        rvVertical.adapter = MyAdapter(listData)


        val listData2 = ArrayList<String>()
        for (i in 0..5) {
            listData2.add("image")
        }
        val layoutManagerH = LinearLayoutManager(this)
        layoutManagerH.orientation = LinearLayoutManager.HORIZONTAL
        rvHorizon.layoutManager = layoutManagerH
        rvHorizon.adapter = MyImgAdapter(listData2)
    }
}
