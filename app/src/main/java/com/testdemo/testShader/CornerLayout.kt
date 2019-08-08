package com.testdemo.testShader

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.util.AttributeSet
import android.widget.RelativeLayout
import com.testdemo.broken_lib.Utils

class CornerLayout : RelativeLayout {

    constructor(context : Context) {
        this(context, null)
    }

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        setWillNotDraw(false)
    }

    override fun onDraw(canvas: Canvas?) {
        var radius = Utils.dp2px(20)
        var radiusF = Integer.valueOf(radius).toFloat()
        println("greyson CornerLayout width = ${width} -- height = ${height} ||| ${radius} -- ${radiusF}")

        if (width > radius && height > radius) {
            val widthF = Integer.valueOf(width).toFloat()
            val heightF = Integer.valueOf(height).toFloat()
            val path = Path()
            path.moveTo(radiusF, 0f)
            path.lineTo(widthF - radiusF, 0f)
            path.quadTo(widthF, 0f, widthF, radiusF)
            path.lineTo(widthF, heightF - radiusF)
            path.quadTo(widthF, heightF, widthF - radiusF, heightF)
            path.lineTo(radiusF, heightF)
            path.quadTo(0f, heightF, 0f, heightF - radiusF)
            path.lineTo(0f, radiusF)
            path.quadTo(0f, 0f, radiusF, 0f)
            canvas?.clipPath(path)
        }
        super.onDraw(canvas)
    }
}