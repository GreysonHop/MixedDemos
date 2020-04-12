package com.testdemo.testjava;

import android.annotation.SuppressLint;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;

/**
 * Create by Greyson on 2020/04/10
 */
@SuppressLint("CheckResult")
public class TestRxJava2 {

    public static void main(String[] args) {
        Observable.fromArray("guishen", "sheng")
                .flatMap(s -> {
                    if (s.startsWith("gui")) {
                        return Observable.just("shenshen");
                    } else {
                        return Observable.just(s);
                    }
                })
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String s) {
                        System.out.println("mytest: " + s);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void test() {
        ArrayList<String> list = new ArrayList<>();
        list.add("me");
        list.add("you");
        Observable.just(list)
                //从这里开始一个个执行
                .flatMap(urls -> Observable.fromIterable(urls))
                //还是从这里开始一个个执行的？
                .flatMap(url -> Observable.just(url+".1"))
                .filter(title -> title != null)
                .subscribe(title -> System.out.println(title));
    }
}
