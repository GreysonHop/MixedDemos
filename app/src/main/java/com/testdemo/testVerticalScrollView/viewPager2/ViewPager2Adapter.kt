package com.testdemo.testVerticalScrollView.viewPager2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.testdemo.R

/**
 * Create by Greyson
 */
class ViewPager2Adapter : RecyclerView.Adapter<ViewPager2Adapter.ViewHolder>() {


    override fun getItemCount(): Int {
        return 4
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false));
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = "我是第${position}项"
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var textView: TextView = view.findViewById<View>(R.id.tv_name) as TextView

        init {
            view.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        }
    }
}