package com.necer.mynew

import org.joda.time.LocalDate

/**
 * Create by Greyson
 */
class WeekDataBean(initialDate: LocalDate, dataList: List<LocalDate>) : CalendarData(initialDate, dataList) {

    override fun isEqualsMonthOrWeek(date: LocalDate, initialDate: LocalDate): Boolean {
        return mDateList.contains(date)
    }

    override fun getFirstDate(): LocalDate {
        return mDateList[0]
    }

}