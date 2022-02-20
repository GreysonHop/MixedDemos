package com.testdemo.testCenterRefresh

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.testdemo.R
import java.util.*

/**
 * Created by Greyson on 2018/06/07 .
 */
class MyAdapter(var dataList: List<String>) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {

    var clickCallBack: ((View, Int) -> Unit)? = null

    fun setNewData(dataList: List<String>) {
        this.dataList = dataList
    }

    //创建新View，被LayoutManager所调用
    @NonNull
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(android.R.layout.simple_list_item_1, viewGroup, false)
        return ViewHolder(view)
    }

    //将数据与界面进行绑定的操作
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.mTextView.text = dataList[position]
        viewHolder.mTextView.setOnClickListener { v -> clickCallBack?.invoke(v, position) }
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
        var mTextView: TextView = view.findViewById(android.R.id.text1)
    }

}