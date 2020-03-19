package com.necer.adapter;

import android.content.Context;
import android.view.ViewGroup;

import com.necer.calendar.BaseCalendar;
import com.necer.mynew.CalendarData;
import com.necer.mynew.MonthDataBean;
import com.necer.utils.Attrs;
import com.necer.utils.CalendarUtil;
import com.necer.view.CalendarView;
import com.necer.view.MonthView;

import org.joda.time.LocalDate;

import java.util.List;

/**
 * Created by necer on 2018/9/11.
 * qq群：127278900
 */
public class MonthCalendarAdapter extends BaseCalendarAdapter {

    public MonthCalendarAdapter(Context context, LocalDate startDate, LocalDate endDate, LocalDate initializeDate, Attrs attrs, BaseCalendar baseCalendar) {
        super(context, startDate, endDate, initializeDate, attrs, baseCalendar);
    }

    @Override
    protected CalendarData getCalendarData(int position) {
        LocalDate initialDate = mInitializeDate.plusMonths(position - mCurr);
        List<LocalDate> dateList = CalendarUtil.getMonthCalendar(initialDate, mAttrs.firstDayOfWeek, mAttrs.isAllMonthSixLine);
        return new MonthDataBean(initialDate, dateList);
    }

    @Override
    protected CalendarView getCalendarView(ViewGroup container) {
        return new MonthView(mContext, container);
    }

    @Override
    protected int getIntervalCount(LocalDate startDate, LocalDate endDate, int type) {
        return CalendarUtil.getIntervalMonths(startDate, endDate);
    }
}
