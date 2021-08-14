package com.testdemo.testjava

import android.util.Log
import com.testdemo.BaseCommonActivity
import kotlinx.coroutines.*

/**
 * Create by Greyson on 2021/06/22
 */

class TestCoroutines : BaseCommonActivity(), CoroutineScope by MainScope() {
    init {
        TAG = "TestCoroutines"
    }

    override fun initView() {
        Log.d(TAG, "initView: init view, and get data...")

        launch {
            getData()
            Log.d(TAG, "initView: data return, set view")
        }

        Log.d(TAG, "initView: finish init")
    }

    private fun test() {

    }

    private suspend fun getData(): String {
        delay(3000)
        return "I'm the data!"
    }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }
}

