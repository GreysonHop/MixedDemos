package com.testdemo.testNestedScroll.raw

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.PointF
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.animation.addListener
import androidx.core.view.NestedScrollingParent2
import androidx.core.view.ViewCompat
import androidx.core.view.marginTop
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * 复制自 CollapsableScrollView 类：（需要看日志的话，去看那个类的这个时间点）
 * Created by Greyson on 2023/3/20.
 */
class CollapsableRecyclerView : FrameLayout, NestedScrollingParent2, ValueAnimator.AnimatorUpdateListener {
    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private var shrinkFirstChild: View? = null
    lateinit var childToShrink: View // 将要被压缩、拉伸的组件
    lateinit var childScrollable: View
    lateinit var topLayoutManager: CustomLayoutManager

    //    private var shrinkValueAnimator: ValueAnimator = ValueAnimator().apply {
//        duration = 300
//        addUpdateListener(this@CollapsableScrollView)
//    }
    private var translateValueAnimator: ValueAnimator = ValueAnimator().apply {
        duration = 300
        addUpdateListener(this@CollapsableRecyclerView)
        addListener(onEnd = {
            Log.w("greyson", "animate end=$isAutoAnimating")
            isAutoAnimating = false
        }, onCancel = {
            Log.w("greyson", "animate cancel=$isAutoAnimating")
            isAutoAnimating = false
        })
    }

    private var shrinkViewOriMarginTop = 0
    private lateinit var shrinkOriLayoutParams: MarginLayoutParams //MarginLayoutParams(0, 0)

    private var shrinkViewMinHeight = 0  // 可伸缩 View 的压缩后高度
    private var shrinkViewMaxHeight = 0
    private var scrollViewOriginY = 0 // 横向 RV 的原始 Bottom

    var horizontalScrolling = false
    var verticalScrolling = false
    var touchSlop = 0

    //    var shouldInitOffset = true // 判断当前是哪个方向滚动，数据是否应该重新收集
    val offset = IntArray(2)
    var firstTouchPoint = PointF()
    var init = false //
    var isAutoAnimating = false // 正在执行自动收缩（或伸展）、位移动画
    var willNestFling = false // 当前开始的嵌套滚动是猛抛（fling）导致的

    @Deprecated("just for test")
    var orderTest = 0

    private var mIsCollapse = false // 可伸缩视图是否为收缩状态
    private var mIsAnimating = false // 可伸缩视图正在收缩或展开


