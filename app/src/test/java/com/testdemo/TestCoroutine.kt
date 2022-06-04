package com.testdemo

import kotlinx.coroutines.*
import org.junit.Test

/**
 * Created by Greyson on 2021/11/18
 */
class TestCoroutine {

    @Test
    fun test1() = runBlocking { // 会阻塞线程来等待里面的协程都完成
        launch {
            delay(200L)
            println("Task from runBlocking")
        }

        coroutineScope { // 创建一个协程作用域。会挂起、等待所有子协程结束
            launch {
                delay(500L)
                println("Task from nested launch")
            }

            delay(100L)
            println("Task from coroutine scope") // 这一行会在内嵌 launch 之前输出
        }

        println("Coroutine scope is over") // 这一行在内嵌 launch 执行完毕后才输出
    }

    @Test
    fun testTerminal1() = runBlocking {
        GlobalScope.launch { // runBlocking不会等它跑完才结束
            repeat(10) { i ->
                println("GlobalScope: I'm sleeping $i ...")
                delay(500L)
            }
        }
        delay(1300L) // 延迟一段时间
        println("testTerminal1: I'm tired of waiting!")
        println("testTerminal1: Now I can quit. And the GlobalScope will ends right now!")
    }

    @Test
    fun testTerminal2() = runBlocking {
        val job = launch {
            repeat(10) { i ->
                println("job: I'm sleeping $i ...")
                delay(500L)
            }
        }
        delay(1300L) // 延迟一段时间
        println("testTerminal2: I'm tired of waiting!")
        job.cancel() // 取消该作业。如果没有此句，job协程会输出10个结果。
        job.join() // 等待作业执行结束
        println("testTerminal2: Now I can quit.")
    }

    fun testWithContext() = runBlocking {
        withContext(Dispatchers.IO) {

        }
    }
}
