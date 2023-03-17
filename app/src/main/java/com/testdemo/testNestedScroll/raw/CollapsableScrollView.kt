package com.testdemo.testNestedScroll.raw

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.NestedScrollingParent2
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.testdemo.util.broken_lib.Utils
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * ScrollView中
 * Created by Greyson on 2023/3/15.
 */
class CollapsableScrollView : FrameLayout, NestedScrollingParent2 {
    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private var shrinkFirstChild: View? = null
    lateinit var childToShrink: View // 将要被压缩、拉伸的组件
    lateinit var childScrollable: View

    private lateinit var shrinkOriLayoutParams: MarginLayoutParams //MarginLayoutParams(0, 0)
//    private var shrinkFirstChildOrgWidth = 0
//    private var shrinkFirstChildOrgHeight = 0
    private var shrinkViewMinHeight = 0  // 可伸缩 View 的压缩后高度
    private var shrinkViewMaxHeight = 0

    private var mCollapseOffset = Utils.dp2px(100) // 可伸缩视图能收缩的长度

    // private val shrinkViewHeight = Utils.dp2px(300) // 可伸缩 View 的原始高度
    private var mIsCollapse = false // 可伸缩视图是否为收缩状态
    private var mIsAnimating = false // 可伸缩视图正在收缩或展开


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
        if (!init) {
            init = true

            childToShrink = getChildAt(0)
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

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        initChildView()

        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        childScrollable.layoutParams = childScrollable.layoutParams.apply {
            height = measuredHeight
        } // 修改第二个组件，不会改变高度的组件的高度

        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        if (shrinkViewMinHeight <=0 ) shrinkViewMinHeight = childToShrink.measuredHeight
        if (shrinkViewMaxHeight == 0) shrinkViewMaxHeight = measuredWidth

        val shrinkLP = childToShrink.layoutParams as MarginLayoutParams
        val shrinkTop = shrinkLP.topMargin
        val scrollTop = shrinkViewMinHeight

        childToShrink.let {
            it.layout(0, 0, measuredWidth, it.measuredHeight)
            it.measuredHeight
        }
        Log.i("greyson", "onLayout: shrink's margin start=${shrinkLP.marginStart}" +
                ", top=$shrinkTop, end=${shrinkLP.marginEnd}, bot=${shrinkLP.bottomMargin}\n" +
                ", height=${shrinkLP.height}, width=${shrinkLP.width}, scrollTop=$scrollTop,\n srollY=${childScrollable.y}-${childScrollable.translationY}")
// 为什么会有 translationY ？？

        childScrollable.layout(0,
                scrollTop, // 这个一直保持初始值，不能变！！
                measuredWidth,
                scrollTop + childScrollable.measuredHeight
        )
    }

