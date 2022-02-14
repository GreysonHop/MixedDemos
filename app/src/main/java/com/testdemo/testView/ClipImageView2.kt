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
class ClipImageView2 : AppCompatImageView {

    private var clipPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var clipShape: Bitmap? = null
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
            val clipShapeDrawable = attrArray.getDrawable(R.styleable.ClipImageView_clipShape)
            if (clipShapeDrawable != null) {
                if (clipShapeDrawable is BitmapDrawable) {
                    clipShape = clipShapeDrawable.bitmap
                } else
                {
                        Log.i("greyson", "")
                    (clipShape ?: Bitmap.createBitmap(clipShapeDrawable.intrinsicWidth, clipShapeDrawable.intrinsicHeight, Bitmap.Config.RGB_565)).let {
                        Log.w("greyson", "clipShape is null: ")
                        val tmpCanvas = Canvas(it)
                        clipShapeDrawable.draw(tmpCanvas)
                        clipShape = it
                    }

                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            attrArray.recycle()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        Log.v("greyson", "onDetachedFromWindow() ")
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (!enableClip) return

        val curWidth = measuredWidth.toFloat()
        val curHeight = measuredHeight.toFloat()
        if (curWidth <= 0 || curHeight <= 0) return

        curRect.right = curWidth
        curRect.bottom = curHeight

        clipShape?.let { bitmapDrawable ->
            Log.i("greyson", "intrinsicWidth=${bitmapDrawable.width}, height=${bitmapDrawable.height}")
            val ratioW2H = bitmapDrawable.width.toFloat() / bitmapDrawable.height.toFloat()
            srcRect.right = (curRect.height() * ratioW2H).toInt()
            srcRect.bottom = curRect.height().toInt()

            val heightScale = curRect.height() / bitmapDrawable.height
            srcMatrix.setScale(heightScale, heightScale)

        }
        Log.v("greyson", "onMeasure(): srcRect=$srcRect, viewRect=$curRect, srcMatrix=$srcMatrix")
    }

    override fun onDraw(canvas: Canvas) {
        val srcBitmap = clipShape
        if (!enableClip || srcBitmap == null) {
            super.onDraw(canvas)
            return
        }

        /*super.onDraw(canvas)

        clipPaint.xfermode = curXfermode // 配上DST_IN

        val layerDrawSrc = canvas.saveLayer(curRect, clipPaint)
        canvas.drawBitmap(srcBitmap.bitmap, null, srcRect, clipPaint)
        canvas.restoreToCount(layerDrawSrc)

        clipPaint.xfermode = null*/

        super.onDraw(canvas)

        val layerId = canvas.saveLayer(curRect, clipPaint)

        canvas.drawColor(Color.WHITE)
        clipPaint.xfermode = curXfermode
        canvas.drawBitmap(srcBitmap, null, srcRect, clipPaint)
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
        clipShape = drawable.bitmap
        postInvalidate()
    }
}