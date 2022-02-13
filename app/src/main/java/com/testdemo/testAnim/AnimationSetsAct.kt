package com.testdemo.testAnim

import android.view.View
import android.widget.FrameLayout
import com.testdemo.BaseCommonActivity
import com.testdemo.R

/**
 * Create by Greyson on 2022/02/14
 */
class AnimationSetsAct : BaseCommonActivity() {

    override fun getLayoutView(): View? {

        return FrameLayout(this).also {
            it.id = R.id.anim_sets_frag
        }
    }

    override fun initView() {
        supportFragmentManager
            .beginTransaction()
            .add(R.id.anim_sets_frag, AnimationSetsFrag())
            .commit()
    }
}