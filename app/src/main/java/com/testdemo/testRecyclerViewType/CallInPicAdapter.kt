package com.testdemo.testRecyclerViewType

import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.luck.picture.lib.tools.ScreenUtils
import com.testdemo.R

/**
 * Created by Greyson on 2020/08/12
 */
class CallInPicAdapter(val data: List<Int>) : RecyclerView.Adapter<CallInPicAdapter.PicViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CallInPicAdapter.PicViewHolder {
        val iv = ImageView(parent.context)
        iv.layoutParams = MarginLayoutParams(ScreenUtils.dip2px(parent.context, 52f), ScreenUtils.dip2px(parent.context, 52f))
            .apply {
            marginStart = ScreenUtils.dip2px(parent.context, 4f)
            marginEnd = ScreenUtils.dip2px(parent.context, 4f)
                topMargin = ScreenUtils.dip2px(parent.context, 4f)
                bottomMargin = ScreenUtils.dip2px(parent.context, 4f)
        }
        iv.setBackgroundResource(R.color.color_orange)
        return PicViewHolder(iv)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: CallInPicAdapter.PicViewHolder, position: Int) {
        data[position].also { resId ->
            (holder.itemView as ImageView).setImageResource(resId)
            if (position > 3) holder.itemView.setBackgroundResource(R.color.Green)
        }
    }

    inner class PicViewHolder(itemView: ImageView) : RecyclerView.ViewHolder(itemView)
}