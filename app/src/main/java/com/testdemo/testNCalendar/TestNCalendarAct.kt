package com.testdemo.testNCalendar

import android.app.Activity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.Group
import com.necer.calendar.Miui10Calendar
import com.necer.enumeration.CalendarState
import com.testdemo.R
import org.joda.time.LocalDate

class TestNCalendarAct : Activity() {

    private lateinit var mMiui10Calendar: Miui10Calendar
    private lateinit var mTvCalendarDate: TextView
    private lateinit var mTvCalendarYear: TextView
    private lateinit var mIvCalendarYearPre: ImageView
    private lateinit var mIvCalendarYearNext: ImageView
    private lateinit var mGroupCalendarMonthView: Group

    private lateinit var mRvSchedule: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_test_ncalendar)

        mMiui10Calendar = findViewById(R.id.miui10Calendar)
        mTvCalendarDate = findViewById(R.id.tv_calendar_date)
        mTvCalendarYear = findViewById(R.id.tv_calendar_year)
        mIvCalendarYearPre = findViewById(R.id.iv_calendar_year_pre)
        mIvCalendarYearNext = findViewById(R.id.iv_calendar_year_next)
        mGroupCalendarMonthView = findViewById(R.id.group_calendar_monthView)
        mRvSchedule = findViewById(R.id.rv_schedule)

        val madTalkPainter = MadTalkPainter(mMiui10Calendar)

        val pointList = arrayListOf("2019-10-01", "2019-11-01", "2019-10-17"
                , "2019-09-23", "2019-11-03", "2019-10-16")
        madTalkPainter.setPointList(pointList)

        val holidayList = arrayListOf("2019-09-01", "2019-11-01", "2019-12-02")
        val workdayList = arrayListOf("2019-10-01", "2019-11-02", "2019-12-01")
        madTalkPainter.setLegalHolidayList(holidayList, workdayList)

//        mMiui10Calendar.setDefaultSelectFitst(true)
        mMiui10Calendar.setOnCalendarChangedListener { baseCalendar, year, month, localDate ->
            //            mTvCalendarDate.text = "${year}年${month}月 当前页面选中 $localDate"
            mTvCalendarDate.text = localDate.toString("今天：yyyy年MM月dd日,E")
            mTvCalendarYear.text = year.toString()
        }

        mMiui10Calendar.setOnCalendarStateChangedListener {
            mGroupCalendarMonthView.visibility = if (it == CalendarState.WEEK) View.GONE else View.VISIBLE
        }

//        val calendarOriginalHeight = mMiui10Calendar.min
        mMiui10Calendar.setOnCalendarScrollingListener {
            println("greyson: $it")
//            mMiui10Calendar.layoutParams.height -= it.toInt()
        }

        /*findViewById<ImageView>(R.id.iv_icon).setOnClickListener {
            if (mMiui10Calendar.calendarState == CalendarState.MONTH) {
                mMiui10Calendar.toWeek()
            } else {
                mMiui10Calendar.toMonth()
            }
        }*/


        mMiui10Calendar.calendarPainter = madTalkPainter

        val jumpYear = { jumpMode: Int ->
            mMiui10Calendar.currectSelectDateList?.let {
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
//                        mMiui10Calendar.jumpDate("${localDateToJump.year}-${localDateToJump.monthOfYear}-${localDateToJump.dayOfMonth}")
                        mMiui10Calendar.jumpDate(localDateToJump.toString("yyyy-MM-dd"))
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
            mMiui10Calendar.jumpDate(LocalDate().toString("yyyy-MM-dd"))
        }


        mRvSchedule.layoutManager = LinearLayoutManager(this)
        mRvSchedule.adapter = RecyclerViewAdapter(this)

        /**   **********************/
        mMiui10Calendar.testKoMeth { i, f ->
            print("invoke my own method!")
            i.toFloat() == f
        }
    }

    fun changeMode(view: View) {
        if (mMiui10Calendar.calendarState == CalendarState.MONTH) {
            mMiui10Calendar.toWeek()
        } else {
            mMiui10Calendar.toMonth()
        }
    }

    fun <T> T.testKoMeth(funtion: (Int, Float) -> Boolean) {
        println("testKoMeth's result: ${funtion(1, 1.1f)}")
    }
}