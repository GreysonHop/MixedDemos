package com.testdemo

import android.app.Activity
import android.app.Application
import android.net.http.HttpResponseCache
import android.util.Log
import com.squareup.leakcanary.LeakCanary
import com.wonderkiln.blurkit.BlurKit
import io.realm.Realm
import java.io.File
import java.io.IOException

/**
 * Create by Greyson on 2019/09/15
 */
class TestApplication : Application() {
    var activity: Activity? = null

    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
        BlurKit.init(this)

        if (LeakCanary.isInAnalyzerProcess(this)) {
            return
        }
        LeakCanary.install(this)

        //set for SVGA
        val dir = applicationContext.cacheDir
        val cacheDir = File(dir, "http")
        try {
            HttpResponseCache.install(cacheDir, 1024 * 1024 * 128)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    //TODO greyson_1/6/21 如何监听应用被杀掉
    override fun onTerminate() {
        super.onTerminate()
        Log.w("TestApplication", "onTerminate: 应用关闭")
    }
}