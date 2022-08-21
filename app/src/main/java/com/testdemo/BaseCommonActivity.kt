package com.testdemo

import android.annotation.SuppressLint
import android.view.View
import androidx.annotation.LayoutRes

/**
 * Create by Greyson
 */
@SuppressLint("Registered")
abstract class BaseCommonActivity : BaseActivity() {

    /**
     * 如果是当前类的子类，请实现[getLayoutResId]或[getLayoutView]来设置当前Activity的内容视图content view
     */
    final override fun setContentView() {
        if (getLayoutResId() != 0) {
            setContentView(getLayoutResId())
        } else if (getLayoutView() != null) {
            setContentView(getLayoutView())
        }
    }

    /**
     * 实现类可重写此方法，返回一个布局的资源ID，用于当前Activity的setContentView()。此方法与[getLayoutView]
     * 方法实现其中一个即可，前者优先级高。
     */
    @LayoutRes
    protected open fun getLayoutResId(): Int { //todo greyson 有没有注解可以标明两个方法中必须有一个被调用
        return 0
    }

    /**
     * 实现类可重写此方法，返回一个View，用于当前Activity的setContentView()。此方法与[getLayoutResId]
     * 方法实现其中一个即可，后者优先级高。
     */
    protected open fun getLayoutView(): View? {
        return null
    }

}