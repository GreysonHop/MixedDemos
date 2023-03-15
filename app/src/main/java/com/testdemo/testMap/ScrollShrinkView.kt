package com.testdemo.testMap

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.core.view.NestedScrollingParent2
import androidx.core.view.ViewCompat
import com.testdemo.util.broken_lib.Utils
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * Create by Greyson
 */
class ScrollShrinkView : FrameLayout, NestedScrollingParent2 {
    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    lateinit var mViewToShrink: View // 将要被压缩、拉伸的组件
    lateinit var mScrollingChild: View

//    private var shrinkViewHeight = 0 // 可伸缩 View 的原始高度
    private var shrinkViewMinHeight = 0  // 可伸缩 View 的压缩后高度
    private var shrinkViewMaxHeight = 0

    override fun onFinishInflate() {
        super.onFinishInflate()
        /*val viewGroup = getChildAt(1) as ViewGroup
        for (index in 0..viewGroup.childCount) {
            if (viewGroup.getChildAt(index) is RecyclerView) {
                mScrollingView = viewGroup.getChildAt(index) as RecyclerView
                break
            }
        }*/
//todo 可能得刷新一下
    }

    var init = false
    private fun initChildView() {
        if (init) return
        init = true

        mViewToShrink = getChildAt(0)
        mScrollingChild = getChildAt(1)

        shrinkViewMaxHeight = mViewToShrink.layoutParams.height

        shrinkViewMinHeight = shrinkViewMaxHeight -  mCollapseOffset
//        shrinkViewMaxHeight = shrinkViewHeight
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        initChildView()

        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        /*mViewToShrink.layoutParams = mViewToShrink.layoutParams.apply {
            height = shrinkViewMaxHeight
        }*/
        mScrollingChild.layoutParams = mScrollingChild.layoutParams.apply {
            height = measuredHeight - shrinkViewMinHeight
        } // 修改第二个组件，不会改变高度的组件的高度

        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        mViewToShrink.let {
            it.layout(0, 0, measuredWidth, it.measuredHeight)
        }

        val scrollTop = shrinkViewMaxHeight
        mScrollingChild.layout(0,
            scrollTop,
            measuredWidth,
            scrollTop + mScrollingChild.measuredHeight
        )
    }

    override fun onStartNestedScroll(child: View, target: View, axes: Int, type: Int): Boolean {
        Log.d("greyson", "onStartNestedScroll， ")
        if (type == ViewCompat.TYPE_NON_TOUCH) {
            return false
        }
        return axes and ViewCompat.SCROLL_AXIS_VERTICAL != 0
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        //跟随手势滑动
        gestureMove(dy.toFloat(), consumed)
    }

    override fun onNestedScrollAccepted(child: View, target: View, axes: Int, type: Int) {
    }

    override fun onNestedScroll(target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int, type: Int) {
        Log.d("greyson", "Shrink_onNestedScroll: dxCon=$dxConsumed, dyCon=$dyConsumed, dxUnCon=$dxUnconsumed, dyUnCon=$dyUnconsumed")

    }

    override fun onStopNestedScroll(target: View, type: Int) {}

    override fun onNestedFling(target: View, velocityX: Float, velocityY: Float, consumed: Boolean): Boolean {
        return false
    }


