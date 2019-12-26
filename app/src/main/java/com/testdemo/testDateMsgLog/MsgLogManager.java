package com.testdemo.testDateMsgLog;

import android.text.TextUtils;

import com.testdemo.testDatePicker.datepicker.bizs.calendars.DPCNCalendar;
import com.testdemo.testDatePicker.datepicker.bizs.calendars.DPCalendar;
import com.testdemo.testDatePicker.datepicker.bizs.calendars.DPUSCalendar;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * 日期管理器
 * The manager of date picker.
 *
 * @author Greyson 2019-12-26
 */
public final class MsgLogManager {
    private static final HashMap<Integer, HashMap<Integer, MsgLogDate[][]>> DATE_CACHE = new HashMap<>();


    private static MsgLogManager sManager;

    private DPCalendar c;

    private MsgLogManager() {
        // 默认显示为中文日历
        String locale = Locale.getDefault().getCountry().toLowerCase();
        if (locale.equals("cn")) {
            initCalendar(new DPCNCalendar());
        } else {
            initCalendar(new DPUSCalendar());
        }
    }

    /**
     * 获取月历管理器
     * Get calendar manager
     *
     * @return 月历管理器
     */
    public static MsgLogManager getInstance() {
        if (null == sManager) {
            sManager = new MsgLogManager();
        }
        return sManager;
    }

    /**
     * 初始化日历对象
     * <p/>
     * Initialization Calendar
     *
     * @param c ...
     */
    public void initCalendar(DPCalendar c) {
        this.c = c;
    }

    /**
     * 获取指定年月的日历对象数组
     *
     * @param year  公历年
     * @param month 公历月
     * @return 日历对象数组 该数组长度恒为6x7 如果某个下标对应无数据则填充为null
     */
    public MsgLogDate[][] obtainMsgLogDate(int year, int month) {
        HashMap<Integer, MsgLogDate[][]> dataOfYear = DATE_CACHE.get(year);
        if (null != dataOfYear && dataOfYear.size() != 0) {
            MsgLogDate[][] dataOfMonth = dataOfYear.get(month);
            if (dataOfMonth != null) {
                return dataOfMonth;
            }
            dataOfMonth = buildMsgLogDate(year, month);
            dataOfYear.put(month, dataOfMonth);
            return dataOfMonth;
        }
        if (null == dataOfYear) dataOfYear = new HashMap<>();
        MsgLogDate[][] dataOfMonth = buildMsgLogDate(year, month);
        dataOfYear.put((month), dataOfMonth);
        DATE_CACHE.put(year, dataOfYear);
        return dataOfMonth;
    }

    private void setDecor(List<String> date, HashMap<String, Set<String>> cache) {
        for (String str : date) {
            int index = str.lastIndexOf("-");
            String key = str.substring(0, index).replace("-", ":");
            Set<String> days = cache.get(key);
            if (null == days) {
                days = new HashSet<>();
            }
            days.add(str.substring(index + 1, str.length()));
            cache.put(key, days);
        }
    }

    private MsgLogDate[][] buildMsgLogDate(int year, int month) {
        MsgLogDate[][] info = new MsgLogDate[6][7];

        String[][] dayStr = c.buildMonthG(year, month);
        String[][] festivalStr = c.buildMonthFestival(year, month);

        Set<String> strHoliday = c.buildMonthHoliday(year, month);
        Set<String> strWeekend = c.buildMonthWeekend(year, month);

        for (int i = 0; i < info.length; i++) {
            for (int j = 0; j < info[i].length; j++) {
                MsgLogDate tmp = new MsgLogDate();
                tmp.dayStr = dayStr[i][j];
                tmp.yearStr = String.valueOf(year);
                tmp.monthStr = String.valueOf(month);

                if (c instanceof DPCNCalendar) {
                    tmp.festivalStr = festivalStr[i][j].replace("F", "");
                } else {
                    tmp.festivalStr = festivalStr[i][j];
                }
                if (!TextUtils.isEmpty(tmp.dayStr) && strHoliday.contains(tmp.dayStr))
                    tmp.isHoliday = true;
                if (!TextUtils.isEmpty(tmp.dayStr)) tmp.isToday =
                        c.isToday(year, month, Integer.valueOf(tmp.dayStr));
                if (strWeekend.contains(tmp.dayStr)) tmp.isWeekend = true;
                if (c instanceof DPCNCalendar) {
                    if (!TextUtils.isEmpty(tmp.dayStr)) tmp.isSolarTerms =
                            ((DPCNCalendar) c).isSolarTerm(year, month, Integer.valueOf(tmp.dayStr));
                    if (!TextUtils.isEmpty(festivalStr[i][j]) && festivalStr[i][j].endsWith("F"))
                        tmp.isFestival = true;
                    if (!TextUtils.isEmpty(tmp.dayStr))
                        tmp.isDeferred = ((DPCNCalendar) c)
                                .isDeferred(year, month, Integer.valueOf(tmp.dayStr));
                } else {
                    tmp.isFestival = !TextUtils.isEmpty(festivalStr[i][j]);
                }
                info[i][j] = tmp;
            }
        }
        return info;
    }
}
