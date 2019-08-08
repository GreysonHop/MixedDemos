package com.testdemo.testShader

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.util.AttributeSet
import android.util.Log
import android.widget.LinearLayout

class CornerLayout2 : LinearLayout {

    constructor(context : Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    override fun onDraw(canvas: Canvas?) {

        if (width > 20 && height > 20) {
            val widthF = Integer.valueOf(width).toFloat()
            val heightF = Integer.valueOf(height).toFloat()
            val path = Path()
            path.moveTo(20f, 0f)
            path.lineTo(widthF - 20f, 0f)
            path.quadTo(widthF, 0f, widthF, 20f)
            path.lineTo(widthF, heightF - 20f)
            path.quadTo(widthF, heightF, widthF - 20f, heightF)
            path.lineTo(20f, heightF)
            path.quadTo(0f, heightF, 0f, heightF - 20f)
            path.lineTo(0f, 20f)
            path.quadTo(0f, 0f, 20f, 0f)
            canvas?.clipPath(path)
        }
        Log.d("greyson", "CornerLayout2222 width = ${width} -- height = ${height} ")
        super.onDraw(canvas)
    }
}