package com.necer.adapter;

import android.content.Context;
import android.view.ViewGroup;

import com.necer.calendar.BaseCalendar;
import com.necer.mynew.CalendarData;
import com.necer.mynew.WeekDataBean;
import com.necer.utils.Attrs;
import com.necer.utils.CalendarUtil;
import com.necer.view.CalendarView;
import com.necer.view.WeekView;

import org.joda.time.LocalDate;

import java.util.List;

/**
 * Created by necer on 2018/9/11.
 * qq群：127278900
 */
public class WeekCalendarAdapter extends BaseCalendarAdapter {

    public WeekCalendarAdapter(Context context, LocalDate startDate, LocalDate endDate, LocalDate initializeDate, Attrs attrs, BaseCalendar baseCalendar) {
        super(context, startDate, endDate, initializeDate, attrs, baseCalendar);
    }

    @Override
    protected CalendarData getCalendarData(int position) {
        LocalDate initialDate = mInitializeDate.plusDays((position - mCurr) * 7);
        List<LocalDate> dateList = CalendarUtil.getWeekCalendar(initialDate, mAttrs.firstDayOfWeek);
        return new WeekDataBean(initialDate, dateList);
    }

    @Override
    protected CalendarView getCalendarView(ViewGroup container) {
        return new WeekView(mContext, container);
    }

    @Override
    protected int getIntervalCount(LocalDate startDate, LocalDate endDate, int type) {
        return CalendarUtil.getIntervalWeek(startDate, endDate, type);
    }
}
