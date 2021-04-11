package com.testdemo.testjava

import android.annotation.SuppressLint
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeoutException
import kotlin.random.Random

/**
 * Create by Greyson
 */

fun main(args: Array<String>) {
    println("-------------主线程[${Thread.currentThread().name}] ${getCurrentTime()} 开始运行------------")
    val java = TestRxJava2()
//    java.testTotal()

    java.testArrayOneByOne()

    println("-------------主线程[${Thread.currentThread().name}] ${getCurrentTime()} 开始阻塞------------")
    Thread.sleep(12 * 1000) //模拟线程任务的时候主线程不能结束，所以得阻塞住
    println("--------------主线程 ${getCurrentTime()} 结束-----------")
}

fun getCurrentTime() : String {
    return SimpleDateFormat("mm:ss", Locale.CHINA).format(System.currentTimeMillis())
}

class TestRxJava2 {

    fun testTotal() {
        val intList = listOf(1, 2, 3, 5)
        val observable1 = Observable.just(2, 3)
        val observable2 = Observable.fromIterable(intList)
        //.subscribeOn(AndroidSchedulers.mainThread())//线程操作是跟一个流Observable的吗

        val threadOb = Observable.create<Int> {
            val int = Random.nextInt(5)
            println("greyson(${Thread.currentThread().name}), threadOb random: $int")
                Thread.sleep(int.toLong())
                it.onNext(8)
                it.onComplete()
        }
        val threadOb2 = Observable.create<Int> {
            val int = Random.nextInt(5)
            println("greyson(${Thread.currentThread().name}), threadOb2 random: $int")
                Thread.sleep(int.toLong())
                it.onNext(1)
                it.onNext(2)
                it.onNext(3)
                it.onComplete()
        }

        Observable.create<List<Int>> {
            println("first in ${SimpleDateFormat("mm:ss").format(System.currentTimeMillis())}")
            Thread.sleep(3*1000)
            it.onNext(listOf(1, 2, 3))
            it.onComplete()
        }
            .filter { it.isNotEmpty() }
            .flatMap { list ->
                Observable.fromIterable(list).withLatestFrom(getInfo(list),
                    BiFunction<Int, Map<Int, String>, String> { i1, i2 ->
                        println("biFunc: ${Thread.currentThread().name}")
                    "$i1 + ${i2[i1]}"
                }).subscribeOn(Schedulers.single())
                /*Observable.combineLatest(getInfo(list),Observable.fromIterable(list),
                    BiFunction<Int, Int, String> { i1, i2 ->
                        "$i1 + $i2"
                    })*/
            }
            .toList().toObservable()
            .subscribeOn(Schedulers.newThread())
            .observeOn(Schedulers.single())
            .subscribe {
                printG("测试结果", it)
            }

        /*Observable.combineLatest(threadOb2, threadOb, BiFunction<Int, Int, String> { l1, l2 ->
            println("BiFunction(${Thread.currentThread().name}): $l1-$l2")
            "$l1-$l2"
        })
//            .subscribeOn(Schedulers.newThread())
//            .observeOn(Schedulers.single())
            .subscribe(object : Observer<String> {
                override fun onSubscribe(d: Disposable) {}
                override fun onError(e: Throwable) {}

                override fun onNext(t: String) {
                    println("greyson(${Thread.currentThread().name}), onNext: $t")
                }

                override fun onComplete() {
                    println("greyson(${Thread.currentThread().name}), complete")
                }
            })*/
//            .subscribe(Consumer {  }, Consumer {  })

    }

    @SuppressLint("CheckResult")
    fun testArrayOneByOne() { //同时请求多个网站，只返回第一个请求成功的
        Observable.just(listOf("a", "b", "c", "d"))
                .flatMap { Observable.fromIterable(it) }
                .flatMap { id ->
                    val seconds = when (id) {
                        "a" -> 4
                        "b" -> -1
                        "c" -> 5
                        else -> 2
                    }
                    Observable.fromCallable { netReq(id, seconds) }
//                            .concatMapDelayError()
                            .doOnError { println("任务[${Thread.currentThread().name}]报错:${it.cause}, ") }
                            .onErrorResumeNext(Observable.empty())
                            .subscribeOn(Schedulers.io())
                }
                .toList()
                .subscribe { result ->
                    println("得到的结果：$result --线程[${Thread.currentThread().name}], 输出时间：${getCurrentTime()}")
                }

        /*Observable.defer{ Observable.just(netReq("ni")) }
         //直接Observable.just()接.subscribeOn()的话，不能切换线程！使用fromCallable()或defer()
                .subscribeOn(Schedulers.newThread())
                .subscribe { println("result=$it") }*/
    }

    fun getInfo(list: List<Int>) : Observable<Map<Int, String>> {
        val add: (List<Int>) -> Int = { it.sum() }
        val getMap: (List<Int>) -> Map<Int, String> = {
            Thread.sleep(2 * 1000)
            mapOf(Pair(2,"gan"), Pair(3,"hu"), Pair(1,"nong"))
        }

        return Observable.fromIterable(list)
            .map { it + 1 }
            .distinct()
            .toList()
            .flatMapObservable { list ->
                printG("getInfo's flatMap in ${SimpleDateFormat("mm:ss").format(System.currentTimeMillis())}", list)
                Observable.create<Map<Int, String>> {
                    println("info in ${SimpleDateFormat("mm:ss").format(System.currentTimeMillis())}")
                    it.onNext(getMap(list))
                    println("getMap in ${SimpleDateFormat("mm:ss").format(System.currentTimeMillis())}")
//                    it.onNext(add(list))
                    it.onComplete()
                }
            }
    }

    private fun printG(param: String, value: Any) {
        println("greyson(${Thread.currentThread().name}), $param: $value")
    }

    companion object {
        const val TIMEOUT_SECONDS = 5

        fun netReq(param: String, runSeconds: Int = 0): String {
            if (runSeconds < 0) throw IllegalArgumentException()

            val rspTime = if (runSeconds == 0) Random.nextInt(3, 8) else runSeconds //网络请求响应所花的时间（秒）

            for (i in 1..rspTime) {
                println("[$param]线程[${Thread.currentThread().name}]运行了第${i}秒")
                if (i == TIMEOUT_SECONDS) {
                    println("线程[${Thread.currentThread().name}]的网络访问超时了！")
                    throw TimeoutException("greyson warn：线程[${Thread.currentThread().name}]超时")
                }
                try {
                    Thread.sleep(500L)
                } catch (e: Exception) { }
            }

            return "[$param]线程[${Thread.currentThread().name}]花费${rspTime}秒返回" //网络请求返回数据
        }
    }
}