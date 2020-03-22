package com.testdemo.testNestedScroll

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment

/**
 * Create by Greyson on 2020/03/21
 */
abstract class BaseFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(getLayoutResId(), container, false)
        initView(view)
        initData()
        return view
    }

    @LayoutRes
    abstract fun getLayoutResId(): Int

    protected abstract fun initView(view: View)

    protected open fun initData() {}
}