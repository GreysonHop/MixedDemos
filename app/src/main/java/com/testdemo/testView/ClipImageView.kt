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
 */
class ClipImageView : AppCompatImageView {

    private var clipPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var clipShape: BitmapDrawable? = null
    private var clipBmp: Bitmap? = null
    private val curRect = RectF(0f, 0f, 0f, 0f)
    private val srcRect = Rect(0, 0, 0, 0)
    private val srcMatrix = Matrix()
    private val curXfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
    private var enableClip = true

    init {
        clipPaint.style = Paint.Style.FILL
    }

    constructor(context: Context) : this(context, null) //如何在构造器中调用其它构造器？（构造器委托的方式）

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
            this.clipShape = (attrArray.getDrawable(R.styleable.ClipImageView_clipShape) as? BitmapDrawable)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            attrArray.recycle()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        Log.v("greyson", "onDetachedFromWindow() ")
        clipBmp?.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (!enableClip) return

        val curWidth = measuredWidth.toFloat()
        val curHeight = measuredHeight.toFloat()
        if (curHeight <= 0 || curHeight <= 0) return

        curRect.right = curWidth
        curRect.bottom = curHeight

        clipShape?.let { srcBitmap ->
            Log.i("greyson", "intrinsicWidth=${srcBitmap.intrinsicWidth}, height=${srcBitmap.intrinsicHeight}")
            val ratioW2H = srcBitmap.intrinsicWidth.toFloat() / srcBitmap.intrinsicHeight.toFloat()
            srcRect.right = (curRect.height() * ratioW2H).toInt()
            srcRect.bottom = curRect.height().toInt()

            val heightScale = curRect.height() / srcBitmap.intrinsicHeight
            srcMatrix.setScale(heightScale, heightScale)


            if (clipBmp == null) {
                // 要剪切的图形，原图可能比较窄，所以需要将图的宽高比例弄成跟当前ImageView的一样。这里是重新绘制到跟ImageView一样宽高的Bitmap上
                clipBmp = Bitmap.createBitmap(curRect.width().toInt(), curRect.height().toInt(), Bitmap.Config.ALPHA_8)
                val clipCanvas = Canvas(clipBmp!!)
                clipCanvas.drawBitmap(srcBitmap.bitmap, srcMatrix, clipPaint)
            }

        }
        Log.v("greyson", "onMeasure(): srcRect=$srcRect, viewRect=$curRect, srcMatrix=$srcMatrix")
    }

    override fun onDraw(canvas: Canvas) {
        val srcBitmap = clipShape
        if (!enableClip || srcBitmap == null) {
            super.onDraw(canvas)
            return
        }

        val layerId = canvas.saveLayer(0f, 0f, curRect.right, curRect.bottom, null)

        super.onDraw(canvas)

        clipPaint.xfermode = curXfermode
        // canvas.drawBitmap(srcBitmap.bitmap, srcMatrix, clipPaint)
        canvas.drawBitmap(clipBmp!!, null, curRect, clipPaint)
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

    fun setClipShape(drawable: BitmapDrawable) {
        clipShape = drawable
        postInvalidate()
    }
}