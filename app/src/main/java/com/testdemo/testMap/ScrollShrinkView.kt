package com.testdemo.testMap

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.core.view.NestedScrollingParent2
import androidx.core.view.ViewCompat
import com.testdemo.util.broken_lib.Utils

/**
 * Create by Greyson
 */
class ScrollShrinkView : LinearLayout, NestedScrollingParent2 {
    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    var mViewToShrink: View? = null
//    lateinit var mScrollingView: RecyclerView

    private var shrinkViewMinHeight = 0
    private var shrinkViewMaxHeight = 0

    override fun onFinishInflate() {
        super.onFinishInflate()
        mViewToShrink = getChildAt(0)
        /*val viewGroup = getChildAt(1) as ViewGroup
        for (index in 0..viewGroup.childCount) {
            if (viewGroup.getChildAt(index) is RecyclerView) {
                mScrollingView = viewGroup.getChildAt(index) as RecyclerView
                break
            }
        }*/
        mViewToShrink?.post {
            shrinkViewMinHeight = mViewToShrink?.let { it.height - mCollapseOffset } ?: mCollapseOffset
            shrinkViewMaxHeight = mViewToShrink?.height ?: mCollapseOffset * 2
        }

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

    }

    override fun onStartNestedScroll(child: View, target: View, axes: Int, type: Int): Boolean {
        Log.d("greyson", "onStartNestedScroll， ")
        if (type == ViewCompat.TYPE_NON_TOUCH) {
            return false
        }
        return axes and ViewCompat.SCROLL_AXIS_VERTICAL != 0
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        val currentShrinkViewHeight = mViewToShrink?.height ?: shrinkViewMinHeight
        Log.i("greyson", "onNestedPreScroll: dy=$dy, 能向下滑动${target.canScrollVertically(-1)}" +
                ", shrinkViewMinHeight=$shrinkViewMinHeight, shrinkViewMaxHeight=$shrinkViewMaxHeight, scrollHeight=$currentShrinkViewHeight")

        if (dy < 0 && !target.canScrollVertically(-1) && currentShrinkViewHeight <= shrinkViewMaxHeight) {//向下滑动，想放大
            updateHeight(dy)
//            extendMap(true)
            consumed[1] = dy

        } else if (dy > 0 && /*currentShrinkViewHeight <= shrinkViewMaxHeight && */currentShrinkViewHeight > shrinkViewMinHeight) {
            updateHeight(dy)
//            extendMap(false)
            consumed[1] = dy
        }
    }

    override fun onNestedScrollAccepted(child: View, target: View, axes: Int, type: Int) {
    }

    override fun onNestedScroll(target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int, type: Int) {
    }

    override fun onStopNestedScroll(target: View, type: Int) {}

    override fun onNestedFling(target: View, velocityX: Float, velocityY: Float, consumed: Boolean): Boolean {
        return false
    }


    private fun updateHeight(dy: Int) {
        mViewToShrink?.let {
            val lp = it.layoutParams
            lp.height -= dy
            it.layoutParams = lp
        }
    }

    private var mCollapseOffset = Utils.dp2px(100)//地图视图能收缩的长度
    private var mIsCollapse = false//地图视图是否为收缩状态
    private var mIsAnimating = false//地图视图正在收缩或展开
    private fun extendMap(toExtendMap: Boolean) {
        mViewToShrink?.let { view ->
            mIsAnimating = true
            val height = if (view.layoutParams.height <= 0) view.height else view.layoutParams.height
            val collapseAnim = ValueAnimator.ofInt(height, height + if (toExtendMap) mCollapseOffset else -mCollapseOffset)

            collapseAnim.addUpdateListener {
                val value = it.animatedValue
                if (value is Int) {
                    val lp = view.layoutParams
                    lp.height = value
                    view.layoutParams = lp
                }
            }

            collapseAnim.addListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator?) {}
                override fun onAnimationCancel(animation: Animator?) {
                    Log.i("greyson", "onAnimationCancel: mIsAnimating=$mIsAnimating - mIsCollapse=$mIsCollapse")
                }

                override fun onAnimationEnd(animation: Animator?) {
                    Log.i("greyson", "onAnimationEnd: mIsAnimating=$mIsAnimating - mIsCollapse=$mIsCollapse")
                    mIsAnimating = false
                    mIsCollapse = !toExtendMap
                }

                override fun onAnimationStart(animation: Animator?) {
                    Log.i("greyson", "onAnimationStart: mIsAnimating=$mIsAnimating - mIsCollapse=$mIsCollapse")
                    mIsAnimating = true
                }
            })
            collapseAnim.duration = 300
            collapseAnim.start()
        }
    }
}