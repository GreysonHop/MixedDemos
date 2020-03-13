package com.testdemo.testShader

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import android.util.TypedValue

/**
 * Create by Greyson
 */
class PictureWithTextDrawable : Drawable {

    var text: String
    private lateinit var paint: Paint
    private lateinit var src: BitmapDrawable
    private lateinit var bg: Drawable
    private lateinit var context: Context

    constructor(src: Drawable, bg: Drawable, text: String) : super() {
        this.src = src as BitmapDrawable
        this.bg = bg
        this.text = text
        init()
    }

    private fun init() {
        paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = Color.GRAY
    }

    public fun setTextSize(textSize: Float) {
        paint.textSize = textSize
    }

    /*override fun getIntrinsicWidth(): Int {
        return bg.intrinsicWidth
    }

    override fun getIntrinsicHeight(): Int {
        return bg.intrinsicHeight
    }*/

    override fun draw(canvas: Canvas) {
        canvas.drawColor(Color.BLACK)
        bg.draw(canvas)
        val srcBitmap = src.bitmap
        Log.d("greyson", "srcBitmap: ${srcBitmap.byteCount}, ${srcBitmap.height}")
        canvas.drawBitmap(srcBitmap, 0f, 0f, paint)
        val textHeight = paint.fontMetricsInt.bottom - paint.fontMetricsInt.top
        canvas.drawText(text, 0f, bounds.bottom.toFloat(), paint)
    }

    override fun onBoundsChange(bounds: Rect?) {
        super.onBoundsChange(bounds)
        getBounds().let {
            Log.d("greyson", "onBoundsChange,l: ${it.left}, t: ${it.top}, r: ${it.right}, b: ${it.bottom}")
        }
    }

    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
    }
}