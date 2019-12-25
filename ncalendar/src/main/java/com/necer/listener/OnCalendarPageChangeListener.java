package com.necer.listener;

import com.necer.calendar.BaseCalendar;

import org.joda.time.LocalDate;

/**
 * Create by Greyson
 */
public interface OnCalendarPageChangeListener {
    void onCalendarPageChange(BaseCalendar baseCalendar/*, LocalDate initialDate*/);
}
