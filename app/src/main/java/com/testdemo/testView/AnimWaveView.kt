package com.testdemo.testView

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent

/**
 * Create by Greyson on 2020/03/22
 */
class AnimWaveView : View, LifecycleObserver {
    private val mPaint: Paint = Paint()
    private val mPath: Path = Path()
    private val mItemWaveLength = 1200f
    private var dx: Int = 0

    private val animator by lazy { //这里其实没必要lazy，反正在构造器中就会实例化。只是小测试一下lazy属性而已
        ValueAnimator.ofInt(0, mItemWaveLength.toInt()).apply {
            duration = 2000
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
            addUpdateListener {
                dx = it.animatedValue as Int
                postInvalidate()
            }
        }
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        mPaint.color = Color.GREEN
        mPaint.style = Paint.Style.FILL

        animator.start()
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

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun stopAnim() {
        Log.d("greyson", "AnimWaveView-stopAnim: activity destroy")
        animator.cancel()
        animator.removeAllListeners()
        // animator.removeAllUpdateListeners()
    }
}