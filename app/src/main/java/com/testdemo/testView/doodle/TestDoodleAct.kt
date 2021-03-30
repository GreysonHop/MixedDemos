package com.testdemo.testView.doodle

import android.view.View
import android.view.ViewGroup
import com.testdemo.BaseCommonActivity
import com.testdemo.R

/**
 * Create by Greyson on 2021/01/06
 */
class TestDoodleAct : BaseCommonActivity() {

    override fun getLayoutResId(): Int {
        return R.layout.test_doodle
    }

    /*override fun getLayoutView(): View? {
        return IMView(this).apply {
            val params = layoutParams?.apply {
                height = ViewGroup.LayoutParams.MATCH_PARENT
                width = ViewGroup.LayoutParams.MATCH_PARENT
            }
                    ?: ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

            layoutParams = params
        }
    }*/

    override fun initView() {}
}