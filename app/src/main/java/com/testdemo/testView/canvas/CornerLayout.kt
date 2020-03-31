package com.testdemo.testView.canvas

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PaintFlagsDrawFilter
import android.graphics.Path
import android.util.AttributeSet
import android.widget.RelativeLayout
import com.testdemo.R

/**
 * Create by Greyson
 * 可以设置四个角落的任一个或多个为圆角的RelativeLayout
 */
class CornerLayout : RelativeLayout {

    private val path = Path()
    private var mRadius = 0f
    private var mTopLeftRadius = 0f
    private var mTopRightRadius = 0f
    private var mBottomLeftRadius = 0f
    private var mBottomRightRadius = 0f
    private var mClipCanvas = false//是否剪切了画布

    private var mSetfil: PaintFlagsDrawFilter? = null //抗锯齿

    init {
    }

    constructor(context: Context) : this(context, null) //如何在构造器中调用其它构造器？（构造器委托的方式）

    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0) {
    }

    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : super(context, attributeSet, defStyleAttr) {
        val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.CornerLayout, defStyleAttr, 0)
        mRadius = typedArray.getDimension(R.styleable.CornerLayout_radius, 0f)
        mTopLeftRadius = typedArray.getDimension(R.styleable.CornerLayout_topLeftRadius, 0f)
        mTopRightRadius = typedArray.getDimension(R.styleable.CornerLayout_topRightRadius, 0f)
        mBottomLeftRadius = typedArray.getDimension(R.styleable.CornerLayout_bottomLeftRadius, 0f)
        mBottomRightRadius = typedArray.getDimension(R.styleable.CornerLayout_bottomRightRadius, 0f)
        typedArray.recycle()
        mSetfil = PaintFlagsDrawFilter(0, Paint.FILTER_BITMAP_FLAG)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        if (mRadius != 0f) {
            if (mTopLeftRadius == 0f) {
                mTopLeftRadius = mRadius
            }
            if (mTopRightRadius == 0f) {
                mTopRightRadius = mRadius
            }
            if (mBottomLeftRadius == 0f) {
                mBottomLeftRadius = mRadius
            }
            if (mBottomRightRadius == 0f) {
                mBottomRightRadius = mRadius
            }
        }
        if (mTopLeftRadius == 0f && mTopRightRadius == 0f
                && mBottomLeftRadius == 0f && mBottomRightRadius == 0f) {
            mClipCanvas = false
            return
        }

        val widthF = measuredWidth.toFloat()
        val heightF = measuredHeight.toFloat()

        path.moveTo(mTopLeftRadius, 0f)
        path.lineTo(widthF - mTopRightRadius, 0f)
        path.quadTo(widthF, 0f, widthF, mTopRightRadius)
        path.lineTo(widthF, heightF - mBottomRightRadius)
        path.quadTo(widthF, heightF, widthF - mBottomRightRadius, heightF)
        path.lineTo(mBottomLeftRadius, heightF)
        path.quadTo(0f, heightF, 0f, heightF - mBottomLeftRadius)
        path.lineTo(0f, mTopLeftRadius)
        path.quadTo(0f, 0f, mTopLeftRadius, 0f)
        setWillNotDraw(false)
        mClipCanvas = true
    }

    override fun dispatchDraw(canvas: Canvas?) {
        canvas?.drawFilter = mSetfil//抗锯齿，是否有用待证
        super.dispatchDraw(canvas)
    }

    override fun onDraw(canvas: Canvas?) {
        if (mClipCanvas) {
            canvas?.clipPath(path)
        }
        super.onDraw(canvas)
    }
}