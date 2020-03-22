package com.testdemo.testCanvas.waveView

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator

/**
 * Create by Greyson on 2020/03/22
 */
class AnimWaveView : View {
    private val mPaint: Paint = Paint()
    private val mPath: Path = Path()
    private val mItemWaveLength = 1200f
    private var dx: Int = 0

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        mPaint.color = Color.GREEN
        mPaint.style = Paint.Style.FILL

        startAnim()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {
            mPath.reset()
            val originY = 300f
            val halfWaveLen = mItemWaveLength / 2
            mPath.moveTo(-mItemWaveLength + dx, originY)
            var i = -mItemWaveLength
            while (i <= width + mItemWaveLength) {
                mPath.rQuadTo(halfWaveLen / 2, -100f, halfWaveLen, 0f)
                mPath.rQuadTo(halfWaveLen / 2, 100f, halfWaveLen, 0f)
                i += mItemWaveLength
            }
            mPath.lineTo(width.toFloat(), height.toFloat())
            mPath.lineTo(0f, height.toFloat())
            mPath.close()

            it.drawPath(mPath, mPaint)
        }

    }

    private fun startAnim() {
        val animator = ValueAnimator.ofInt(0, mItemWaveLength.toInt())
        animator.duration = 2000
        animator.repeatCount = ValueAnimator.INFINITE
        animator.interpolator = LinearInterpolator()
        animator.addUpdateListener {
            dx = it.animatedValue as Int
            postInvalidate()
        }
        animator.start()
    }
}