package com.testdemo.testNCalendar

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.Group
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.necer.calendar.BaseCalendar
import com.necer.calendar.MonthCalendar
import com.necer.calendar.WeekCalendar
import com.necer.enumeration.CalendarState
import com.testdemo.R
import kotlinx.android.synthetic.main.act_test_ncalendar.*
import org.joda.time.LocalDate
import java.lang.ref.WeakReference

class TestNCalendarAct : Activity() {

    private lateinit var mTvCalendarDate: TextView
    private lateinit var mTvCalendarYear: TextView
    private lateinit var mIvCalendarYearPre: ImageView
    private lateinit var mIvCalendarYearNext: ImageView
    private lateinit var mGroupCalendarMonthView: Group

    private lateinit var mRvSchedule: RecyclerView

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_test_ncalendar)

        mTvCalendarDate = findViewById(R.id.tv_calendar_date)
        mTvCalendarYear = findViewById(R.id.tv_calendar_year)
        mIvCalendarYearPre = findViewById(R.id.iv_calendar_year_pre)
        mIvCalendarYearNext = findViewById(R.id.iv_calendar_year_next)
        mGroupCalendarMonthView = findViewById(R.id.group_calendar_monthView)
        mRvSchedule = findViewById(R.id.rv_schedule)

        miui10Calendar.post {
//            val height = if (miui10Calendar.height == 0) miui10Calendar.measuredHeight else miui10Calendar.height
            val params = tv_week_tip.layoutParams
            params.height = miui10Calendar.weekViewHeight + cl_week_bar.height
            tv_week_tip.layoutParams = params

            val yBottom = miui10Calendar.y + miui10Calendar.monthViewHeight / 6 * 5
            tv_month_tip.post {
                tv_month_tip.y = yBottom //- tv_month_tip.height
            }
        }

        val madTalkPainter = MadTalkPainter(miui10Calendar)

        val pointList = arrayListOf("2019-10-01", "2019-11-01", "2019-10-17"
                , "2019-09-23", "2019-11-03", "2019-10-16")
        madTalkPainter.setPointList(pointList)

        val holidayList = arrayListOf("2019-09-01", "2019-11-01", "2019-12-02")
        val workdayList = arrayListOf("2019-10-01", "2019-11-02", "2019-12-01")
        madTalkPainter.setLegalHolidayList(holidayList, workdayList)

        var mWeekFirstDate: LocalDate? = null//
        var mMonthFirstDate: LocalDate? = null
        miui10Calendar.setNestedScroll(true)
        miui10Calendar.setOnCalendarChangedListener { baseCalendar, year, month, localDate ->
            Log.v("greyson", "setOnCalendarChangedListener baseCalendar=$baseCalendar")


            /*val curFirstDate = baseCalendar.firstDate
            if (baseCalendar is MonthCalendar) {//月视图切换月
                if (mMonthFirstDate != null && curFirstDate != mMonthFirstDate) {
                    Toast.makeText(this, "${curFirstDate.year}年${curFirstDate.monthOfYear}月", Toast.LENGTH_SHORT).show()
                }
                mMonthFirstDate = curFirstDate

            } else if (baseCalendar is WeekCalendar) {//切换周
                if (mWeekFirstDate != null && curFirstDate != mWeekFirstDate) {
                    val weekLastDate = curFirstDate.plusDays(6)
                    var endYear = ""
                    var endMonth = ""
                    if (curFirstDate.year != weekLastDate.year) {
                        endYear = "${weekLastDate.year}年"
                    }
                    if (curFirstDate.monthOfYear != weekLastDate.monthOfYear) {
                        endMonth = "${weekLastDate.monthOfYear}月"
                    }
                    tv_week_tip.text = "${curFirstDate.year}年${curFirstDate.monthOfYear}月${curFirstDate.dayOfMonth}日 - " +
                            "${endYear}${endMonth}${weekLastDate.dayOfMonth}日"
                    tv_week_tip.visibility = View.VISIBLE
                    tv_week_tip.postDelayed({ tv_week_tip.visibility = View.GONE }, 1500)
//                    Toast.makeText(this, "${curFirstDate.year}年${curFirstDate.monthOfYear}月${curFirstDate.dayOfMonth}日 - " +
//                            "${endYear}${endMonth}${weekLastDate.dayOfMonth}日", Toast.LENGTH_SHORT).show()
                }
                mWeekFirstDate = curFirstDate
            }*/


            mTvCalendarDate.text = localDate.toString("今天：yyyy年MM月dd日,E")
            mTvCalendarYear.text = year.toString()
        }


        miui10Calendar.setOnCalendarPageChangeListener { baseCalendar: BaseCalendar ->
            Log.v("greyson", "setOnCalendarPageChangeListener: baseCalendar=$baseCalendar" +
                    "\n ${miui10Calendar.calendarState == CalendarState.WEEK}")

            val curFirstDate = baseCalendar.firstDate
            if (miui10Calendar.calendarState == CalendarState.MONTH && baseCalendar is MonthCalendar) {
//                Toast.makeText(this, "${curFirstDate.year}年${curFirstDate.monthOfYear}月", Toast.LENGTH_SHORT).show()
                tv_month_tip.text = "${curFirstDate.year}年${curFirstDate.monthOfYear}月"
                tv_month_tip.visibility = View.VISIBLE
                mShowMonthTipMills = System.currentTimeMillis()
                mHandler.sendEmptyMessageDelayed(MSG_TYPE_CLOSE_MONTH, 1500)

            } else if (miui10Calendar.calendarState == CalendarState.WEEK && baseCalendar is WeekCalendar) {
                val weekLastDate = curFirstDate.plusDays(6)
                var endYear = ""
                var endMonth = ""
                if (curFirstDate.year != weekLastDate.year) {
                    endYear = "${weekLastDate.year}年"
                }
                if (curFirstDate.monthOfYear != weekLastDate.monthOfYear) {
                    endMonth = "${weekLastDate.monthOfYear}月"
                }
                tv_week_tip.text = "${curFirstDate.year}年${curFirstDate.monthOfYear}月${curFirstDate.dayOfMonth}日 - " +
                        "${endYear}${endMonth}${weekLastDate.dayOfMonth}日"
                tv_week_tip.visibility = View.VISIBLE
//                tv_week_tip.postDelayed({ tv_week_tip.visibility = View.GONE }, 1500)
                mShowWeekTipMills = System.currentTimeMillis()
                mHandler.sendEmptyMessageDelayed(MSG_TYPE_CLOSE_WEEK, 1500)

            }
        }



        miui10Calendar.setOnCalendarStateChangedListener {
            Log.v("greyson", "setOnCalendarStateChangedListener")
            mGroupCalendarMonthView.visibility = if (it == CalendarState.WEEK) View.GONE else View.VISIBLE
        }

