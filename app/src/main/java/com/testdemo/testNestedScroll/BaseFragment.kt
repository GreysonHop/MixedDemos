package com.testdemo.testNestedScroll

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenStarted
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Create by Greyson on 2020/03/21
 */
abstract class BaseFragment : Fragment() {

    init { // Notice that we can safely launch in the constructor of the Fragment.
        lifecycleScope.launch {
            whenStarted {
                // The block inside will run only when Lifecycle is at least STARTED.
                // It will start executing when fragment is started and
                // can call other suspend methods.
                //loadingView.visibility = View.VISIBLE
                val canAccess = withContext(Dispatchers.IO) {
                    //checkUserAccess()
                    false
                }

                // When checkUserAccess returns, the next line is automatically
                // suspended if the Lifecycle is not *at least* STARTED.
                // We could safely run fragment transactions because we know the
                // code won't run unless the lifecycle is at least STARTED.
                // greyson_7/22/20 为什么在协程中可以直接更新UI？ 2022/4/4 答：因为协程在哪里启动，它就是在哪个线程中运行的，
                // 说到底kotlin在JVM中的协程只是线程切换框架（至于其它语言的协程不知是什么情况）
                //loadingView.visibility = View.GONE
                if (canAccess == false) {
                    if (lifecycle.currentState > Lifecycle.State.STARTED) {}
                    //findNavController().popBackStack()
                } else {
                    //showContent()
                }
            }

            // This line runs only after the whenStarted block above has completed.

        }
    }

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