package com.testdemo.testRecyclerViewType.itemtouchhelperdemo.contactList

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
            val childCenter: Int = (helper.getDecoratedStart(targetView)
                    + helper.getDecoratedMeasurement(targetView) / 2)
            val containerCenter: Int = helper.startAfterPadding + helper.totalSpace / 2
            result[1] = childCenter - containerCenter
        }
        return result
    }

    override fun findTargetSnapPosition(layoutManager: RecyclerView.LayoutManager?, velocityX: Int, velocityY: Int)
            : Int {
        return RecyclerView.NO_POSITION
    }

    override fun findSnapView(layoutManager: RecyclerView.LayoutManager?): View? {
        return if (layoutManager!!.canScrollVertically()) {
            findCenterView(layoutManager, getVerticalHelper(layoutManager))
        } else null
    }

    private fun findCenterView(layoutManager: RecyclerView.LayoutManager, helper: OrientationHelper): View? {
        return null
    }

    private fun getVerticalHelper(layoutManager: RecyclerView.LayoutManager): OrientationHelper {
        val helper = verticalHelper
        if (helper == null || helper.layoutManager !== layoutManager) {
            verticalHelper = OrientationHelper.createVerticalHelper(layoutManager)
        }
        return verticalHelper!!
    }
}