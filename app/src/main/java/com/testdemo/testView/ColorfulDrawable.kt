package com.testdemo.testView

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.Log
import android.util.TypedValue

/**
 * Create by Greyson
 */
class ColorfulDrawable(val context: Context) : Drawable() {
    val tag: String = this.javaClass.simpleName

    private var paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val path = Path()

    private val bgdColor = Color.parseColor("#3E82FB")
    private val fgdColor = Color.parseColor("#AAC8FF")

    /**
     *   _____
     *  /    /
     * /____/
     * 斜四方形的上、下边长度
     */
    private var inclinedRectWidth = dp2px(12f)

    /**
     * 斜四方形的斜边在X轴上所占的宽度
     */
    private val inclinedLineWidth = dp2px(10f)

    override fun draw(canvas: Canvas) {
        Log.e("greyson", "parse's bgd: $bgdColor, fgd: $fgdColor. and 0x's bgd: ${0x3E82FB}, fgd: ${0xAAC8FF}")
        canvas.drawColor(bgdColor) //todo greyson 设置颜色不能直接用0x3E82FB，为什么！
        paint.color = fgdColor
        Log.d("greyson", "width:$intrinsicWidth, height=$intrinsicHeight, bound=${bounds}")
        val number = intrinsicWidth / inclinedRectWidth
        for (i in 0..number.toInt() + 1 step 2) {
            drawInclinedRect(canvas, i)
        }
    }

    private fun drawInclinedRect(canvas: Canvas, offset: Int) {
        //        path.reset()
        path.moveTo(offset * inclinedRectWidth, 0f)
        path.rLineTo(inclinedRectWidth, 0f)
        path.rLineTo(-inclinedLineWidth, inclinedLineWidth)
        path.rLineTo(-inclinedRectWidth, 0f)
        path.rLineTo(inclinedLineWidth, -inclinedLineWidth)
        canvas.drawPath(path, paint)
    }

    override fun onBoundsChange(bounds: Rect?) {
        super.onBoundsChange(bounds)
        Log.e("greyson", "onBoundsChange() : $bounds")
    }

    /*override fun getMinimumWidth(): Int {
        return context
    }

    override fun getIntrinsicHeight(): Int {
        return 30
    }

    override fun getIntrinsicWidth(): Int {
        return 600
    }*/

    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
        invalidateSelf()
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
        invalidateSelf()
    }

    private fun dp2px(dpValue: Float) : Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, context.resources.displayMetrics)
    }
}