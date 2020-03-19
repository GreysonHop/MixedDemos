package com.necer.view;

import android.content.Context;
import android.view.ViewGroup;

import org.joda.time.LocalDate;

/**
 * Created by necer on 2018/9/11.
 * qq群：127278900
 */
public class WeekView extends CalendarView {

    public WeekView(Context context, ViewGroup container) {
        super(context, container);
    }

    @Override
    protected void dealClickDate(LocalDate localDate) {
        mCalendar.onClickCurrentMonthOrWeekDate(localDate);
    }

    @Override
    public boolean isEqualsMonthOrWeek(LocalDate date, LocalDate initialDate) {
        return mDateList.contains(date);
    }

    @Override
    public LocalDate getFirstDate() {
        return mDateList.get(0);
    }
}
