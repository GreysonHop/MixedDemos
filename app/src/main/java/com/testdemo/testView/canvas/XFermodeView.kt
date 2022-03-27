package com.testdemo.testView.canvas

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.View

class XFermodeView : View {

    lateinit var dstBitmap: Bitmap
    lateinit var srcBitmap: Bitmap
    lateinit var paint: Paint
    var modeArray = arrayOf(
        PorterDuff.Mode.SRC, PorterDuff.Mode.SRC_IN, PorterDuff.Mode.SRC_OVER, PorterDuff.Mode.SRC_OUT, PorterDuff.Mode.SRC_ATOP,
        PorterDuff.Mode.DST, PorterDuff.Mode.DST_IN, PorterDuff.Mode.DST_OVER, PorterDuff.Mode.DST_OUT, PorterDuff.Mode.DST_ATOP,
        PorterDuff.Mode.XOR, PorterDuff.Mode.DARKEN, PorterDuff.Mode.LIGHTEN, PorterDuff.Mode.MULTIPLY, PorterDuff.Mode.SCREEN,
        PorterDuff.Mode.ADD, PorterDuff.Mode.OVERLAY, PorterDuff.Mode.CLEAR
    )
    var modeIndex = 0
    var xfermode = PorterDuffXfermode(modeArray[modeIndex])

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr) {
        init()
    }

    fun init() {
        setLayerType(LAYER_TYPE_SOFTWARE, null)
        paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 20f, resources.displayMetrics)

        setOnClickListener {
            modeIndex++
            if (modeIndex >= modeArray.size) {
                modeIndex = 0
            }
            xfermode = PorterDuffXfermode(modeArray[modeIndex])
            invalidate()
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        Log.i("greyson", "XFermodeView onSizeChanged")
        if (width != 0 && height != 0) {
            val halfWidth = width / 2
            val halfHeight = height / 2
            paint.color = 0xffffcc44.toInt()
            dstBitmap = Bitmap.createBitmap(halfWidth, halfHeight, Bitmap.Config.ARGB_8888)
            val dstCanvas = Canvas(dstBitmap)
            dstCanvas.drawCircle(halfWidth / 2f, halfWidth / 2f, halfWidth / 2f, paint)

            paint.color = 0xff66aaff.toInt()
            srcBitmap = Bitmap.createBitmap(halfWidth, halfWidth, Bitmap.Config.ARGB_8888)
            val srcCanvas = Canvas(srcBitmap)
            srcCanvas.drawRect(Rect(0, 0, halfWidth, halfWidth), paint)
        }
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawText(modeArray[modeIndex].toString(), 0f, height.toFloat(), paint)

        // val layerId = canvas.saveLayer(0f, 0f, width.toFloat(), height.toFloat(), null)
        // 有没有用新图层来绘制效果一样？但如果是自己用Bitmap去作为src的话，不用新图层去绘制xfermode会变成黑色或白色，而不能透明？！
        canvas.drawBitmap(dstBitmap, 0f, 0f, paint)
        paint.xfermode = xfermode
        canvas.drawBitmap(srcBitmap, width / 4f, width / 4f, paint)
        paint.xfermode = null

        // canvas.restoreToCount(layerId)

    }

}