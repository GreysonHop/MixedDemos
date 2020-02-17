package com.testdemo.testStartMode

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.Window
import android.widget.Button
import android.widget.LinearLayout
import com.testdemo.BaseActivity
import com.testdemo.broken_lib.Utils
import android.os.Build
import android.transition.TransitionInflater
import android.view.View

import com.testdemo.R


/**
 * Create by Greyson
 */
class ActivityB : BaseActivity() {

    lateinit var button: Button

    val flagGroups = arrayOf(0
            , Intent.FLAG_ACTIVITY_SINGLE_TOP
            , Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            , Intent.FLAG_ACTIVITY_NEW_TASK
            , Intent.FLAG_ACTIVITY_CLEAR_TOP
            , -1)
    var flagIndex = 0

    override fun getLayoutView(): View? {
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL

        button = Button(this)
        button.text = "start A"
        button.textSize = Utils.dp2px(20).toFloat()
        layout.addView(button)

        val buttonFlag = Button(this)
        buttonFlag.text = "切换flag"
        buttonFlag.textSize = Utils.dp2px(18).toFloat()
        layout.addView(buttonFlag)
        buttonFlag.setOnClickListener {
            flagIndex++
            if (flagIndex >= flagGroups.size) {
                flagIndex = 0
            }
            Log.d("greyson", "当前FlagIndex=$flagIndex")
        }

        button.setOnClickListener {
            val intent = Intent(this, ActivityA::class.java)
            /*if (flagGroups[flagIndex] == -1) {
                intent.removeFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.removeFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            } else {*/
            intent.flags = flagGroups[flagIndex]
//            }
            startActivity(intent)
        }
        return layout
    }

    override fun initialize() {
        super.initialize()
        if (Build.VERSION.SDK_INT >= 21) {//ActivityOptions实现Activity动画
            window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
            val explode = TransitionInflater.from(this).inflateTransition(R.transition.explode)
            window.exitTransition = explode
            window.enterTransition = explode
            window.reenterTransition = explode
        }
    }

    override fun initView() {
        if (Build.VERSION.SDK_INT >= 21) {//ActivityOptions实现Activity动画
            button.transitionName = "test_transition"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.v("greyson", "ActivityB onCreate($savedInstanceState), object=${hashCode()}, taskId=$taskId")
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        Log.v("greyson", "ActivityB onCreate($savedInstanceState, $persistentState), object=${hashCode()}, taskId=$taskId")
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Log.v("greyson", "ActivityB onNewIntent($intent), object=${hashCode()}, taskId=$taskId")
    }

    override fun onResume() {
        super.onResume()
        Log.v("greyson", "ActivityB onResume(), object=${hashCode()}, taskId=$taskId")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.v("greyson", "ActivityB onDestroy(), object=${hashCode()}, taskId=$taskId")
    }
}