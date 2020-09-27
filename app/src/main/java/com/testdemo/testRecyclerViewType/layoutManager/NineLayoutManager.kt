package com.testdemo.testRecyclerViewType.layoutManager

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutParams

/**
 * Created by Greyson on 2020/09/10
 */
class NineLayoutManager : RecyclerView.LayoutManager() {
//TODO greyson_9/15/20 RecyclerView无法使用WRAP_CONTENT！
    var gap = 0
    /*set(value) {
        field = value
        invalidate()
    }*/

    /**
     * 该方法是LayoutManager的入口。它会在如下情况下被调用：
     * 1 在RecyclerView初始化时，会被调用两次。
     * 2 在调用adapter.notifyDataSetChanged()时，会被调用。
     * 3 在调用setAdapter替换Adapter时,会被调用。
     * 4 在RecyclerView执行动画时，它也会被调用。
     */
    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        if (itemCount == 0) {
            detachAndScrapAttachedViews(recycler)
            return
        }

        if (childCount == 0 && state.isPreLayout) { //state.isPreLayout()是支持动画的
            return
        }

        detachAndScrapAttachedViews(recycler)
        fill(recycler, state)
    }

    /**
     * 在考虑滑动位移的情况下：
     * 1 回收所有屏幕不可见的子View
     * 2 layout所有可见的子View
     */
    private fun fill(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        val validWidth = width - paddingLeft - paddingRight //可以布局子View的有效宽度
        val column = if (itemCount <= 4) 2 else 3 //总共多少列
        val viewSize = (validWidth - gap * (column - 1)) / column //所有子View的宽高强制一样

        for (i in 0 until itemCount.coerceAtMost(9)) { //最多只填充9个
            val child = recycler.getViewForPosition(i)
            child.layoutParams.apply { width = viewSize; height = viewSize }
            addView(child)
            measureChildWithMargins(child, 0, 0)

            val col = i % column //当前子控件在第几列
            val row = i / column

            if (itemCount == 3 && i == 2) {
                val offsetThird = (viewSize + gap) / 2
                layoutDecoratedWithMargins(
                    child,
                    col * (viewSize + gap) + offsetThird,
                    row * (viewSize + gap),
                    col * (viewSize + gap) + offsetThird + viewSize,
                    row * (viewSize + gap) + viewSize
                ) //todo 考虑Padding？

            } else {
                layoutDecoratedWithMargins(
                    child,
                    col * (viewSize + gap),
                    row * (viewSize + gap),
                    col * (viewSize + gap) + viewSize,
                    row * (viewSize + gap) + viewSize
                )
            }
        }
    }

    override fun generateDefaultLayoutParams(): LayoutParams {
        return LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
    }

}