    private fun initChildView() {
        if (!init) {
            init = true

            childToShrink = getChildAt(0)
            shrinkViewOriMarginTop = childToShrink.marginTop
            childScrollable = getChildAt(1)
        }

        val shrinkView = childToShrink
        if (shrinkFirstChild == null) {
//            if (shrinkView is ViewGroup && shrinkView.childCount > 1) {
//                val rv = shrinkView.getChildAt(1)
            if (shrinkView is RecyclerView && shrinkView.childCount > 0) {
                shrinkFirstChild = shrinkView.getChildAt(0).also {
                    shrinkOriLayoutParams = MarginLayoutParams(it.layoutParams as MarginLayoutParams)
                    shrinkOriLayoutParams.width = it.width
                    shrinkOriLayoutParams.height = it.height
//                    shrinkFirstChildOrgWidth = it.width
//                    shrinkFirstChildOrgHeight = it.height
                }

            }
//            }
        }

    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {

        return super.dispatchTouchEvent(ev)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        /*val scrollViewY = (childScrollable.y).toInt()
        val shrinkViewCantReact = (scrollViewY > scrollViewOriginY && ev.y.toInt() <= scrollViewY)
//                || scrollViewY <= scrollViewOriginY && ev.y.toInt() <= scrollViewY
        if (shrinkViewCantReact) return true*/
        return super.onInterceptTouchEvent(ev)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        initChildView()

        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        childScrollable.layoutParams = childScrollable.layoutParams.apply {
            height = measuredHeight
        } // 修改第二个组件，不会改变高度的组件的高度

        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        if (shrinkViewMinHeight <= 0) shrinkViewMinHeight = childToShrink.measuredHeight
        if (shrinkViewMaxHeight == 0) shrinkViewMaxHeight = measuredWidth

        val shrinkLP = childToShrink.layoutParams as MarginLayoutParams
        val shrinkMarginTop = shrinkLP.topMargin // 横向 RV 的实时 MarginTop
        if (scrollViewOriginY == 0) scrollViewOriginY = shrinkViewMinHeight + shrinkViewOriMarginTop

        childToShrink.let {
            val shrinkViewBottom = shrinkMarginTop + it.measuredHeight
            it.layout(0, shrinkMarginTop, measuredWidth, shrinkViewBottom)
        }

        Log.i("greyson", "onLayout: shrink's margin start=${shrinkLP.marginStart}" +
                ", top=$shrinkMarginTop, end=${shrinkLP.marginEnd}, bot=${shrinkLP.bottomMargin}\n" +
                ", height=${shrinkLP.height}, width=${shrinkLP.width}, scrollOriginTop=$scrollViewOriginY,\n" +
                "scrollView's Y=${childScrollable.y}, ty=${childScrollable.translationY}, top=${childScrollable.top}")

        childScrollable.layout(0,
                scrollViewOriginY, // 这个一直保持初始值，不能变！！
                measuredWidth,
                scrollViewOriginY + childScrollable.measuredHeight
        )
    }

    override fun onStartNestedScroll(child: View, target: View, axes: Int, type: Int): Boolean {
        val ret = axes and ViewCompat.SCROLL_AXIS_VERTICAL != 0
        Log.d("greyson", "onStartNestedScroll， type=$type, axes=$axes, ret=$ret,  order=$orderTest")
        orderTest += 1

        willNestFling = type == ViewCompat.TYPE_NON_TOUCH
        translateValueAnimator.cancel()
//        shrinkValueAnimator.cancel()
        return ret
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        if (touchSlop == 0) {
            val vc = ViewConfiguration.get(context)
            touchSlop = vc.scaledTouchSlop
        }

        offset[0] += dx
        offset[1] += dy
        Log.d("greyson", "onNestedPreScroll: offset=${offset[0]}-${offset[1]}。slop=$touchSlop")
        if (!verticalScrolling && !horizontalScrolling) {
            if (abs(offset[1]) > touchSlop) {
                verticalScrolling = true
                (childScrollable as? RecyclerView)?.scrollToPosition(0)
            } else if (abs(offset[0]) > touchSlop) {
                horizontalScrolling = true
                // 可能有些动荡，修复一下位置
            }
        }

        /*if (ev.actionMasked == MotionEvent.ACTION_DOWN) {
            firstTouchPoint.y = ev.y
            firstTouchPoint.x = ev.x
            horizontalScrolling = false
            verticalScrolling = false

        } else if (ev.actionMasked == MotionEvent.ACTION_MOVE) {
            if (touchSlop == 0) {
                val vc = ViewConfiguration.get(context)
                touchSlop = vc.scaledTouchSlop
            }

            if ((ev.y - firstTouchPoint.y) > touchSlop) {
                horizontalScrolling = false
                verticalScrolling = true
            } else if (ev.x - firstTouchPoint.x > touchSlop) {
                horizontalScrolling = true
                verticalScrolling = false
            }
        }*/
        //跟随手势滑动
        gestureMove(dx, dy, consumed)
    }

    override fun onNestedScrollAccepted(child: View, target: View, axes: Int, type: Int) {
    }

    override fun onNestedScroll(target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int, type: Int) {
        Log.d("greyson", "Shrink_onNestedScroll: dxCon=$dxConsumed, dyCon=$dyConsumed, dxUnCon=$dxUnconsumed, dyUnCon=$dyUnconsumed")

    }

    override fun onStopNestedScroll(target: View, type: Int) {
        //此时 childViewY 必为 3个标志位之一，判断不为这三个数值就自动滑动
        val scrollViewY = childScrollable.y.toInt()
        Log.w("greyson", "onStopNestedScroll:type=$type, scrollViewY=$scrollViewY， scrollViewTY=${childScrollable.translationY}, willFling=$willNestFling,  order=$orderTest")
        orderTest += 1

        if (!willNestFling) {
            verticalScrolling = false
            horizontalScrolling = false
            offset[0] = 0
            offset[1] = 1
        }

        if (scrollViewY != scrollViewOriginY && scrollViewY != 0 && scrollViewY != shrinkViewMaxHeight
                && !willNestFling) {
            autoScrollInternal()
        }
        willNestFling = false
    }

    override fun onNestedPreFling(target: View, velocityX: Float, velocityY: Float): Boolean {
        Log.d("greyson", "onNestedPreFling: x=$velocityX, y=$velocityY,  order=$orderTest")
        orderTest += 1

//        return false
        return super.onNestedPreFling(target, velocityX, velocityY)
    }

    override fun onNestedFling(target: View, velocityX: Float, velocityY: Float, consumed: Boolean): Boolean {
        Log.d("greyson", "onNestedFling: x=$velocityX, y=$velocityY,  order=$orderTest")
        orderTest += 1
//        return false
        return super.onNestedFling(target, velocityX, velocityY, consumed)
    }

    private var state = 0 //

    companion object {
        const val STATE_OUT = 3
        const val STATE_EXPANDED = 2
        const val STATE_NORMAL = 1

        const val EVENT_TRANSLATE_OUT = 1
        const val EVENT_TRANSLATE_IN = 2
        const val EVENT_EXPAND = 3
        const val EVENT_COLLAPSE = 4
    }

    private fun gestureMove(dx: Int, dy: Int, consumed: IntArray) {
        /* 能向下滑动${target.canScrollVertically(-1)} */

        val shrinkViewY = (childToShrink.y).toInt()
        val scrollViewY = (childScrollable.y).toInt()

        val curShrinkViewHeight = childToShrink.height

        Log.i("greyson", "gestureMove: dy=$dy, dx=$dx, canScrollVertically=${childScrollable.canScrollVertically(-1)}" +
//                ", \nshrinkViewMinHeight=$shrinkViewMinHeight, shrinkViewMaxHeight=$shrinkViewMaxHeight, ty=${childScrollable.translationY}" +
                " \nshrinkViewY=$shrinkViewY, scrollViewY=$scrollViewY curShrinkViewHeight=$curShrinkViewHeight, scrollViewOriginY=$scrollViewOriginY" +
                "\n horizontalScrolling=$horizontalScrolling, verticalScrolling=$verticalScrolling")

        if (horizontalScrolling) {
            consumed[1] = dy
            return
        } else if (verticalScrolling) {
            consumed[0] = dx
//            return // 纵向滚动还需要执行后面的动画
        }

        val scrollUp = dy > 0
        val offset = abs(dy)
        if (scrollViewY == 0) { // 伸缩视图完全被移出界面的情况

            if (!scrollUp && !childScrollable.canScrollVertically(-1)) {
                childToShrink.y = min(shrinkViewY + offset.toInt(), shrinkViewOriMarginTop).toFloat()
                childScrollable.y = min(scrollViewY + offset.toInt(), scrollViewOriginY).toFloat()
                consumed[1] = dy.toInt()
                scrollingInternal(scrollUp, offset, EVENT_TRANSLATE_IN)
            }

        } else if (scrollViewY > 0 && scrollViewY < scrollViewOriginY) {
            // 伸缩视图正在移出界面，还没到临界点的情况
            if (scrollUp) {
                childToShrink.y = max(shrinkViewY - offset.toInt(), -scrollViewOriginY).toFloat()
                childScrollable.y = max(scrollViewY - offset.toInt(), 0).toFloat()
                scrollingInternal(scrollUp, offset, EVENT_TRANSLATE_OUT)

            } else {
                childToShrink.y = min(shrinkViewY + offset.toInt(), shrinkViewOriMarginTop).toFloat()
                childScrollable.y = min(scrollViewY + offset.toInt(), scrollViewOriginY).toFloat()
                scrollingInternal(scrollUp, offset, EVENT_TRANSLATE_IN)
            }
            consumed[1] = dy.toInt()

        } else if (scrollViewY == scrollViewOriginY) {
            // 伸缩视图 压缩状态：

            if (scrollUp) {
                // else 向上不能再压缩。
                if (shrinkViewY > -scrollViewOriginY) {
                    childToShrink.y = max(shrinkViewY - offset.toInt(), -scrollViewOriginY).toFloat()
                    val scrollViewNewY = max(scrollViewY - offset.toInt(), 0).toFloat()
                    childScrollable.y = scrollViewNewY
//                    Log.w("greyson", "gestureMove伸缩状态向下: scrollView new y=${scrollViewNewY}, shrink new height=$height, ty=${childScrollable.translationY}")

                    consumed[1] = dy.toInt()
                    scrollingInternal(scrollUp, offset, EVENT_TRANSLATE_OUT)
                }

            } else {

                if (!childScrollable.canScrollVertically(-1) && !childToShrink.canScrollHorizontally(-1)) {
                    childScrollable.y = min(scrollViewY + offset.toInt(), shrinkViewMaxHeight).toFloat()
                    scrollingInternal(scrollUp, offset, EVENT_EXPAND)

//                    Log.w("greyson", "gestureMove伸缩状态向下: scrollView new y=${scrollViewNewY}, shrink new height=$height, ty=${childScrollable.translationY}")
                    consumed[1] = dy.toInt()
                }

            }

        } else if (scrollViewY > scrollViewOriginY && scrollViewY < shrinkViewMaxHeight) {
            // 伸缩视图在伸展和压缩状态之间变化的情况

            if (scrollUp) {
                childScrollable.y = max(scrollViewY - offset.toInt(), scrollViewOriginY).toFloat()
                scrollingInternal(scrollUp, offset, EVENT_COLLAPSE)

            } else {
                childScrollable.y = min(scrollViewY + offset.toInt(), shrinkViewMaxHeight).toFloat()
                scrollingInternal(scrollUp, offset, EVENT_EXPAND)
//                Log.w("greyson", "gestureMove: scrollView new y=${scrollViewNewY}, shrink new height=$height, ty=${childScrollable.translationY}")

            }
            consumed[1] = dy.toInt()

        } else if (scrollViewY == shrinkViewMaxHeight) {
            // 伸缩视图 伸展状态：

            if (scrollUp) {
                childScrollable.y = max(scrollViewY - offset.toInt(), scrollViewOriginY).toFloat()
                scrollingInternal(scrollUp, offset, EVENT_COLLAPSE)

                consumed[1] = dy.toInt()
            }

        }

    }

    override fun onAnimationUpdate(animation: ValueAnimator?) {
        /*if (animation == shrinkValueAnimator) {

        } else */if (animation == translateValueAnimator) {

            val value = animation.animatedValue as Float
            val dy = value - childScrollable.y
            val scrollUp = dy < 0
            childScrollable.y = value
            scrollingInternal(scrollUp, abs(dy.toInt()), if (scrollUp) EVENT_COLLAPSE else EVENT_EXPAND)

        }
    }

    private fun autoScrollInternal() {
        val scrollViewY = childScrollable.y.toInt()
//        Log.w("greyson", "autoScrollInternal: isAutoAnimating=$isAutoAnimating")
        if (scrollViewY > scrollViewOriginY && scrollViewY < shrinkViewMaxHeight && !isAutoAnimating) {
            var startPos = 0
            var endPos = 0
            val middle = (shrinkViewMaxHeight - scrollViewOriginY) / 2 + scrollViewOriginY
            if (scrollViewY > middle) {
//                shrinkValueAnimator.setFloatValues(scrollViewY.toFloat(), shrinkViewMaxHeight.toFloat())
                translateValueAnimator.setFloatValues(scrollViewY.toFloat(), shrinkViewMaxHeight.toFloat())

            } else {
//                shrinkValueAnimator.setFloatValues(scrollViewY.toFloat(), scrollViewOriginY.toFloat())
                translateValueAnimator.setFloatValues(scrollViewY.toFloat(), scrollViewOriginY.toFloat())

            }

            Log.w("greyson", "animate start=$isAutoAnimating")
            isAutoAnimating = true
//            shrinkValueAnimator.start()
            translateValueAnimator.start()

        }

    }

    private fun scrollingInternal(scrollUp: Boolean, offset: Int, event: Int) {
        Log.w("greyson", "scrollingInternal: scrollUp=$scrollUp, new offset=$offset, event=$event\n")
        // 压缩横向 RV 时的一些参数。压缩过程的当前百分比
        val totalOffset = shrinkViewMaxHeight - scrollViewOriginY
        val offsetFromMin = childScrollable.y - scrollViewOriginY
//      val offsetFromMin = shrinkViewMaxHeight - childScrollable.y
        val ratioFromMin = offsetFromMin / totalOffset.toFloat()

        val shrinkViewLP = childToShrink.layoutParams as MarginLayoutParams
        val curShrinkViewHeight = childToShrink.height
        // 横向 RV 自己的动画
        if (event == EVENT_COLLAPSE) {
            // 压缩
            val newMargin = (shrinkViewOriMarginTop * (1 - ratioFromMin)).toInt()
            val marginOffset = newMargin - shrinkViewLP.topMargin

            val newHeight = (curShrinkViewHeight - offset) - abs(marginOffset)
            val height = max(shrinkViewMinHeight, newHeight.toInt())

            shrinkViewLP.height = height
            shrinkViewLP.topMargin = newMargin
            childToShrink.layoutParams = shrinkViewLP
            Log.w("greyson", "scrolling collapse: shrink new y=${shrinkViewLP.topMargin}, new height=$height\n")

        } else if (event == EVENT_EXPAND) {
            // 向下伸展
            val newMargin = (shrinkViewOriMarginTop * (1 - ratioFromMin)).toInt()
            val marginOffset = newMargin - shrinkViewLP.topMargin

            val newHeight = (curShrinkViewHeight + offset) + abs(marginOffset)
            val height = min(shrinkViewMaxHeight, newHeight.toInt())

            shrinkViewLP.height = height
            shrinkViewLP.topMargin = newMargin
            childToShrink.layoutParams = shrinkViewLP
            Log.w("greyson", "scrolling expand: shrink new y=${shrinkViewLP.topMargin}, new height=$height\n")

        }


        // 横向 rv 子 View动画
        if (event == EVENT_COLLAPSE || event == EVENT_EXPAND) {

            shrinkFirstChild?.let { target ->
                val targetParent = target.parent
//            Log.i("greyson", "scrolling(): shrinkFirstChildOrgWidth=${shrinkOriLayoutParams.width}" +
//                    ", ${target.javaClass.name}, parent=${targetParent.javaClass.name}")
                if (targetParent is ViewGroup) {

                    val parentWidth = targetParent.width
                    val totalWidthOffset = parentWidth - shrinkOriLayoutParams.width
                    val totalHeightOffset = /*targetParent.height*/ shrinkViewMaxHeight - shrinkOriLayoutParams.height

//                    Log.w("greyson", "scrolling: ratio=$ratioFromMin, parentWid=$parentWidth, offsetFromMin=$offsetFromMin")

                    val targetLP = target.layoutParams as MarginLayoutParams

//                    Log.w("greyson", "targetLP: start=${targetLP.marginStart}, top=${targetLP.topMargin}, end=${targetLP.marginEnd}, bot=${targetLP.bottomMargin}")
                    Log.w("greyson", "shrinkOriLayoutParams: start=${shrinkOriLayoutParams.marginStart}, top=${shrinkOriLayoutParams.topMargin}, end=${shrinkOriLayoutParams.marginEnd}, bot=${shrinkOriLayoutParams.bottomMargin}")

                    // 注意：margin 是从大到小。跟宽度从小到大不一样。所以比率是相反的
                    val newMarginStart = (shrinkOriLayoutParams.marginStart * (1 - ratioFromMin)).toInt()
                    val newMarginTop = (shrinkOriLayoutParams.topMargin * (1 - ratioFromMin)).toInt()
                    val newMarginEnd = (shrinkOriLayoutParams.marginEnd * (1 - ratioFromMin)).toInt()
                    val newMarginBottom = (shrinkOriLayoutParams.bottomMargin * (1 - ratioFromMin)).toInt()
                    targetLP.marginStart = newMarginStart
                    targetLP.marginEnd = newMarginEnd
                    targetLP.topMargin = newMarginTop
                    targetLP.bottomMargin = newMarginBottom

                    Log.w("greyson", "targetLP new !: start=${targetLP.marginStart}, top=${targetLP.topMargin}, end=${targetLP.marginEnd}, bot=${targetLP.bottomMargin}")

                    // 设置横向 RV 的子 View 的宽高
                    targetLP.width = (totalWidthOffset * ratioFromMin).toInt() + shrinkOriLayoutParams.width
                    targetLP.height = (totalHeightOffset * ratioFromMin).toInt() + shrinkOriLayoutParams.height

                    target.layoutParams = targetLP

//                    if (targetParent is RecyclerView) targetParent.scrollToPosition(0)
                }

            }

        }
    }

}