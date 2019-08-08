package com.testdemo.testDatePicker.datepicker.bizs.languages;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * 中文的默认实现类
 * 如果你想实现更多的语言请参考Language{@link DPLManager}
 * <p>
 * The implementation class of chinese.
 * You can refer to Language{@link DPLManager} if you want to define more language.
 *
 * @author AigeStudio 2015-03-28
 */
public class CN extends DPLManager {

    private SimpleDateFormat simpleDateFormat;

    @Override
    public String[] titleMonth() {
        return new String[]{"一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月"};
    }

    @Override
    public String titleEnsure() {
        return "确定";
    }

    @Override
    public String titleBC() {
        return "公元前";
    }

    @Override
    public String[] titleWeek() {
        return new String[]{"六", "日", "一", "二", "三", "四", "五"};
    }

    @Override
    public String getDateFormatStr() {
        return "yyyy年M月d日";
    }

    @Override
    public DateFormat getDateFormat() {
        if (simpleDateFormat == null) {
            simpleDateFormat = new SimpleDateFormat(getDateFormatStr(), Locale.CHINA);
        }
        return simpleDateFormat;
    }
}
