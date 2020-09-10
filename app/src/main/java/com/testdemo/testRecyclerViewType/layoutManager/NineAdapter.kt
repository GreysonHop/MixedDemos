package com.testdemo.testRecyclerViewType.layoutManager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.testdemo.R
import com.testdemo.testPictureSelect.imageLoader.MediaBean

/**
 * Created by Greyson on 2020/09/10
 */
class NineAdapter : RecyclerView.Adapter<NineAdapter.NineViewHolder>() {

    private val dataList = ArrayList<MediaBean>()

    fun setDataList(dataList: List<MediaBean>) {
        this.dataList.clear()
        if (dataList.isNotEmpty()) {
            this.dataList.addAll(dataList)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NineViewHolder {
        return NineViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_grid_media, null))
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: NineViewHolder, position: Int) {
        dataList[position].also {
            holder.bg.setImageResource(it.type)
            holder.tv.text = it.folderName
        }
    }


    inner class NineViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val bg = itemView.findViewById<ImageView>(R.id.iv_picture)
        val tv = itemView.findViewById<TextView>(R.id.video_msg_duration)
    }
}