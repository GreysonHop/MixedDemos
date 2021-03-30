package com.testdemo

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import androidx.annotation.AnimRes
import androidx.appcompat.app.AppCompatActivity

/**
 * Create by Greyson
 */
@SuppressLint("Registered")
abstract class BaseActivity : AppCompatActivity() {
    @AnimRes
    private var openEnterAnim = 0
    @AnimRes
    private var openExitAnim = 0
    @AnimRes
    private var closeEnterAnim = 0
    @AnimRes
    private var closeExitAnim = 0

    /**
     * 取消默认的requestedOrientation
     */
    protected var disableDefaultOrientation = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("greyson", "BaseActivity onCreate()__当前类名：${javaClass.simpleName}")

        initialize()
        if (!disableDefaultOrientation) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }

        setContentView()
        initView()
        initData()

        if (openEnterAnim != 0 && openExitAnim != 0) {
            overridePendingTransition(openEnterAnim, openExitAnim)
        }
    }

    /**
     * 设置视图前的初始化，如设置进场动画和状态栏等
     */
    protected open fun initialize() {}

    protected abstract fun setContentView()

    protected abstract fun initView()

    protected open fun initData() {}

    override fun finish() {
        super.finish()
        if (closeEnterAnim != 0 && closeExitAnim != 0) {
            overridePendingTransition(closeEnterAnim, closeExitAnim)
        }
    }


    /**
     * 设置当前Activity打开时的进场动画和上一个Activity的退场动画，必须两个都设置才有效。
     * 假如有一个值小于或等于0，则不执行任何指定动画。
     * 在
     * @param enterAnim
     * @param exitAnim
     */
    fun setOpenAnim(@AnimRes enterAnim: Int, @AnimRes exitAnim: Int) {
        openEnterAnim = enterAnim
        openExitAnim = exitAnim
    }

    /**
     * 设置当前Activity关闭时的退场动画和上一个Activity的进场动画，必须两个都设置才有效。
     * 假如有一个值小于或等于0，则不执行任何指定动画。
     * 在
     * @param enterAnim
     * @param exitAnim
     */
    fun setCloseAnim(@AnimRes enterAnim: Int, @AnimRes exitAnim: Int) {
        closeEnterAnim = enterAnim
        closeExitAnim = exitAnim
    }

    inline fun <reified T> go() {
        startActivity(Intent(this, T::class.java))
    }

    inline fun <reified T> go(bundle: Bundle) {
        startActivity(Intent(this, T::class.java).putExtras(bundle))
    }
}