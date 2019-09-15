package com.testdemo

import android.app.Activity
import android.app.Application
import android.net.http.HttpResponseCache
import java.io.File
import java.io.IOException

/**
 * Create by Greyson on 2019/09/15
 */
class TestApplication : Application() {
    var activity: Activity? = null

    override fun onCreate() {
        super.onCreate()

        //set for SVGA
        val dir = applicationContext.cacheDir
        val cacheDir = File(dir, "http")
        try {
            HttpResponseCache.install(cacheDir, 1024 * 1024 * 128)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}