package com.testdemo.testView.nineView

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.Px
import kotlin.math.min

/**
 * Created by Greyson on 2020/06/30
 */
class NineView : LinearLayout {

    var gap = 0
        set(value) {
            field = value
            invalidate()
        }

    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet? = null) : super(context, attrs)

    /*fun setGap(@Px gap: Int) {
        this.gap = gap
        invalidate()
    }

    fun getGap(): Int {
        return gap
    }*/

    override fun addView(child: View?, index: Int, params: ViewGroup.LayoutParams?) {
        if (childCount == 9) {
            return
        }
        super.addView(child, index, params)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)

        val getModeStr: (Int) -> String = {
            when (it) {
                MeasureSpec.EXACTLY -> "EXACTLY"
                MeasureSpec.AT_MOST -> "AT_MOST"
                else -> "UNSPECIFIED"
            }
        }
        Log.e("greyson", "NineView-onMeasure: mode=${getModeStr(widthMode)} _ size = $widthSize" +
                " \nmode2= ${getModeStr(heightMode)} _ size2= $heightSize")

        val column = if (childCount <= 4) 2 else 3 //总共多少列
        val row = (childCount - 1) / column + 1
        val childWidth = (measuredWidth - gap * (column - 1)) / column
        val expectedHeight = row * childWidth + (row - 1) * gap

        for (i in 0 until childCount.coerceAtMost(9)) {
            val childView = getChildAt(i)
            // Log.e("greyson", "NineView-onMeasure's child$i: ${childView.layoutParams.width} _ ${childView.layoutParams.height}")
            // measureChild(childView, widthMeasureSpec, heightMeasureSpec)
            childView.measure(MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.AT_MOST),
                MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.AT_MOST))
        }

        //如果是UNSPECIFIED模式怎么给出宽呢？
        setMeasuredDimension(widthSize,
            when (heightMode) {
                MeasureSpec.AT_MOST -> {
                    min(heightSize, expectedHeight)
                }
                MeasureSpec.EXACTLY -> {
                    heightSize
                }
                else -> {
                    expectedHeight
                }
            })
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val column = if (childCount <= 4) 2 else 3 //总共多少列
        val childWidth = (measuredWidth - gap * (column - 1)) / column

        for (i in 0 until childCount.coerceAtMost(9)) {
            val col = i % column //当前子控件在第几列
            val row = i / column
            val childView = getChildAt(i)

            if (childCount == 3 && i == 2) {
                val offsetThird = (childWidth + gap) / 2
                childView.layout( col * (childWidth + gap) + offsetThird,
                    row * (childWidth + gap),
                    col * (childWidth + gap) + offsetThird + childWidth,
                    row * (childWidth + gap) + childWidth)

            } else {
                childView.layout( col * (childWidth + gap),
                    row * (childWidth + gap),
                    + col * (childWidth + gap) + childWidth,
                    row * (childWidth + gap) + childWidth)
            }
        }
    }

}
