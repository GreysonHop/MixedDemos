package com.testdemo

import androidx.viewbinding.ViewBinding

/**
 * Create by Greyson
 */
abstract class BaseBindingActivity<T : ViewBinding> : BaseActivity() {
    protected lateinit var binding: T

    final override fun setContentView() {
        binding = getViewBinding()
    }

    protected abstract fun getViewBinding() : T
}