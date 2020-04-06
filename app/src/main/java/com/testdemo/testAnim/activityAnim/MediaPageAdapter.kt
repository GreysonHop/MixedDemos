package com.testdemo.testAnim.activityAnim

import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import com.chad.library.adapter.base.BaseQuickAdapter.OnItemChildClickListener
import com.testdemo.R
import com.testdemo.testPictureSelect.imageLoader.MediaBean

/**
 * Create by Greyson
 */
class MediaPageAdapter : PagerAdapter() {
    var dataList: Array<List<MediaBean>?>? = null
    var adapterList = SparseArray<GridMediaSelectAdapter>()

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        var adapter = adapterList.get(position)
        if (adapter == null) {
            adapter = GridMediaSelectAdapter()
            adapter.onItemChildClickListener = OnItemChildClickListener { _, view, _ ->
                if (R.id.item_pic_cb == view.id) {
                    val adapterIndex = adapterList.indexOfValue(adapter)
                    onMediaCheckedListener?.onMediaChecked(adapterIndex, adapter, view, position)
                }
            }
            adapterList.put(position, adapter)
        }

        val rv = RecyclerView(container.context)
        rv.layoutManager = GridLayoutManager(container.context, 4)
        rv.layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
        rv.adapter = adapter

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

    interface OnMediaCheckedListener {
        /**
         * @param page 点击的媒体文件所在的RecyclerView是MediaPageAdapter中的第几页
         * @param adapter 点击的媒体文件所在的RecyclerView.Adapter。如果为空，说明
         * @param view 点击的CheckBox
         */
        fun onMediaChecked(page: Int, adapter: GridMediaSelectAdapter, view: View, checkedPosition: Int)
    }

    private var onMediaCheckedListener: OnMediaCheckedListener? = null
    fun setOnMediaCheckedListener(listener: OnMediaCheckedListener) {
        this.onMediaCheckedListener = listener
    }
}