    private fun gestureMove(dy: Float, consumed: IntArray) {
        /* 能向下滑动${target.canScrollVertically(-1)} */

        val shrinkViewY = (mViewToShrink.y).toInt()
        val scrollViewY = (mScrollingChild.y).toInt()

        val shrinkViewLP = mViewToShrink.layoutParams
        val curShrinkViewHeight = shrinkViewLP.height

        Log.i("greyson", "onNestedPreScroll: dy=$dy, canScrollVertically=${canScrollVertically(-1)}" +
                ", shrinkViewMinHeight=$shrinkViewMinHeight, shrinkViewMaxHeight=$shrinkViewMaxHeight," +
                " \nshrinkViewY=$shrinkViewY, scrollViewY=$scrollViewY realShrinkViewHeight=$curShrinkViewHeight")

        val scrollUp = dy > 0
        val offset = abs(dy)
        if (scrollViewY == shrinkViewMinHeight ) { // 伸缩视图压缩状态：

            if (!scrollUp && !mScrollingChild.canScrollVertically(-1)) {
                // 向下伸展
                val height = min(shrinkViewMaxHeight, shrinkViewLP.height + offset.toInt())
                shrinkViewLP.height = height
                mViewToShrink.layoutParams = shrinkViewLP

                mScrollingChild.y = min(scrollViewY + (-dy.toInt()), shrinkViewMaxHeight).toFloat()

                consumed[1] = dy.toInt()
                scrolling(dy)
            } // else 向上不能再压缩。

        } else if (scrollViewY == shrinkViewMaxHeight) { // 伸缩视图伸展状态：

            if (scrollUp) {
                // 压缩
                val height = max(shrinkViewMinHeight, shrinkViewLP.height - dy.toInt())
                shrinkViewLP.height = height
                mViewToShrink.layoutParams = shrinkViewLP

                mScrollingChild.y = max(scrollViewY - dy.toInt(), shrinkViewMinHeight).toFloat()

                consumed[1] = dy.toInt()
                scrolling(dy)
            }

        } else if (scrollViewY > shrinkViewMinHeight && scrollViewY < shrinkViewMaxHeight) {
            if (scrollUp) {
                val height = max(shrinkViewMinHeight, shrinkViewLP.height - dy.toInt())
                shrinkViewLP.height = height
                mViewToShrink.layoutParams = shrinkViewLP

                mScrollingChild.y = max(scrollViewY - dy.toInt(), shrinkViewMinHeight).toFloat()

                consumed[1] = dy.toInt()
                scrolling(dy)
            } else {
                val height = min(shrinkViewMaxHeight, shrinkViewLP.height + offset.toInt())
                shrinkViewLP.height = height
                mViewToShrink.layoutParams = shrinkViewLP

                mScrollingChild.y = min(scrollViewY + (-dy.toInt()), shrinkViewMaxHeight).toFloat()

                consumed[1] = dy.toInt()
                scrolling(dy)
            }
        }

        /*if (dy < 0
            && scrollViewY >= shrinkViewMinHeight
            && scrollViewY <= shrinkViewMaxHeight
            && shrinkViewY == 0 && mScrollingChild.canScrollVertically(-1)) {
            // 从小的状态开始 拉伸
            val shrinkOffset = max(-dy.toInt(), shrinkViewMaxHeight - curShrinkViewHeight)
            shrinkViewLP.height = shrinkViewLP.height + shrinkOffset
            mViewToShrink.layoutParams = shrinkViewLP

//            val scrollerOffset = max(-dy.toInt(), shrinkViewMaxHeight - scrollViewY)
//            mScrollingChild.y = (scrollViewY + scrollerOffset).toFloat()
            mScrollingChild.y = min(scrollViewY + (-dy.toInt()), shrinkViewMaxHeight).toFloat()

            consumed[1] = dy.toInt()
            scrolling(dy)

        } else if (dy > 0
                && scrollViewY >= shrinkViewMinHeight
                && scrollViewY <= shrinkViewMaxHeight
                && shrinkViewY == 0) {
            // 从大的状态开始 压缩
            val height = max(shrinkViewMinHeight, shrinkViewLP.height - dy.toInt())
//            val shrinkOffset = max(-dy.toInt(), shrinkViewMaxHeight - curShrinkViewHeight)
//            shrinkViewLP.height = shrinkViewLP.height + shrinkOffset
            shrinkViewLP.height = height
            mViewToShrink.layoutParams = shrinkViewLP

            mScrollingChild.y = max(scrollViewY - dy.toInt(), shrinkViewMinHeight).toFloat()

            consumed[1] = dy.toInt()
            scrolling(dy)

        } else if (dy < 0 && scrollViewY >= shrinkViewMinHeight
                && scrollViewY <= shrinkViewMaxHeight && shrinkViewY == 0
                ) {
            // 向下拉，但必须是可滚动的对象不能再向下拉了！

        }*/


        /*if (dy < 0 && !target.canScrollVertically(-1) && currentShrinkViewHeight <= shrinkViewMaxHeight) {//向下滑动，想放大
            updateHeight(dy)
//            extendMap(true)
            consumed[1] = dy

        } else if (dy > 0 && *//*currentShrinkViewHeight <= shrinkViewMaxHeight && *//*currentShrinkViewHeight > shrinkViewMinHeight) {
            updateHeight(dy)
//            extendMap(false)
            consumed[1] = dy
        }*/
    }

    private fun scrolling(dy: Float) {

    }

    private fun updateHeight(dy: Int) {
        mViewToShrink?.let {
            val lp = it.layoutParams
            lp.height -= dy
            it.layoutParams = lp
        }
    }

    fun getOffset(offset: Float, maxOffset: Float): Float {
        return if (offset > maxOffset) {
            maxOffset
        } else offset
    }


    private var mCollapseOffset = Utils.dp2px(100) // 地图视图能收缩的长度
//    private val shrinkViewHeight = Utils.dp2px(300) //
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