package com.testdemo.testShader

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import com.testdemo.R
import kotlinx.android.synthetic.main.act_test_shader.*
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.widget.ImageView
import java.io.ByteArrayOutputStream
import java.lang.Exception


/**
 * Created by Greyson on 2018/12/10.
 */
class TestShaderAct : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_test_shader)

        iv_test_bitmap.post {
            val xmlDrawable = iv_test_bitmap.drawable
            val xmlBitmap = (xmlDrawable as BitmapDrawable).bitmap
            Log.i("greyson", "通过XML设置的ImageView：height=${iv_test_bitmap.height} - width=${iv_test_bitmap.width}" +
                    "\n drawable's height=${xmlDrawable.bounds.height()}-width=${xmlDrawable.bounds.width()}" +
                    "\n intrinsicHeight=${xmlDrawable.intrinsicHeight}-intrinsicWidth=${xmlDrawable.intrinsicWidth}")
            Log.i("greyson", "通过XML设置的Bitmap：byteCount=${xmlBitmap.byteCount}, height=${xmlBitmap.height}" +
                    "-width=${xmlBitmap.width}, density=${xmlBitmap.density},config=${xmlBitmap.config},ninePatchChunk=${xmlBitmap.ninePatchChunk}")
        }


        val myBitmap = readBitMap(this, R.drawable.img5)
        myBitmap?.let {
            Log.i("greyson", "通过压缩获取的Bitmap：byteCount=${it.byteCount}, height=${it.height}" +
                    "-width=${it.width}, density=${it.density},config=${it.config},ninePatchChunk=${it.ninePatchChunk}")

            iv_test_my_bitmap.setImageBitmap(it)
            Log.i("greyson", "自己生成的Bitmap与设置后的对象对比：$it -- ${(iv_test_my_bitmap.drawable as BitmapDrawable).bitmap}")

            Log.i("greyson", "获取设置后的Drawable：height=${iv_test_my_bitmap.height} - width=${iv_test_my_bitmap.width}" +
                    "\n drawable's height=${iv_test_my_bitmap.drawable.bounds.height()}-width=${iv_test_my_bitmap.drawable.bounds.width()}" +
                    "\n intrinsicHeight=${iv_test_my_bitmap.drawable.intrinsicHeight}-intrinsicWidth=${iv_test_my_bitmap.drawable.intrinsicWidth}")
        }


        println("greyson this phone's factor: " +
                "\ndensity = ${resources?.displayMetrics?.density}" +
                "\nwidthPixel = ${resources.displayMetrics.widthPixels}" +
                "\nheightPixel = ${resources.displayMetrics.heightPixels}" +
                "\ndensityDpi = ${resources.displayMetrics.densityDpi} " +
                "\nscaledDensity = ${resources.displayMetrics.scaledDensity}" +
                "\nxdpi = ${resources.displayMetrics.xdpi}" +
                "\nydpi = ${resources.displayMetrics.ydpi}")
    }

    fun readBitMap(context: Context, resId: Int): Bitmap? {
        val opt = BitmapFactory.Options()
        opt.inPreferredConfig = Bitmap.Config.RGB_565
        opt.inPurgeable = true
        opt.inInputShareable = true
        //获取资源图片
        val `is` = context.resources.openRawResource(resId)
        return BitmapFactory.decodeStream(`is`, null, opt)
    }

    /**
     * Bitmap图片的质量压缩方法，微信分享等管用
     */
    private fun compressByQuality(srcBitmap: Bitmap, dstByte: Int): Bitmap? {
        try {
            val baos = ByteArrayOutputStream()
            var quality = 100
            var bytes: ByteArray
            do {
                baos.reset()
                quality -= 2
                srcBitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos)
                bytes = baos.toByteArray()
                Log.d("greyson", "质量压缩方法quality=$quality, bytes=${bytes.size}")
            } while (bytes.size >= dstByte && quality>2)

            Log.d("greyson", "质量压缩方法最后quality=$quality, bytes=${bytes.size}")
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}