//        val calendarOriginalHeight = miui10Calendar.min
        miui10Calendar.setOnCalendarScrollingListener {
            println("greyson: setOnCalendarScrollingListener: $it")
//            miui10Calendar.layoutParams.height -= it.toInt()
        }

        /*findViewById<ImageView>(R.id.iv_icon).setOnClickListener {
            if (miui10Calendar.calendarState == CalendarState.MONTH) {
                miui10Calendar.toWeek()
            } else {
                miui10Calendar.toMonth()
            }
        }*/


        miui10Calendar.calendarPainter = madTalkPainter

        val jumpYear = { jumpMode: Int ->
            miui10Calendar.currectSelectDateList?.let {
                if (it.size > 0) {
                    it[0]?.let { selectedLocalDate ->
                        val localDateToJump = when (jumpMode) {
                            1 -> selectedLocalDate.minusYears(1)
                            2 -> selectedLocalDate.plusYears(1)
                            else -> {
                                selectedLocalDate.withYear(LocalDate().year)
                            }
                        }
                        Log.d("greyson", localDateToJump.toString("yyyy-MM-dd"))
//                        miui10Calendar.jumpDate("${localDateToJump.year}-${localDateToJump.monthOfYear}-${localDateToJump.dayOfMonth}")
                        miui10Calendar.jumpDate(localDateToJump.toString("yyyy-MM-dd"))
                    }
                }
            }
        }

        mIvCalendarYearPre.setOnClickListener {
            jumpYear(1)
        }

        mIvCalendarYearNext.setOnClickListener {
            jumpYear(2)
        }

        mTvCalendarYear.setOnClickListener {
            jumpYear(0)
        }

        mTvCalendarDate.setOnClickListener {
            miui10Calendar.jumpDate(LocalDate().toString("yyyy-MM-dd"))
        }


        mRvSchedule.layoutManager = LinearLayoutManager(this)
        mRvSchedule.adapter = RecyclerViewAdapter(this)

        /**   **********************/
        miui10Calendar.testKoMeth { i, f ->
            print("invoke my own method!")
            i.toFloat() == f
        }
    }

    fun changeMode(view: View) {
        if (miui10Calendar.calendarState == CalendarState.MONTH) {
            miui10Calendar.toWeek()
        } else {
            miui10Calendar.toMonth()
        }
    }

    fun <T> T.testKoMeth(funtion: (Int, Float) -> Boolean) {
        println("testKoMeth's result: ${funtion(1, 1.1f)}")
    }


    private val mHandler = StaticHandler(this)
    private var mShowWeekTipMills: Long = 0
    private var mShowMonthTipMills = 0L

    private companion object {
        private val MSG_TYPE_CLOSE_WEEK = 0
        private val MSG_TYPE_CLOSE_MONTH = 1
    }

    private class StaticHandler(context: Context) : Handler() {
        private var ref: WeakReference<Context>? = null

        init {
            ref = WeakReference(context)
        }

        fun setContext(context: Context?) {
            ref = if (context == null) {
                null
            } else {
                WeakReference(context)
            }
        }

        override fun handleMessage(msg: Message) {
            ref?.get()?.let { context ->
                if (context is TestNCalendarAct) {

                    val now = System.currentTimeMillis()
                    when (msg.what) {
                        MSG_TYPE_CLOSE_WEEK -> {
                            if (now - context.mShowWeekTipMills >= 1500) {
                                context.tv_week_tip.visibility = View.GONE
                            }
                        }

                        MSG_TYPE_CLOSE_MONTH -> {
                            if (now - context.mShowMonthTipMills >= 1500) {
                                context.tv_month_tip.visibility = View.GONE
                            }
                        }
                    }

                }
            }
        }
    }
}