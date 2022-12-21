package com.testdemo

import android.app.Activity
import android.app.Application
import android.net.http.HttpResponseCache
import android.os.Build
import android.util.Log
import com.wonderkiln.blurkit.BlurKit
import io.realm.Realm
import java.io.File
import java.io.IOException
import java.util.*

/**
 * Create by Greyson on 2019/09/15
 */
class TestApplication : Application() {
    var activity: Activity? = null

    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
        BlurKit.init(this)

        //set for SVGA
        val dir = applicationContext.cacheDir
        val cacheDir = File(dir, "http")
        try {
            HttpResponseCache.install(cacheDir, 1024 * 1024 * 128)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        /**
         * 对于7.0以下，需要在Application创建的时候进行语言切换
         */
        val language = getSharedPreferences(SP_COMMON, MODE_PRIVATE).getString(SP_KEY_LANGUAGE, "")
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            val resources = resources
            val configuration = resources.configuration
            //获取想要切换的语言类型
            val locale = Locale.JAPANESE //(language) //TODO greyson_6/4/21 先写死
            configuration.setLocale(locale)
            // updateConfiguration
            val dm = resources.displayMetrics
            resources.updateConfiguration(configuration, dm)
        }
    }

    //TODO greyson_1/6/21 如何监听应用被杀掉
    override fun onTerminate() {
        super.onTerminate()
        Log.w("TestApplication", "onTerminate: 应用关闭")
    }
}