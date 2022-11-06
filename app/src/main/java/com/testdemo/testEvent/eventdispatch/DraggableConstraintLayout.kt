package com.testdemo.testEvent.eventdispatch

import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.ViewConfiguration
import androidx.constraintlayout.widget.ConstraintLayout
import com.testdemo.testRecyclerViewType.gridpagersanphelper.ScreenUtils
import kotlin.math.abs

/**
 * Created by Greyson on 2022/09/02
 */
class DraggableConstraintLayout : ConstraintLayout {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val SLIDE_MODE_NO = -1
    private val SLIDE_MODE_RIGHT = 0
    private val SLIDE_MODE_LEFT = 1

    private var mSlideMode = SLIDE_MODE_NO
    private val minMoveX by lazy { ViewConfiguration.get(context).scaledTouchSlop }
    private val maxMoveX = ScreenUtils.dip2px(context, 55f).toFloat() // 消息项向右滑动的最大可移动距离，也是触发右滑事件的距离
    private var firstTouchX = 0f
    private var firstTouchY = 0f

    /*override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        Log.d("greyson", "dispatchTouchEvent(${this.hashCode()}): ${ev.actionMasked}, $forcedInterception")
        if (forcedInterception) {
            delegateTouchEvent?.onTouch(this, ev)

            if (ev.actionMasked == MotionEvent.ACTION_UP || ev.actionMasked == MotionEvent.ACTION_CANCEL) {
                forcedInterception = false
            }
            return true
        }

        if (ev.actionMasked == MotionEvent.ACTION_DOWN) {
            firstTouchX = ev.rawX
            firstTouchY = ev.rawY
            downEventBackUp = MotionEvent.obtain(ev)

        } else if (ev.actionMasked == MotionEvent.ACTION_MOVE) {
            val moveX: Float = ev.rawX - firstTouchX
            val moveY: Float = ev.rawY - firstTouchY
            val vMove = abs(moveY) < abs(moveX) / 2
            Log.d("greyson", "dispatchTouchEvent-ACTION_MOVE: $vMove, ${abs(moveX)}, $minMoveX")
            if (abs(moveX) > minMoveX && vMove) {
                forcedInterception = true
                delegateTouchEvent?.let { listener ->
                    downEventBackUp?.let { listener.onTouch(this, it) }
                    listener.onTouch(this, ev)
                }

                super.dispatchTouchEvent(
                    MotionEvent.obtain(ev).apply {
                        action = MotionEvent.ACTION_CANCEL
                    }
                )
                return true
            }

        }

        return super.dispatchTouchEvent(ev)
    }*/

    var downEventBackUp: MotionEvent? = null
    var forcedInterception = false

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        Log.d("greyson", "DraggableConstraintLayout(${this.hashCode()})-onInterceptTouchEvent: ${ev.actionMasked}")

        if (ev.actionMasked == MotionEvent.ACTION_DOWN) {
            firstTouchX = ev.rawX
            firstTouchY = ev.rawY
            // downEventBackUp = MotionEvent.obtain(ev)

        } else if (ev.actionMasked == MotionEvent.ACTION_MOVE) {
            val moveX: Float = ev.rawX - firstTouchX
            val moveY: Float = ev.rawY - firstTouchY
            val vMove = abs(moveY) < abs(moveX) / 2
            Log.d("greyson", "DraggableConstraintLayout-onInterceptTouchEvent-ACTION_MOVE: $vMove, ${abs(moveX)}, $minMoveX")
            if (abs(moveX) > minMoveX && vMove) {
                return true
            }
        }

