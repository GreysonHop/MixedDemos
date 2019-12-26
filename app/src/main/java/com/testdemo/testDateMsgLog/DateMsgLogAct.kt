package com.testdemo.testDateMsgLog

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.testdemo.R
import kotlinx.android.synthetic.main.act_date_msg_log.*
import org.joda.time.LocalDate

/**
 * Create by Greyson
 */
class DateMsgLogAct : Activity() {

    private lateinit var today: LocalDate
    private var currentTargetLocalDate: LocalDate? = null
    private lateinit var monthListAdapter: MonthListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_date_msg_log)

        monthListAdapter = MonthListAdapter()
        monthListAdapter.setOnDayClickListener(MsgLogMonthView.OnDayClickListener { clickDay ->
            Toast.makeText(this@DateMsgLogAct, "你点击了：$clickDay", Toast.LENGTH_SHORT).show()
        })
        rv_month_list.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true)
        rv_month_list.adapter = monthListAdapter

        initData()
        tv_week.setOnClickListener {
            currentTargetLocalDate?.let {
                val moreDatas = loadDate(it, 1, 0)
                monthListAdapter.addDatas(moreDatas)
            }
        }
    }


    private fun initData() {
        today = LocalDate()

        /*for (year in 2019..2020) {
            for (month in 1..4)
                tempList.add(MsgLogManager.getInstance().obtainMsgLogDate(year, month))
        }*/
        monthListAdapter.setNewData(loadDate(today, 1, 2))
    }

    /**
     * 以baseLocalDate为基准，获取yearsAgo年又monthsAgo个月之前为结束点，之间的所有数据
     */
    private fun loadDate(baseLocalDate: LocalDate, yearsAgo: Int, monthsAgo: Int)
            : List<Array<Array<MsgLogDate>>> {
        val tempList = arrayListOf<Array<Array<MsgLogDate>>>()
        val targetLocalDate = baseLocalDate.minusYears(yearsAgo).minusMonths(monthsAgo)
        currentTargetLocalDate = targetLocalDate

        for (year in baseLocalDate.year downTo targetLocalDate.year) {
            var startMonth = 12
            var endMonth = 1
            if (year == baseLocalDate.year) {
                startMonth = baseLocalDate.monthOfYear
            }
            if (year == targetLocalDate.year) {
                endMonth = targetLocalDate.monthOfYear
            }

            for (month in startMonth downTo endMonth) {
                tempList.add(MsgLogManager.getInstance().obtainMsgLogDate(year, month))
            }
        }
        return tempList
    }

}