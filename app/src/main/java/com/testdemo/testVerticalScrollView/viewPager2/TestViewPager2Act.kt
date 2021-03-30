package com.testdemo.testVerticalScrollView.viewPager2

import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.testdemo.BaseCommonActivity

/**
 * Create by Greyson
 */
class TestViewPager2Act : BaseCommonActivity() {

    lateinit var viewpager2 : ViewPager2

    override fun getLayoutView(): View? {
        viewpager2 = ViewPager2(this)
        viewpager2.orientation = ViewPager2.ORIENTATION_VERTICAL
        return viewpager2
    }

    override fun initView() {
        val myAdapter = ViewPager2Adapter(/*arrayListOf("哈哈", "呼哈", "干")*/)
        viewpager2.adapter = myAdapter
    }
}