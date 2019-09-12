package com.testdemo.testShader

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.util.AttributeSet
import android.widget.RelativeLayout
import com.testdemo.broken_lib.Utils

class CornerLayout : RelativeLayout {

    private val path = Path()
    private val radius = Utils.dp2px(20)
    private val radiusF = radius.toFloat()

    init {
        println("greyson CornerLayout init{} radius = $radius")
    }

    constructor(context : Context) : super(context) //如何在构造器中调用其它构造器？（构造器委托的方式）

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        println("greyson CornerLayout constructor()")
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        val widthF = measuredWidth.toFloat()
        val heightF = measuredHeight.toFloat()

        path.moveTo(radiusF, 0f)
        path.lineTo(widthF - radiusF, 0f)
        path.quadTo(widthF, 0f, widthF, radiusF)
        path.lineTo(widthF, heightF - radiusF)
        path.quadTo(widthF, heightF, widthF - radiusF, heightF)
        path.lineTo(radiusF, heightF)
        path.quadTo(0f, heightF, 0f, heightF - radiusF)
        path.lineTo(0f, radiusF)
        path.quadTo(0f, 0f, radiusF, 0f)
        setWillNotDraw(false)
    }

    override fun onDraw(canvas: Canvas?) {
        if (width > radius && height > radius) {
            canvas?.clipPath(path)
        }
        super.onDraw(canvas)
    }
}