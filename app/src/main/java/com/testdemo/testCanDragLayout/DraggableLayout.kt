package com.testdemo.testCanDragLayout

import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.animation.DecelerateInterpolator
import android.widget.LinearLayout

class DraggableLayout : LinearLayout {
    private val TAG = "greyson_dragLayout"

    private var lastTouchY = 0f
    private var firstTouchY = 0f

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        ev?.let {
            val y = it.y
            when (it.action) {
                MotionEvent.ACTION_DOWN -> {
                    Log.i(TAG, "event down y = $y")
                    lastTouchY = y
                    firstTouchY = y
                }

                MotionEvent.ACTION_MOVE -> return lastTouchY != y
            }
        }
        return super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            val y = it.y
            when (it.action) {
                MotionEvent.ACTION_MOVE -> {
                    val translationOffset = y - firstTouchY//滑动的总偏移量
                    val currentY = (Math.sqrt(Math.abs(translationOffset).toDouble()) * 2).toFloat()
                    when (y - firstTouchY < 0) {
                        true -> translationY = -currentY
                        false -> translationY = currentY
                    }
                    lastTouchY = y
                }

                MotionEvent.ACTION_UP -> {
                    Log.i(TAG, "event up y = $y  translationY=$translationY")
                    val objectAnimator = ObjectAnimator.ofFloat(this, "translationY", 0f)
                    objectAnimator.duration = 300
                    objectAnimator.interpolator = DecelerateInterpolator()
                    objectAnimator.start()
                }
            }
        }
        return true
    }
}