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
class ClipImageViewV1 : AppCompatImageView {
    private val tag = "ClipImageViewV1"

    private var clipPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var clipShape: BitmapDrawable? = null
    private var clipBmp: Bitmap? = null
    private val curRect = RectF(0f, 0f, 0f, 0f)
    private val srcMatrix = Matrix()
    private val curXfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
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

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (!enableClip) return

        val curWidth = measuredWidth
        val curHeight = measuredHeight
        if (curWidth <= 0 || curHeight <= 0) return

        curRect.right = curWidth.toFloat()
        curRect.bottom = curHeight.toFloat()

        clipShape?.let { srcDrawable ->
            Log.w(
                tag, "onMeasure(${this.hashCode()}): srcDrawable, density=${srcDrawable.bitmap.density}" +
                        ", bounds=${srcDrawable.bounds}, width=${srcDrawable.intrinsicWidth}, height=${srcDrawable.intrinsicHeight}"
            )

            val sizeScale = curRect.height() / srcDrawable.intrinsicHeight
            srcMatrix.setScale(sizeScale, sizeScale)

            if (clipBmp == null) {
                // 要剪切的图形，原图可能比较窄，所以需要将图的宽高比例弄成跟当前ImageView的一样。这里是重新绘制到跟ImageView一样宽高的Bitmap上
                clipBmp = Bitmap.createBitmap(curWidth, curHeight, Bitmap.Config.ALPHA_8)

                val clipCanvas = Canvas(clipBmp!!)

                // 设置的裁剪形状图的密度（具体在哪个Drawable目录下）跟当前手机屏幕像素密度之间有差别的话，进行相应的缩放
                // 如果图片的密度较小，则画布矩阵会放大，Rect变小，但画布的大小不变！drawBitmap时是根据矩阵的点（猜测），所以图片就等于放大了
                val densityScale = resources.displayMetrics.densityDpi.toFloat() / srcDrawable.bitmap.density
                clipCanvas.scale(densityScale, densityScale)
                Log.w(
                    tag,
                    "绘制之前的： clipCanvas density=${clipCanvas.density} —— ${clipCanvas.clipBounds} _ ${clipCanvas.height}"
                )

                // 如果不用matrix进行缩放，以下两个 drawBitmap 方法都只会将 srcDrawable 的像素点原原本本地画到clipCanvas上，
                // 根据已知条件判断：clipShape这个Drawable中的Bitmap对象，在读取资源文件时已经根据资源文件所在目录与当前手机像素
                // 密度之间的差别而进行过缩放了
                clipCanvas.drawBitmap(srcDrawable.bitmap, srcMatrix, clipPaint)
                // clipCanvas.drawBitmap(srcDrawable.bitmap, 0f, 0f, clipPaint)
            }

            Log.w(
                tag,
                "onMeasure(${this.hashCode()}): displayMetrics, density=${resources.displayMetrics.density}" +
                        ", densityDpi=${resources.displayMetrics.densityDpi}" +
                        ", scaledDensity=${resources.displayMetrics.scaledDensity}, srcMatrix=$srcMatrix"
            )
        }
    }

    override fun onDraw(canvas: Canvas) {
        val srcBitmap = clipBmp
        if (!enableClip || srcBitmap == null) {
            super.onDraw(canvas)
            return
        }

        Log.e(
            tag,
            "onDraw(${this.hashCode()}): $measuredWidth-$measuredHeight" +
                    ", ${srcBitmap.width}-${srcBitmap.height}_${srcBitmap.density}, $curRect"
        )
        val layerId = canvas.saveLayer(0f, 0f, curRect.right, curRect.bottom, null)

        super.onDraw(canvas)


        clipPaint.xfermode = curXfermode
        canvas.drawBitmap(srcBitmap, null, curRect, clipPaint)
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