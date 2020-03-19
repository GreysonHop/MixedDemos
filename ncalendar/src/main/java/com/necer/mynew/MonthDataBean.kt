package com.necer.mynew

import com.necer.utils.CalendarUtil
import org.joda.time.LocalDate

/**
 * Create by Greyson
 */
class MonthDataBean(initialDate: LocalDate, dataList: List<LocalDate>) : CalendarData(initialDate, dataList) {

    override fun isEqualsMonthOrWeek(date: LocalDate, initialDate: LocalDate): Boolean {
        return CalendarUtil.isEqualsMonth(date, initialDate)
    }

    override fun getFirstDate(): LocalDate {
        return LocalDate(mInitialDate.year, mInitialDate.monthOfYear, 1)
    }

}