package com.testdemo.testStartMode

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.testdemo.broken_lib.Utils

/**
 * Create by Greyson
 */
class ActivityA : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.v("greyson", "ActivityA onCreate($savedInstanceState), object=${hashCode()}, taskId=$taskId")
        val textView = TextView(this)
        textView.text = "start B"
        textView.textSize = Utils.dp2px(20).toFloat()
        setContentView(textView)

        textView.setOnClickListener {
            startActivity(Intent(this, ActivityB::class.java))
        }
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
}