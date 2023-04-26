package testJava

import android.util.Log
import com.google.android.material.internal.FlowLayout
import com.testdemo.BaseCommonActivity
import kotlinx.coroutines.*

/**
 * Create by Greyson on 2021/06/22
 */

class TestCoroutinesInActivity : BaseCommonActivity(), CoroutineScope by MainScope() {
    init {
        TAG = "TestCoroutines"
    }

    override fun initView() {
        // FlowLayout
        Log.d(TAG, "initView: start.")

        launch() {
            val data = withContext(Dispatchers.IO) {
                Log.d(TAG, "切换上下文")
                getData()
            }
            Log.d(TAG, "initView: network data($data) return, set view")
        }

        Log.d(TAG, "initView: finish.")
    }

    private fun test() {

    }

    private suspend fun getData(): String {
        Log.d(TAG, "进入getData()")

        for (i in 0..5) {
            val random = Math.random()
            val sec = random.toInt()
            Log.d(TAG, "模拟IO操作阻塞，阻塞秒数：$sec，random=$random,")
            Thread.sleep(sec * 1000L)
        }

        delay(3000)
        Log.d(TAG, "getData()即将退出!")
        return "I'm the data!"
    }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }
}

