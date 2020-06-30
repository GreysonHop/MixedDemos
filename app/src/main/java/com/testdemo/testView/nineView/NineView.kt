package com.testdemo.testView.nineView

import android.content.Context
import android.graphics.Canvas
import android.view.View
import android.widget.LinearLayout

/**
 * Created by Greyson on 2020/06/30
 */
class NineView : LinearLayout {

    private var gap = 0

    constructor(context: Context) : super(context)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val mode = MeasureSpec.getMode(widthMeasureSpec)

        for (i in 0 until childCount) {
            val childView = getChildAt(i)
            when {
                childCount <= 4 -> {
                    val childWidth = (measuredWidth - gap) / 2
                    measureChild(childView, widthMeasureSpec, heightMeasureSpec)
                    childView.measure(MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.AT_MOST)
                        , MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.EXACTLY))
                }

                childCount <= 9 -> {
                    val childWidth = (measuredWidth - gap * 2) / 3
                    childView.measure(MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.AT_MOST)
                        , MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.EXACTLY))
                }

                else -> childView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.EXACTLY)
                    , MeasureSpec.makeMeasureSpec(0, MeasureSpec.EXACTLY))
            }

        }
        setMeasuredDimension(width, MeasureSpec.getSize(heightMeasureSpec))
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var layoutX = l //当前布局开始点的X坐标
        var layoutY = t
        when {
            childCount <= 4 -> {
                val childWidth = (measuredWidth - gap) / 2
                for (i in 0 until childCount) {
                    val column = i % 2
                    val row = i / 2

                    val childView = getChildAt(i)
//                    childView.layout(layoutX + column * (childWidth + gap), layoutX + row * (childWidth + gap)
//                        , )
                }
            }

            childCount == 3 -> {
                val childWidth = (measuredWidth - gap) / 2
            }
            childCount >= 4 -> {

            }
        }
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
    }
}