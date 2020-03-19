package com.necer.mynew

import org.joda.time.LocalDate

/**
 * Create by Greyson
 */
abstract class CalendarData(initialDate: LocalDate, dateList: List<LocalDate>) {

    protected var mDateList: List<LocalDate> = dateList
    protected var mInitialDate: LocalDate = initialDate

    fun getMiddleLocalDate(): LocalDate {
        return mDateList[mDateList.size / 2 + 1]
    }

    fun getDateList(): List<LocalDate> {
        return mDateList
    }

    fun getInitialDate(): LocalDate {
        return mInitialDate
    }

    //初始化的日期和绘制的日期是否是同月，周都相同
    abstract fun isEqualsMonthOrWeek(date: LocalDate, initialDate: LocalDate): Boolean

    //周或者月的第一天
    abstract fun getFirstDate(): LocalDate

}