package com.testdemo.testjava

import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import kotlin.random.Random

/**
 * Create by Greyson
 */

fun main(args: Array<String>) {
    val java = TestRxJava2()
    java.test()
    /*println("----------------")
    java.test()
    println("----------------")
    java.test()*/

    Thread.sleep(10 * 1000)
}

class TestRxJava2 {

    fun test() {
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
}