package com.necer.calendar;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.necer.adapter.BaseCalendarAdapter;
import com.necer.adapter.MonthCalendarAdapter;
import com.necer.painter.CalendarPainter;
import com.necer.utils.Attrs;
import com.necer.utils.CalendarUtil;

import org.joda.time.LocalDate;

/**
 * Created by necer on 2018/9/11.
 * qq群：127278900
 */
public class MonthCalendar extends BaseCalendar {

    public MonthCalendar(@NonNull Context context, @Nullable AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override
    protected BaseCalendarAdapter getCalendarAdapter(Context context, LocalDate startDate, LocalDate endDate, LocalDate initializeDate, Attrs attrs) {
        return new MonthCalendarAdapter(context, startDate, endDate, initializeDate, attrs);
    }

    @Override
    protected int getTwoDateCount(LocalDate startDate, LocalDate endDate, int type) {
        return CalendarUtil.getIntervalMonths(startDate, endDate);
    }

    @Override
    protected LocalDate getIntervalDate(LocalDate localDate, int count, boolean isJumpClick) {
        LocalDate date = localDate.plusMonths(count);
        if (isJumpClick) {
            return date;
            
        } else {
            LocalDate today = new LocalDate();
            if (CalendarUtil.getIntervalMonths(date, today) == 0) {
                return today;//当前月份包含"今天"，则默认选中今天，否则选中月份的第一天
            } else {
                return getFirstDate();
            }
        }
    }
}