    override fun onStartNestedScroll(child: View, target: View, axes: Int, type: Int): Boolean {
        val ret = axes and ViewCompat.SCROLL_AXIS_VERTICAL != 0
        Log.d("greyson", "onStartNestedScroll， type=$type, axes=$axes, ret=$ret")
        /*if (type == ViewCompat.TYPE_NON_TOUCH) {
            return false
        }*/
        return ret
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

    override fun onStopNestedScroll(target: View, type: Int) {
        //此时 childViewY 必为 3个标志位之一，判断不为这三个数值就自动滑动
        val scrollViewY: Int = childScrollable.y.toInt()
        Log.w("greyson", "onStopNestedScroll=$type, $target, $scrollViewY， ${childScrollable.translationY}")

        if (scrollViewY != shrinkViewMinHeight && scrollViewY != 0 && scrollViewY != shrinkViewMaxHeight) {
            autoScroll()
        } else {
//            callBackCalenadarState()
        }
    }

    /*override fun onNestedPreFling(target: View, velocityX: Float, velocityY: Float): Boolean {
        Log.d("greyson", "onNestedPreFling: x=$velocityX, y=$velocityY")
        return false
    }

    override fun onNestedFling(target: View, velocityX: Float, velocityY: Float, consumed: Boolean): Boolean {
        Log.d("greyson", "onNestedFling: x=$velocityX, y=$velocityY")
        return false
    }*/

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

    private fun gestureMove(dy: Float, consumed: IntArray) {
        /* 能向下滑动${target.canScrollVertically(-1)} */

        val shrinkViewY = (childToShrink.y).toInt()
        val scrollViewY = (childScrollable.y).toInt()

        val shrinkViewLP = childToShrink.layoutParams
        val curShrinkViewHeight = childToShrink.height

        Log.i("greyson", "onNestedPreScroll: dy=$dy, canScrollVertically=${childScrollable.canScrollVertically(-1)}" +
                ", \nshrinkViewMinHeight=$shrinkViewMinHeight, shrinkViewMaxHeight=$shrinkViewMaxHeight, ty=${childScrollable.translationY}" +
                " \nshrinkViewY=$shrinkViewY, scrollViewY=$scrollViewY curShrinkViewHeight=$curShrinkViewHeight")

        val scrollUp = dy > 0
        val offset = abs(dy)
        if (scrollViewY == 0) { // 伸缩视图完全被移出界面的情况

            if (!scrollUp && !childScrollable.canScrollVertically(-1)) {
                childToShrink.y = min(shrinkViewY + offset.toInt(), 0).toFloat()
                childScrollable.y = min(scrollViewY + offset.toInt(), shrinkViewMinHeight).toFloat()
                consumed[1] = dy.toInt()
                scrolling(scrollUp, offset, EVENT_TRANSLATE_IN)
            }

        } else if (scrollViewY > 0 && scrollViewY < shrinkViewMinHeight) {
            // 伸缩视图正在移出界面，还没到临界点的情况
            if (scrollUp) {
                childToShrink.y = max(shrinkViewY - offset.toInt(), -curShrinkViewHeight).toFloat()
                childScrollable.y = max(scrollViewY - offset.toInt(), 0).toFloat()
                scrolling(scrollUp, offset, EVENT_TRANSLATE_OUT)

            } else {
                childToShrink.y = min(shrinkViewY + offset.toInt(), 0).toFloat()
                childScrollable.y = min(scrollViewY + offset.toInt(), shrinkViewMinHeight).toFloat()
                scrolling(scrollUp, offset, EVENT_TRANSLATE_IN)
            }
            consumed[1] = dy.toInt()

        } else if (scrollViewY == shrinkViewMinHeight) {
            // 伸缩视图 压缩状态：

            if (scrollUp) {
                // else 向上不能再压缩。
                if (shrinkViewY > -shrinkViewMinHeight) {
                    childToShrink.y = max(shrinkViewY - offset.toInt(), -curShrinkViewHeight).toFloat()
                    val scrollViewNewY = max(scrollViewY - offset.toInt(), 0).toFloat()
                    childScrollable.y = scrollViewNewY
                    Log.w("greyson", "gestureMove伸缩状态向下: scrollView new y=${scrollViewNewY}, shrink new height=$height, ty=${childScrollable.translationY}")

                    consumed[1] = dy.toInt()
                    scrolling(scrollUp, offset, EVENT_TRANSLATE_OUT)
                }

            } else {

                if (!childScrollable.canScrollVertically(-1)) {
                    // 向下伸展
                    val height = min(shrinkViewMaxHeight, curShrinkViewHeight + offset.toInt())
                    shrinkViewLP.height = height
                    childToShrink.layoutParams = shrinkViewLP

                    val scrollViewNewY = min(scrollViewY + (-dy.toInt()), shrinkViewMaxHeight).toFloat()
                    childScrollable.y = scrollViewNewY

                    Log.w("greyson", "gestureMove伸缩状态向下: scrollView new y=${scrollViewNewY}, shrink new height=$height, ty=${childScrollable.translationY}")
                    consumed[1] = dy.toInt()
                    scrolling(scrollUp, offset, EVENT_EXPAND)
                }

            }

        } else if (scrollViewY > shrinkViewMinHeight && scrollViewY < shrinkViewMaxHeight) {
            // 伸缩视图在伸展和压缩状态之间变化的情况

            if (scrollUp) {
                val height = max(shrinkViewMinHeight, curShrinkViewHeight - dy.toInt())
                shrinkViewLP.height = height
                childToShrink.layoutParams = shrinkViewLP

                val scrollViewNewY = max(scrollViewY - dy.toInt(), shrinkViewMinHeight).toFloat()
                childScrollable.y = scrollViewNewY
                Log.w("greyson", "gestureMove之间身上: scrollView new y=${scrollViewNewY}, shrink new height=$height, ty=${childScrollable.translationY}")
                scrolling(scrollUp, offset, EVENT_COLLAPSE)

            } else {
                val height = min(shrinkViewMaxHeight, curShrinkViewHeight + offset.toInt())
                shrinkViewLP.height = height
                childToShrink.layoutParams = shrinkViewLP

                val scrollViewNewY = min(scrollViewY + (-dy.toInt()), shrinkViewMaxHeight).toFloat()
                Log.w("greyson", "gestureMove: scrollView new y=${scrollViewNewY}, shrink new height=$height, ty=${childScrollable.translationY}")
                childScrollable.y = scrollViewNewY
                scrolling(scrollUp, offset, EVENT_EXPAND)

            }
            consumed[1] = dy.toInt()

        } else if (scrollViewY == shrinkViewMaxHeight) {
            // 伸缩视图 伸展状态：

            if (scrollUp) {
                // 压缩
                val height = max(shrinkViewMinHeight, curShrinkViewHeight - dy.toInt())
                shrinkViewLP.height = height
                childToShrink.layoutParams = shrinkViewLP

                childScrollable.y = max(scrollViewY - dy.toInt(), shrinkViewMinHeight).toFloat()

                consumed[1] = dy.toInt()
                scrolling(scrollUp, offset, EVENT_COLLAPSE)
            }

        }

    }

    private fun autoScroll() {
        val scrollViewY = childScrollable.y.toInt()


    }

    private fun shrinkViewOut() {

    }

    private fun scrolling(scrollUp: Boolean, offset: Float, event: Int) {
        shrinkFirstChild?.let { target ->
            val targetParent = target.parent
            Log.i("greyson", "scrolling(): shrinkFirstChildOrgWidth=${shrinkOriLayoutParams.width}" +
                    ", ${target.javaClass.name}, parent=${targetParent.javaClass.name}")
            if (targetParent is ViewGroup) {

                if (event == EVENT_COLLAPSE || event == EVENT_EXPAND) {

                    val totalOffset = shrinkViewMaxHeight - shrinkViewMinHeight
                    val offsetFromMin = childScrollable.y - shrinkViewMinHeight
//                val offsetFromMin = shrinkViewMaxHeight - childScrollable.y
                    val ratioFromMin = offsetFromMin.toFloat() / totalOffset.toFloat()

                    val parentWidth = targetParent.width
                    val totalWidthOffset = parentWidth - shrinkOriLayoutParams.width
                    val totalHeightOffset = targetParent.height - shrinkOriLayoutParams.height

                    Log.w("greyson", "scrolling: ratio=$ratioFromMin, parentWid=$parentWidth, offsetFromMin=$offsetFromMin")

                    val targetLP = target.layoutParams as MarginLayoutParams

                    Log.w("greyson", "targetLP: start=${targetLP.marginStart}, top=${targetLP.topMargin}, end=${targetLP.marginEnd}, bot=${targetLP.bottomMargin}")
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

                    // 设置子 View 的宽
                    targetLP.width = (totalWidthOffset * ratioFromMin).toInt() + shrinkOriLayoutParams.width
                    targetLP.height = (totalHeightOffset * ratioFromMin).toInt() + shrinkOriLayoutParams.height

                    target.layoutParams = targetLP

                    if (targetParent is RecyclerView) targetParent.scrollToPosition(0)
                }

            }
        }
    }

}