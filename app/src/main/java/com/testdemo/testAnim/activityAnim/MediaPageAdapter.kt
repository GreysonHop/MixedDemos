package com.testdemo.testAnim.activityAnim

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import com.testdemo.testPictureSelect.imageLoader.ChatPictureBean

/**
 * Create by Greyson
 */
class MediaPageAdapter : PagerAdapter() {
    var dataList: Array<List<ChatPictureBean>?>? = null

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val adapter = GridPicSelectAdapter()
        dataList?.let {
            adapter.setNewData(it[position])
        }
        val rv = RecyclerView(container.context)
        rv.layoutManager = GridLayoutManager(container.context, 4)
        rv.adapter = adapter
        return rv
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun getCount(): Int {
        return dataList?.size ?: 0
    }

    fun setNewData(list: Array<List<ChatPictureBean>?>) {
        dataList = list
        notifyDataSetChanged()
    }
}