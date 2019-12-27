package com.testdemo.testDateMsgLog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.testdemo.R
import com.testdemo.testDateMsgLog.MsgLogMonthView.OnDayClickListener

/**
 * Create by Greyson
 */
class MonthListAdapter : RecyclerView.Adapter<MonthListAdapter.MonthViewHolder>()
        , OnDayClickListener {

    private val dateList = arrayListOf<Array<Array<MsgLogDate>>>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonthViewHolder {
        return MonthViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_date_msg_log, null))
    }

    override fun getItemCount(): Int {
        return dateList.size
    }

    override fun onBindViewHolder(holder: MonthViewHolder, position: Int) {
//        holder.monthViewPanel.removeAllViews()
//        holder.monthViewPanel.addView()

        val firstDay = dateList[position][0][0]
        holder.monthTitle.text = "${firstDay.yearStr}年${firstDay.monthStr}月"
        holder.msgLogMonthView.setMonthData(dateList[position])
        holder.msgLogMonthView.onDayClickListener = this
    }

    inner class MonthViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        //        var monthViewPanel : FrameLayout = view.findViewById(R.id.fl_month_view_panel)
        var monthTitle = view.findViewById<TextView>(R.id.tv_date_msg_yearMonth)
        var msgLogMonthView: MsgLogMonthView = view.findViewById(R.id.msgLogMonthView)
    }

    fun setNewData(datas: List<Array<Array<MsgLogDate>>>?) {
        dateList.clear()
        if (datas != null) {
            dateList.addAll(datas)
        }
    }

    fun addData(data: Array<Array<MsgLogDate>>?) {
        if (data != null) {
            dateList.add(data)
            notifyItemInserted(dateList.size)
        }
    }

    fun addDatas(datas: List<Array<Array<MsgLogDate>>>) {
        if (datas.isNotEmpty()) {
            dateList.addAll(datas)
            notifyItemInserted(dateList.size)
        }
    }

    override fun onDayClickListener(msgLogMonthView: MsgLogMonthView, year: Int, month: Int, day: Int) {
        mOnDayClickListener?.onDayClickListener(msgLogMonthView, year, month, day)
    }

    private var mOnDayClickListener: OnDayClickListener? = null
    fun setOnDayClickListener(listener: OnDayClickListener) {
        mOnDayClickListener = listener
    }
}