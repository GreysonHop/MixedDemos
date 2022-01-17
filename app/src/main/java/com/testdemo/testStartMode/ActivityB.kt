package com.testdemo.testStartMode

import android.app.SharedElementCallback
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.Window
import android.widget.Button
import android.widget.LinearLayout
import com.testdemo.util.broken_lib.Utils
import android.os.Build
import android.transition.TransitionInflater
import android.view.View
import com.testdemo.BaseCommonActivity

import com.testdemo.R


/**
 * Create by Greyson
 */
class ActivityB : BaseCommonActivity() {

    lateinit var button: Button

    val flagGroups = arrayOf(0
            , Intent.FLAG_ACTIVITY_SINGLE_TOP
            , Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            , Intent.FLAG_ACTIVITY_NEW_TASK
            , Intent.FLAG_ACTIVITY_CLEAR_TOP
            , -1)
    val flagGroupNames= arrayOf("0", "SINGLE_TOP", "SINGLE_TOP|CLEAR_TOP", "NEW_TASK", "CLEAR_TOP", "-1")
    var flagIndex = 0

    override fun getLayoutView(): View? {
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL

        button = Button(this)
        button.text = "start A"
        button.textSize = Utils.dp2px(20).toFloat()
        button.transitionName = "test_transition" // ActivityOptions实现Activity动画
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
            Log.d(TAG, "当前FlagIndex=$flagIndex, 从该界面启动Activity时将使用flags：${flagGroupNames[flagIndex]}")
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

            setEnterSharedElementCallback(object: SharedElementCallback() {
                override fun onSharedElementEnd(sharedElementNames: MutableList<String>?,
                         sharedElements: MutableList<View>?, sharedElementSnapshots: MutableList<View>?) {
                    super.onSharedElementEnd(sharedElementNames, sharedElements, sharedElementSnapshots)
                    Log.d(TAG, "EnterSharedElementCallback -- onSharedElementEnd: 结束了！")
                }
            })

            window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS) // 网上的博客说该标志必须使用，自己没验证过
            val explode = TransitionInflater.from(this).inflateTransition(R.transition.explode)
            window.exitTransition = explode
            window.enterTransition = explode
            window.reenterTransition = explode
            // 如果不像上面手动去设置window的transition动画的话，下面的代码取出来的transition动画都将为Null
            Log.d(TAG, "enter=${window.enterTransition} enter=${window.sharedElementEnterTransition}")
        }
    }

    override fun initView() {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.v(TAG, "ActivityB onCreate($savedInstanceState), object=${hashCode()}, taskId=$taskId")
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        Log.v(TAG, "ActivityB onCreate($savedInstanceState, $persistentState), object=${hashCode()}, taskId=$taskId")
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Log.v(TAG, "ActivityB onNewIntent($intent), object=${hashCode()}, taskId=$taskId")
    }

    override fun onResume() {
        super.onResume()
        Log.v(TAG, "ActivityB onResume(), object=${hashCode()}, taskId=$taskId")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.v(TAG, "ActivityB onDestroy(), object=${hashCode()}, taskId=$taskId")
    }
}