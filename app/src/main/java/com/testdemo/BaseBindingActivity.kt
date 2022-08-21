package com.testdemo

import androidx.viewbinding.ViewBinding

/**
 * Create by Greyson
 */
abstract class BaseBindingActivity<T : ViewBinding> : BaseActivity() {
    protected lateinit var binding: T

    /**
     * 如果是当前类的子类，请实现[getViewBinding]来设置当前Activity的内容视图content view
     */
    final override fun setContentView() {
        binding = getViewBinding()
        setContentView(binding.root)
    }

    protected abstract fun getViewBinding() : T
}