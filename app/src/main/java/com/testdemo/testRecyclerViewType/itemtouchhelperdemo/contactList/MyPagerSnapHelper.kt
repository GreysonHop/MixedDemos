package com.testdemo.testRecyclerViewType.itemtouchhelperdemo.contactList

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper

/**
 * Created by Greyson on 2021/03/01
 */
class MyPagerSnapHelper : SnapHelper() {
    private var verticalHelper: OrientationHelper? = null

    override fun calculateDistanceToFinalSnap(layoutManager: RecyclerView.LayoutManager, targetView: View)
            : IntArray? {
        val result = intArrayOf(0, 0)
        if (layoutManager.canScrollVertically()) {
            val helper = getVerticalHelper(layoutManager)
            Log.d("greyson", "calculateDistanceToFinalSnap, totalSpace=${helper.totalSpace}")
            val childCenter: Int = (helper.getDecoratedStart(targetView)
                    + helper.getDecoratedMeasurement(targetView) / 2)
            val containerCenter: Int = helper.startAfterPadding/* + helper.totalSpace / 2*/
            result[1] = childCenter - containerCenter
        }
        return result
    }

    override fun findTargetSnapPosition(layoutManager: RecyclerView.LayoutManager?, velocityX: Int, velocityY: Int)
            : Int {
        return RecyclerView.NO_POSITION
    }

    override fun findSnapView(layoutManager: RecyclerView.LayoutManager?): View? {
        if (layoutManager == null) return null

        val childCount = layoutManager.childCount
        if (childCount == 0) return null

        val firstVisibleCount = run myFor@{
            for (i in 0 until childCount) {
                layoutManager.getChildAt(i)?.takeIf {
                    layoutManager.isViewPartiallyVisible(it, true, true)
                }?.run { return@myFor i }
            }
            -1
        }
        /*if (firstVisibleCount == -1) {
            return null
        }*/

        return layoutManager.getChildAt(firstVisibleCount + 5 * 5)
    }

    private fun getVerticalHelper(layoutManager: RecyclerView.LayoutManager): OrientationHelper {
        val helper = verticalHelper
        if (helper == null || helper.layoutManager !== layoutManager) {
            verticalHelper = OrientationHelper.createVerticalHelper(layoutManager)
        }
        return verticalHelper!!
    }
}