package com.testdemo.testView

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.util.Log
import androidx.appcompat.widget.AppCompatImageView
import com.testdemo.R
import java.lang.Exception

/**
 * Create by Greyson
 *
 * 根据图片非透明区域进行切割的View。
 * 2022/3/27 目前只做正方形的，并且对切割图，要么正方，要么瘦高，不适配胖矮
 * 此版本修复了图片较窄时、以及适配了2K屏手机上的显示（比如手机像素密度不是480，而图片资源在xxhdpi)
 */
class ClipImageView2 : AppCompatImageView {
    private val tag = "ClipImageView2"

    private var clipPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var clipShape: BitmapDrawable? = null
    private var clipBmp: Bitmap? = null
    private val curRect = RectF(0f, 0f, 0f, 0f)
    private val curXfermode by lazy { PorterDuffXfermode(PorterDuff.Mode.DST_IN) }
    private var enableClip = true

    init {
        clipPaint.style = Paint.Style.FILL
    }

    constructor(context: Context) : this(context, null) // 如何在构造器中调用其它构造器？（构造器委托的方式）

    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)

    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attributeSet,
        defStyleAttr
    ) {
        init(context, attributeSet)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        if (attrs == null) return

        val attrArray = context.obtainStyledAttributes(attrs, R.styleable.ClipImageView)
        try {
            this.clipShape = attrArray.getDrawable(R.styleable.ClipImageView_clipShape) as? BitmapDrawable
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            attrArray.recycle()
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        Log.d(tag, "onLayout: changed=$changed, $left-$top-$right-$bottom")

        if (!enableClip) return

        curRect.right = width.toFloat()
        curRect.bottom = height.toFloat()
        if (curRect.isEmpty) return

        clipShape?.let { srcDrawable ->
            Log.d(
                tag, "onLayout(${this.hashCode()}): srcDrawable, density=${srcDrawable.bitmap.density}" +
                        ", bounds=${srcDrawable.bounds}, width=${srcDrawable.intrinsicWidth}, height=${srcDrawable.intrinsicHeight}"
            )

            if (clipBmp == null) { // 变成正方形，只需要运行一次
                // 要剪切的图形，原图可能比较窄，所以需要将图的宽高比例弄成正方形的，以高为依据
                clipBmp = Bitmap.createBitmap(
                    srcDrawable.intrinsicHeight,
                    srcDrawable.intrinsicHeight,
                    Bitmap.Config.ALPHA_8
                )

                val clipCanvas = Canvas(clipBmp!!)

                Log.d(
                    tag,
                    "clipCanvas' density=${clipCanvas.density}, width=${clipCanvas.width}, height=${clipCanvas.height}" +
                            ", displayMetrics' density=${resources.displayMetrics.density}" +
                            ", densityDpi=${resources.displayMetrics.densityDpi}" +
                            ", scaledDensity=${resources.displayMetrics.scaledDensity}"
                )

                clipCanvas.drawBitmap(srcDrawable.bitmap, 0f, 0f, clipPaint)
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        val srcBitmap = clipBmp
        if (!enableClip || srcBitmap == null) {
            super.onDraw(canvas)
            return
        }

        Log.d(
            tag,
            "onDraw(${this.hashCode()}): srcBitmap=${srcBitmap.width}-${srcBitmap.height}_${srcBitmap.density}" +
                    ", curRect=$curRect"
        )

        val layerId = canvas.saveLayer(curRect, null)

        super.onDraw(canvas) // dst
        clipPaint.xfermode = curXfermode
        canvas.drawBitmap(srcBitmap, null, curRect, clipPaint) // src，这里源Bitmap会被自动放大
        clipPaint.xfermode = null

        canvas.restoreToCount(layerId)
    }

    /**
     * 是否剪切ImageView，默认为true
     */
    fun enableClip(enable: Boolean) {
        enableClip = enable
        postInvalidate()
    }

    fun setClipShape(resId: Int) {
        clipShape = resources.getDrawable(resId, null) as? BitmapDrawable ?: return
        postInvalidate()
    }

    fun setClipShape(drawable: BitmapDrawable) {
        clipShape = drawable
        postInvalidate()
    }
}