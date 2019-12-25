package com.testdemo.testDateMsgLog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.testdemo.R
import org.joda.time.LocalDate

/**
 * Create by Greyson
 */
class MonthListAdapter : RecyclerView.Adapter<MonthListAdapter.MonthViewHolder>() {

    private val mCount = 0
    private val dateList = arrayListOf<LocalDate>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonthViewHolder {
        return MonthViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_date_msg_log, null))
    }

    override fun getItemCount(): Int {
        return dateList.size
    }

    override fun onBindViewHolder(holder: MonthViewHolder, position: Int) {
//        holder.monthViewPanel.removeAllViews()
//        holder.monthViewPanel.addView()


    }

    inner class MonthViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var monthViewPanel : FrameLayout = view.findViewById(R.id.fl_month_view_panel)
        var monthTitle = view.findViewById<TextView>(R.id.tv_date_msg_yearMonth)
    }
}