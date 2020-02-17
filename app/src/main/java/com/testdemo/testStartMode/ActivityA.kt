package com.testdemo.testStartMode

import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import com.testdemo.BaseActivity
import com.testdemo.broken_lib.Utils

/**
 * Create by Greyson
 */
class ActivityA : BaseActivity() {

    lateinit var textView: TextView

    override fun getLayoutView(): View? {
        textView = TextView(this)
        textView.text = "start B"
        textView.textSize = Utils.dp2px(20).toFloat()
        textView.gravity = Gravity.CENTER

        textView.setOnClickListener {
            startActivity(Intent(this, ActivityB::class.java)
                    , ActivityOptionsCompat.makeSceneTransitionAnimation(this, textView, "test_transition").toBundle())
        }
        return textView
    }

    override fun initView() {
        if (Build.VERSION.SDK_INT >= 21) {//ActivityOptions实现Activity动画
            textView.transitionName = "test_transition"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.v("greyson", "ActivityA onCreate($savedInstanceState), object=${hashCode()}, taskId=$taskId")
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        Log.v("greyson", "ActivityA onCreate($savedInstanceState, $persistentState), object=${hashCode()}, taskId=$taskId")
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Log.v("greyson", "ActivityA onNewIntent($intent), object=${hashCode()}, taskId=$taskId")
    }

    override fun onResume() {
        super.onResume()
        Log.v("greyson", "ActivityA onResume(), object=${hashCode()}, taskId=$taskId")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.v("greyson", "ActivityA onDestroy(), object=${hashCode()}, taskId=$taskId")
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        Log.d("greyson", "ActivityA onConfigurationChanged: ${newConfig}")
    }
}