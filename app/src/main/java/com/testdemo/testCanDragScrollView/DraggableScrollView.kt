package com.testdemo.testCanDragScrollView

import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.ScrollView

/**
 * ScrollView中，包含三个或三个以上的View，第三个和第二个重叠
 * ，可通过下拉到顶后的拉伸来展示全部。
 * 请保证ScrollView中的ViewGroup的三个子View的正确顺序（位置）
 * Created by Greyson on 2018/4/16.
 */
class DraggableScrollView : ScrollView {
    private val TAG = "DraggableScrollView"

    private var draggableView: View? = null
    private var draggableLength = 0

    private var lastY = 0f//每次滑动的上一次触摸点
    private var draggableY = 0f//下拉到顶时，手指所在的位置
    private var isDragging = false//正在拉伸中

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    override fun onFinishInflate() {
        super.onFinishInflate()
        postDelayed({
            val viewGroup = getChildAt(0) as ViewGroup
            if (viewGroup.childCount > 2) {
                val tempView = viewGroup.getChildAt(1)
                viewGroup.getChildAt(2)?.let {
                    draggableView = it
                    draggableLength = tempView.bottom - it.top + 2
                    Log.i(TAG, "onFinishInflate $it\ndraggableLength = $draggableLength\n" +
                            "tempView = ${it.top} - ${tempView.height} - ${tempView.top}\n" +
                            "view = ${it.top} - ${it.height}")
                }

            }
        }, 500)
    }

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        draggableView?.let {
            if (ev == null) {
                return super.onTouchEvent(ev)
            }

            val y = ev.y
            val mScrollY = scrollY

            when (ev.action) {
                MotionEvent.ACTION_DOWN -> lastY = y

                MotionEvent.ACTION_MOVE -> {
                    if (isDragging) {
                        if (y - lastY > 0) {
                            var offsetLength = y - draggableY
                            if (draggableLength != 0 && draggableLength < offsetLength) {
                                offsetLength = draggableLength.toFloat()
                            }
                            it.translationY = offsetLength

                        } else {//向上划
                            var offsetLength = y - draggableY;//Length必须大于0，不然可能会比原来的位置还高
                            if (offsetLength < 0) {
                                offsetLength = 0f
                            }
                            it.translationY = offsetLength

                            if (y < draggableY) {
                                isDragging = false
                            }
                        }
                        return true

                    } else if (mScrollY == 0) {
                        if (y - lastY > 0) {
                            Log.i(TAG, "----向下划 = ${y - lastY}")
                            if (!isDragging) {
                                draggableY = y
                                isDragging = true
                            }
                        }
                    }
                    lastY = y
                }

                MotionEvent.ACTION_UP -> {
                    val objectAnimator = ObjectAnimator.ofFloat(it, "translationY", 0f)
                    objectAnimator.duration = 300
                    objectAnimator.interpolator = DecelerateInterpolator()
                    objectAnimator.start()
                    isDragging = false
                }
            }
        }

        return super.onTouchEvent(ev)
    }
}