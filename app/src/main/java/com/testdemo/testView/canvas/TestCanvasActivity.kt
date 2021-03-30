package com.testdemo.testView.canvas

import com.testdemo.BaseCommonActivity
import com.testdemo.R
import kotlinx.android.synthetic.main.act_test_canvas.*

class TestCanvasActivity : BaseCommonActivity() {

    override fun getLayoutResId(): Int {
        return R.layout.act_test_canvas
    }

    override fun initView() {
        lifecycle.addObserver(awv)
    }
}