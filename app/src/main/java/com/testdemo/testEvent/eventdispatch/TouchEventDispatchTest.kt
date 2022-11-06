package com.testdemo.testEvent.eventdispatch

import android.annotation.SuppressLint
import android.util.Log
import android.view.MotionEvent
import android.widget.Toast
import com.testdemo.BaseBindingActivity
import com.testdemo.databinding.ActTouchEventDispatchBinding
import com.testdemo.testRecyclerViewType.gridpagersanphelper.ScreenUtils
import kotlin.math.abs

/**
 * Create by Greyson on 2022/09/04
 */
class TouchEventDispatchTest : BaseBindingActivity<ActTouchEventDispatchBinding>() {
    private val SLIDE_MODE_NO = -1
    private val SLIDE_MODE_RIGHT = 0
    private val SLIDE_MODE_LEFT = 1

    private var slideMode = SLIDE_MODE_NO
    private val maxMoveX by lazy { ScreenUtils.dip2px(this, 55f).toFloat() } // 消息项向右滑动的最大可移动距离，也是触发右滑事件的距离
    private val minMoveX by lazy { ScreenUtils.dip2px(this, 3f).toFloat() }
    private var firstTouchX = 0f
    private var firstTouchY = 0f


    override fun getViewBinding(): ActTouchEventDispatchBinding {
        return ActTouchEventDispatchBinding.inflate(layoutInflater)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun initView() {
        binding.ivAvatar.setOnClickListener { log("ivAvatar: clicked!") }
        binding.tvQuoteText.setOnClickListener { log("tvQuoteText: clicked!") }
        binding.tvContentText.setOnClickListener { log("tvContentText: clicked") }
        binding.clMsgParent.setOnClickListener { log("clMsgParent: clicked") }

        binding.ivAvatar.setOnLongClickListener { log("ivAvatar: long clicked"); true }
        binding.tvQuoteText.setOnLongClickListener { log("tvQuoteText: long clicked!"); true }
        binding.tvContentText.setOnLongClickListener { log("tvContentText: long clicked"); true }
        binding.clMsgParent.setOnLongClickListener { log("clMsgParent: long clicked"); true }

        binding.clMsgParent.setOnTouchListener { v, event ->
            Log.v("greyson", "Activity-OnTouch(): ${event.actionMasked}, x,y=${event.rawX},${event.rawY}")

            if (event.action == MotionEvent.ACTION_DOWN) {
                firstTouchX = event.rawX
                firstTouchY = event.rawY
                Log.v("greyson", "TouchEvent down: firstTouchX=$firstTouchX - $firstTouchY")

            } else if (event.action == MotionEvent.ACTION_MOVE) {
                if (firstTouchX == 0f) firstTouchX = event.rawX
                if (firstTouchY == 0f) firstTouchY = event.rawY

                val moveX = event.rawX - firstTouchX
                val moveY = event.rawY - firstTouchY
                if (slideMode == SLIDE_MODE_NO) {

                    val vMove = abs(moveY) < abs(moveX) / 2
                    if (moveX > minMoveX && vMove) {
                        slideMode = SLIDE_MODE_RIGHT
                        v.cancelLongPress() // 取消长按事件，避免与左右滑事件的冲突
                        Log.v("greyson", "TouchEvent move: first right!")

                        return@setOnTouchListener true

                    } else if (moveX < -minMoveX && vMove) {
                        slideMode = SLIDE_MODE_LEFT
                        v.cancelLongPress()
                        Log.v("greyson", "TouchEvent move: first left??")
                    }

                } else if (slideMode == SLIDE_MODE_RIGHT) {
                    Log.v("greyson", "TouchEvent move: right Mode!")

                } else if (slideMode == SLIDE_MODE_LEFT) {
                    Log.v("greyson", "TouchEvent move: left send view??")
                    return@setOnTouchListener true

                }

            } else if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL) {
                Log.v("greyson", "TouchEvent final ${event.action}")
                slideMode = SLIDE_MODE_NO

                firstTouchX = 0f
                firstTouchY = 0f
            }

            false
        }

    }

    private fun log(str: String) {
        toast(str)
        Log.d("greyson", str)
    }

    private fun toast(str: String) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show()
    }

}