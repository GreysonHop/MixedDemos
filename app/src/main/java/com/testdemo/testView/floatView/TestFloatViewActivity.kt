package com.testdemo.testView.floatView

import android.content.Context
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.testdemo.BaseCommonActivity
import com.testdemo.R


/**
 * Create by Greyson on 2022/06/30
 */
class TestFloatViewActivity : BaseCommonActivity() {

    private val floatView by lazy {
        TextView(this).apply {
            text = "悬浮的View"
            setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.call_icon_gift, 0, 0)
            // layoutParams = Winl
        }
    }

    override fun getLayoutView(): View {
        return LinearLayout(this@TestFloatViewActivity).also {
            it.orientation = LinearLayout.VERTICAL
            it.addView(Button(this).apply {
                text = "打开悬浮窗"
                setOnClickListener {
                    val windowManager =
                        applicationContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                    val layoutParams = WindowManager.LayoutParams()
                    layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                    layoutParams.token = window.decorView.windowToken // 必须要

                    windowManager.addView(floatView, layoutParams)
                }
            })

            it.addView(Button(this).apply {
                text = "关闭悬浮窗"
                setOnClickListener {
                    windowManager.removeView(floatView)
                }
            })
        }
    }

    override fun initView() {
    }
}