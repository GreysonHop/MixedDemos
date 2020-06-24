package com.testdemo.architecture

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.testdemo.R

/**
 * Created by Greyson on 2020/06/24
 */
class SaveStateAct : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_save_state)
        Log.e("greyson", "SaveStateAct-onCreate: $savedInstanceState, task= $taskId")
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        Log.e("greyson", "SaveStateAct-onRestoreInstanceState: $savedInstanceState")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.e("greyson", "SaveStateAct-onSaveInstanceState: $outState")
    }


    fun onClickFinish(view: View) {
        finish()
    }

    override fun finish() {
//        overridePendingTransition(0, 0)
        super.finish()
    }
}