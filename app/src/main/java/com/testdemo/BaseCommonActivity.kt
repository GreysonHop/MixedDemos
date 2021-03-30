package com.testdemo

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.AnimRes
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity

/**
 * Create by Greyson
 */
@SuppressLint("Registered")
abstract class BaseCommonActivity : BaseActivity() {

    final override fun setContentView() {
        if (getLayoutResId() != 0) {
            setContentView(getLayoutResId())
        } else if (getLayoutView() != null) {
            setContentView(getLayoutView())
        }
    }

    @LayoutRes
    protected open fun getLayoutResId(): Int {//todo greyson 有没有注解可以标明两个方法中必须有一个被调用
        return 0
    }

    protected open fun getLayoutView(): View? {
        return null
    }

}