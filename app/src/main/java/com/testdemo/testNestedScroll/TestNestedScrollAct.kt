package com.testdemo.testNestedScroll

import androidx.fragment.app.Fragment
import com.testdemo.BaseActivity
import com.testdemo.R
import kotlinx.android.synthetic.main.act_test_nestedscroll.*
import java.util.ArrayList

/**
 * Create by Greyson on 2020/03/21
 */
class TestNestedScrollAct : BaseActivity() {

    override fun getLayoutResId(): Int {
        return R.layout.act_test_nestedscroll
    }

    override fun initView() {
        val fragmentList = ArrayList<Fragment>()
//        vp_main.adapter = PageAdapter(supportFragmentManager, fragmentList)
    }
}
