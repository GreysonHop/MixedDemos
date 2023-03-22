package com.testdemo.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.testdemo.R

/**
 * Created by Greyson on 2018/06/07 .
 */
class CommonImgAdapter(var dataList: List<String>) : RecyclerView.Adapter<CommonImgAdapter.ViewHolder>() {

    var clickCallBack: ((View, Int) -> Unit)? = null

    fun setNewData(dataList: List<String>) {
        this.dataList = dataList
    }

    //创建新View，被LayoutManager所调用
    @NonNull
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_img, viewGroup, false)
        return ViewHolder(view)
    }

    //将数据与界面进行绑定的操作
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.mImage.contentDescription = dataList[position]
        viewHolder.mImage.setImageResource(R.drawable.bg_vip_gold)
        viewHolder.mImage.setOnClickListener { v -> clickCallBack?.invoke(v, position) }
    }

    /*override fun onBindViewHolder(holder: ViewHolder, position: Int, @NonNull payloads: List<Any>) {
        super.onBindViewHolder(holder, position, payloads)
    }*/

    //获取数据的数量
    override fun getItemCount(): Int {
        return dataList.size
    }

    //自定义的ViewHolder，持有每个Item的的所有界面元素
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var mImage: ImageView = view.findViewById(R.id.iv_img)
    }

}