        return super.onInterceptTouchEvent(ev)
    }

    // ----------------------- 简单的测试 开始 》》》》》》》》》》》》》》》》》
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        Log.d("greyson", "DraggableConstraintLayout-dispatchTouchEvent: ${ev.actionMasked}")
        return super.dispatchTouchEvent(ev)
    }

    /*override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        Log.d("greyson", "DraggableConstraintLayout-onInterceptTouchEvent: ${ev.actionMasked}")
        return true
        // return super.onInterceptTouchEvent(ev)
    }*/

    override fun onTouchEvent(event: MotionEvent): Boolean {
        Log.d("greyson", "DraggableConstraintLayout-onTouchEvent: ${event.actionMasked}")

        return super.onTouchEvent(event)
        /*return if (delegateTouchEvent?.onTouch(this, event) == true) {
            true
        } else {
            super.onTouchEvent(event)
        }*/
    }
    // 《《《《《《《《《《《《《《《 简单的测试 结束 -----------------------


    /*override fun onTouchEvent(event: MotionEvent): Boolean {

        if (event.action == MotionEvent.ACTION_DOWN) {
            firstTouchX = event.rawX
            firstTouchY = event.rawY
            Log.d("greyson", "TouchEvent down:")

        } else if (event.action == MotionEvent.ACTION_MOVE) {
            var moveX: Float = event.rawX - firstTouchX
            val moveY: Float = event.rawY - firstTouchY
            if (mSlideMode == SLIDE_MODE_NO) {
                val vMove = abs(moveY) < abs(moveX) / 2
                if (moveX > minMoveX && vMove) {
                    mSlideMode = SLIDE_MODE_RIGHT
                    slideEvent?.modeChanged(SLIDE_MODE_RIGHT)
                    cancelLongPress() // 取消长按事件，避免与左右滑事件的冲突
                    Log.d(
                        "greyson",
                        "TouchEvent move: first right "
                    )
                    parent.requestDisallowInterceptTouchEvent(true)
                    return true
                } else if (moveX < -minMoveX && vMove) {
                    mSlideMode = SLIDE_MODE_LEFT

                    slideEvent?.modeChanged(SLIDE_MODE_LEFT)
                    cancelLongPress()
                    Log.d(
                        "greyson",
                        "TouchEvent move: first left"
                    )
                }
            } else if (mSlideMode == SLIDE_MODE_LEFT) {
                // greyson_2022/9/2 原来是发送左划的组件
                Log.d(
                    "greyson",
                    "TouchEvent move: left send view "
                )

            } else if (mSlideMode == SLIDE_MODE_RIGHT) {
                if (moveX < 0) {
                    moveX = 0f
                } else if (moveX >= maxMoveX) {
                    moveX = maxMoveX
                    if (firstPullFull) {
                        firstPullFull = false
                        vibrate()
                    }
                } else {
                    firstPullFull = true
                }
                viewToSlide.setTranslationX(moveX)
                viewQuoteTip.setAlpha(moveX / maxMoveX)
                viewQuoteTip.setTranslationX(moveX / 3)
                return true
            }
        } else if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL) {
            parent.requestDisallowInterceptTouchEvent(false)
            if (SLIDE_MODE_RIGHT == mSlideMode) {
                val translationX: Float = viewToSlide.getTranslationX()
                if (translationX > 0) {
                    //将滑动的组件复位
                    val movePercent: Float = translationX / maxMoveX
                    val alphaAnim = ObjectAnimator.ofFloat(viewQuoteTip, "alpha", movePercent, 0f)
                    alphaAnim.duration = 300
                    alphaAnim.start()
                    val translateAnimForTip = ObjectAnimator.ofFloat(viewQuoteTip, "translationX", translationX / 3, 0f)
                    translateAnimForTip.duration = 300
                    translateAnimForTip.start()
                    val translateAnim = ObjectAnimator.ofFloat(viewToSlide, "translationX", translationX, 0f)
                    translateAnim.duration = 300
                    translateAnim.start()
                }
                if (event.action == MotionEvent.ACTION_UP && Math.abs(translationX - maxMoveX) < 7) {
                    //抬起手时，向右滑动的距离近似于触发右滑事件的距离。7是允许的误判范围
                    EventBus.getDefault().post(EventMessage(Const.Event.RIGHT_PULL_QUOTE_MSG, item))
                }
                viewQuoteTip = null
                viewToSlide = viewQuoteTip
                mSlideMode = SLIDE_MODE_NO
                slideEvent?.modeChanged(SLIDE_MODE_NO)

                Log.d(
                    "greyson",
                    "TouchEvent final " + event.action + ": right"
                )
                return true
            }
            Log.d(
                "greyson",
                "TouchEvent final " + event.action + ": other"
            )
            viewQuoteTip = null
            viewToSlide = viewQuoteTip
            mSlideMode = SLIDE_MODE_NO
            slideEvent?.modeChanged(SLIDE_MODE_NO)
        }
        return false
    }*/

    var slideEvent: SlideEvent? = null

    interface SlideEvent {
        fun modeChanged(mode: Int)

        fun sliding(x: Int, y: Int)

    }

    var delegateTouchEvent: OnTouchListener? = null
}