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

    // 测试协程的取消
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

    @Test
    fun `test supervisorScope2`() = runBlocking {
        try {
            supervisorScope {
                val child = launch {
                    try {
                        println("The child is sleeping")
                        delay(Long.MAX_VALUE)
                    } finally {
                        println("The child is cancelled")
                    }
                }
                yield()
                println("Throwing an exception from the scope")
                throw AssertionError()
            }
        } catch (e: AssertionError) {
            println("Caught an assertion error")
        }
    }

    /**
     * 测试父协程和子协程的关系
     */
    @Test
    fun testParentChildCoroutine() {
//        MainScope().launch {
        CoroutineScope(Dispatchers.IO).launch { // 测试没有 runBlocking 的协程是怎么结束的

            println("刚进入父协程")

            launch { // 子协程
                println("刚进入子协程")
                delay(1000)
                println("子协程运行完了")
            }

            println("父协程结束了！")

        }

        println("主函数结束。")

    }

    /**
     * 测试父协程会等待所有子协程完成任务之后再关闭的关系。也验证一下join()的作用
     */
    @Test
    fun testParentChildCoroutine2() = runBlocking {
        val parentJob = launch { // 父协程

            println("刚进入父协程（${this.coroutineContext[Job]}）---${Thread.currentThread()}")

            launch { // 子协程1
                println("刚进入子协程1（${this.coroutineContext[Job]}）---${Thread.currentThread()}")
                delay(100)
                println("子协程1运行完了")
            }

            launch { // 子协程2
                println("刚进入子协程2（${coroutineContext[Job]}）---${Thread.currentThread()}")
                delay(130)
                println("子协程2运行完了")
            }

            delay(50)
            println("父协程结束了！")
        }

        // join() 会挂起协程，直到父协程的 job 全部完成，再继续执行后续代码。所以这里用这句来判断父协程什么时候结束，
        // 以此看出父协程会等待所有子协程结束的情况！
        parentJob.join() // 这句只会影响下面打印语句的执行顺序，不会有其它影响！
        println("主函数结束！---${this.coroutineContext[Job]} --- ${this.coroutineContext[CoroutineName]}）---${Thread.currentThread()}") // 如果没有上面的join，这里会直接调用。不会等到父子协程都结束之后再调
    }

    // 测试父协程和子协程的执行顺序。（真的都是父协程执行完后才会执行子协程吗？）
    @Test
    fun testParentChildCoroutine3() = runBlocking {

        val parentJob = launch {
            // TODO: 可能是执行的时间太短，导致日志看起来像是像父协程先执行完，再执行子协程。想个办法让协程里的每个循环都比较耗时
            launch {
                for (i in 1..1000) {
                    println("testParentChildCoroutine3, launch2: $i (${TimeUtils.getCurTimeVisual()})")
                } // 输出结果来看，嵌套在最里面的子协程，将是最后被执行的。
            }

            for (i in 1..1000) {
                // if (i == 4) yield() // 这句代码执行之后，launch3会让出执行权，launch2执行完才返回来继续执行launch3。
                if (i == 5) delay(1)

                println("testParentChildCoroutine3, launch3: $i (${TimeUtils.getCurTimeVisual()})")
            }
        }

        for (i in 1..1000) {
            if (i == 10) delay(1)

            println("testParentChildCoroutine3, launch1: $i (${TimeUtils.getCurTimeVisual()})")
        }

        println("testParentChildCoroutine3, Over?!")
    }

    // 测试并发
    @Test
    fun testConcurrent() = runBlocking {
        val startTime = System.currentTimeMillis()
        val value1 = async {
            delay(250)
            2
        }

        val value2 = async {
            delay(200)
            3
        }
        val value3 = value1.await() + value2.await()
        val endTime = System.currentTimeMillis()
        print("两个值加起来等于：$value3。总花费时间：${endTime - startTime}")
    }

}
