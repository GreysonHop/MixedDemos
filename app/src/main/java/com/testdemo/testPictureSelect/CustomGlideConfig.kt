package com.testdemo.testPictureSelect

import android.content.Context
import android.os.Environment
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.engine.cache.DiskLruCacheWrapper
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.stream.HttpGlideUrlLoader
import com.bumptech.glide.load.model.stream.UrlLoader
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.module.GlideModule
import okhttp3.OkHttpClient
import java.io.File
import java.io.InputStream
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager

/**
 * Created by Greyson on 2021/05/11
 * 修改 Glide 的缓存路径
 * 使用方式：
 * <pre>
 * <meta-data
    android:name="包名.GiphyGlideModule"
    android:value="GlideModule" />
 </pre>
 */
class CustomGlideConfig : GlideModule {

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        //设置图片的显示格式ARGB_8888(指图片大小为32bit)
        // builder.setDecodeFormat(DecodeFormat.PREFER_ARGB_8888)
        builder.setImageDecoderEnabledForBitmaps(true)

        builder.setDiskCache {
            // DiskLruCacheWrapper.get( //原来博客的代码，但已经被弃用
            DiskLruCacheWrapper.create(
                File(
                    context.externalCacheDir,
                    "GlidePhoto"
                ),
                200 * 1000 * 1000
            ) //缓存的路径
        }

        // builder.setMemoryCache()
    }

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        val builder = OkHttpClient.Builder()
        builder.sslSocketFactory(sSLSocketFactory, trustManager)
        val okHttpClient = builder.build()
        // registry.replace(GlideUrl::class.java, InputStream::class.java, HttpGlideUrlLoader.Factory())
        registry.replace(GlideUrl::class.java, InputStream::class.java, OkHttpUrlLoader.Factory(okHttpClient))
    }

    /** 获取一个SSLSocketFactory */
    val sSLSocketFactory: SSLSocketFactory
        get() = try {
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, arrayOf(trustManager), SecureRandom())
            sslContext.socketFactory
        } catch (e: Exception) {
            throw RuntimeException(e)
        }

    val trustManager: X509TrustManager
        get() = object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) { }
            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) { }
            override fun getAcceptedIssuers(): Array<X509Certificate> { return arrayOf() }
        }
}