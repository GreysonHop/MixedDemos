package com.testdemo.testNestedScroll.raw

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.testdemo.BaseCommonActivity
import com.testdemo.R
import com.testdemo.util.CommonTextAdapter
import com.testdemo.util.CommonImgAdapter

/**
 * Create by Greyson on 2020/03/21
 */
class CollapsableScrollAct : BaseCommonActivity() {

    private lateinit var rvVertical: RecyclerView
    private lateinit var rvHorizon: RecyclerView

    override fun getLayoutResId(): Int {
        return R.layout.act_collapsable_scroll_backup
    }

    override fun initView() {
        rvVertical = findViewById(R.id.rv_vertical)
        rvHorizon = findViewById(R.id.rv_horizontal)

        val layoutManagerV = LinearLayoutManager(this)
        layoutManagerV.orientation = LinearLayoutManager.VERTICAL
        rvVertical.layoutManager = layoutManagerV

        val listData = ArrayList<String>()
        for (i in 0..27) {
            listData.add("item$i")
        }
        rvVertical.adapter = CommonTextAdapter(listData)


        val listData2 = ArrayList<String>()
        for (i in 0..5) {
            listData2.add("image")
        }
        val layoutManagerH = CustomLayoutManager(this)
        layoutManagerH.setScrollMode(CustomLayoutManager.SCROLL_ENABLE_VERTICAL)
        layoutManagerH.orientation = LinearLayoutManager.HORIZONTAL
        rvHorizon.layoutManager = layoutManagerH
        rvHorizon.adapter = CommonImgAdapter(listData2)
    }
}
