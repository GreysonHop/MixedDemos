package com.testdemo.testAnim.activityAnim

import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import com.testdemo.testPictureSelect.imageLoader.MediaBean

/**
 * Create by Greyson
 */
class MediaPageAdapter : PagerAdapter() {
    var dataList: Array<List<MediaBean>?>? = null
    var adapterList = SparseArray<GridPicSelectAdapter>()

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val adapter = GridPicSelectAdapter()
        val rv = RecyclerView(container.context)
        rv.layoutManager = GridLayoutManager(container.context, 4)
        rv.layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
        rv.adapter = adapter

        adapterList.put(position, adapter)
        dataList?.let {
            adapter.setNewData(it[position])
        }
        container.addView(rv)
        return rv
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun getCount(): Int {
        return dataList?.size ?: 0
    }

    fun setNewData(list: Array<List<MediaBean>?>) {
        val oldSize = count
        dataList = list
        if (oldSize != list.size) {
            notifyDataSetChanged()
        } else {
            list.mapIndexed { index, l ->
                adapterList.get(index)?.setNewData(l)
            }
        }
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        super.destroyItem(container, position, `object`)
    }
}