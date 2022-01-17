package com.testdemo.testNCalendar;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextUtils;

import com.necer.calendar.ICalendar;
import com.necer.entity.CalendarDate;
import com.necer.painter.CalendarPainter;
import com.necer.utils.Attrs;
import com.necer.utils.CalendarUtil;
import com.necer.view.CalendarView;
import com.necer.view.MonthView;
import com.testdemo.util.broken_lib.Utils;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Greyson on 2019/10/16 according to necer on 2019/1/3.
 */
public class MadTalkPainter implements CalendarPainter {

    private Attrs mAttrs;
    protected Paint mTextPaint;
    protected Paint mCirclePaint;

    private int noAlphaColor = 255;
    private boolean isLocalChina;
    private float mSelectBgRectLength;

    protected List<LocalDate> mHolidayList;
    protected List<LocalDate> mWorkdayList;

    private List<LocalDate> mPointList;
    private Map<LocalDate, Integer> mPointColorMap;
    private Map<LocalDate, String> mReplaceLunarStrMap;
    private Map<LocalDate, Integer> mReplaceLunarColorMap;

    private ICalendar mCalendar;

    public MadTalkPainter(ICalendar calendar) {
        this.mAttrs = calendar.getAttrs();
        this.mCalendar = calendar;
        mTextPaint = getPaint();
        mCirclePaint = getPaint();
        mPointList = new ArrayList<>();
        mHolidayList = new ArrayList<>();
        mWorkdayList = new ArrayList<>();
        mPointColorMap = new HashMap<>();
        mReplaceLunarStrMap = new HashMap<>();
        mReplaceLunarColorMap = new HashMap<>();
        isLocalChina = Locale.getDefault().getLanguage().toLowerCase().equals("zh");
        mSelectBgRectLength = Utils.dp2px(38);

        List<String> holidayList = CalendarUtil.getHolidayList();
        for (int i = 0; i < holidayList.size(); i++) {
            mHolidayList.add(new LocalDate(holidayList.get(i)));
        }
        List<String> workdayList = CalendarUtil.getWorkdayList();
        for (int i = 0; i < workdayList.size(); i++) {
            mWorkdayList.add(new LocalDate(workdayList.get(i)));
        }
    }


    private Paint getPaint() {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTextAlign(Paint.Align.CENTER);
        return paint;
    }


    @Override
    public void onDrawCalendarBackground(CalendarView calendarView, Canvas canvas, RectF rectF, LocalDate localDate, int totalDistance, int currentDistance) {
        if (calendarView instanceof MonthView && mAttrs.isShowNumberBackground) {
            mTextPaint.setTextSize(mAttrs.numberBackgroundTextSize);
            mTextPaint.setColor(mAttrs.numberBackgroundTextColor);
            int alphaColor = mAttrs.numberBackgroundAlphaColor * currentDistance / totalDistance;
            mTextPaint.setAlpha(alphaColor);
            canvas.drawText(localDate.getMonthOfYear() + "", rectF.centerX(), getBaseLineY(rectF), mTextPaint);
        }

    }

    @Override
    public void onDrawToday(Canvas canvas, RectF rectF, LocalDate localDate, List<LocalDate> selectDateList) {
        if (selectDateList.contains(localDate)) {
            drawSelectBg(canvas, rectF, noAlphaColor);
            drawSolar(canvas, rectF, localDate, noAlphaColor, true, true);
            drawLunar(canvas, rectF, localDate, noAlphaColor, true, true);
            drawPoint(canvas, rectF, true, noAlphaColor, localDate);
            drawHolidays(canvas, rectF, true, noAlphaColor, localDate);
        } else {
            drawTodayBg(canvas, rectF, noAlphaColor);
            drawSolar(canvas, rectF, localDate, noAlphaColor, false, true);
            drawLunar(canvas, rectF, localDate, noAlphaColor, false, true);
            drawPoint(canvas, rectF, false, noAlphaColor, localDate);
            drawHolidays(canvas, rectF, false, noAlphaColor, localDate);
        }
    }

    @Override
    public void onDrawCurrentMonthOrWeek(Canvas canvas, RectF rectF, LocalDate localDate, List<LocalDate> selectDateList) {
        if (selectDateList.contains(localDate)) {
            drawSelectBg(canvas, rectF, noAlphaColor);
            drawSolar(canvas, rectF, localDate, noAlphaColor, true, false);
            drawLunar(canvas, rectF, localDate, noAlphaColor, true, false);
            drawPoint(canvas, rectF, true, noAlphaColor, localDate);
            drawHolidays(canvas, rectF, false, noAlphaColor, localDate);
        } else {
            drawSolar(canvas, rectF, localDate, noAlphaColor, false, false);
            drawLunar(canvas, rectF, localDate, noAlphaColor, false, false);
            drawPoint(canvas, rectF, false, noAlphaColor, localDate);
            drawHolidays(canvas, rectF, false, noAlphaColor, localDate);
        }
    }

    @Override
    public void onDrawLastOrNextMonth(Canvas canvas, RectF rectF, LocalDate localDate, List<LocalDate> selectDateList) {
        if (selectDateList.contains(localDate)) {
            drawSelectBg(canvas, rectF, mAttrs.alphaColor);
            drawSolar(canvas, rectF, localDate, noAlphaColor, true, false);
            drawLunar(canvas, rectF, localDate, noAlphaColor, true, false);
            drawPoint(canvas, rectF, false, mAttrs.alphaColor, localDate);
            drawHolidays(canvas, rectF, false, mAttrs.alphaColor, localDate);
        } else {
            drawSolar(canvas, rectF, localDate, mAttrs.alphaColor, false, false);
            drawLunar(canvas, rectF, localDate, mAttrs.alphaColor, false, false);
            drawPoint(canvas, rectF, false, mAttrs.alphaColor, localDate);
            drawHolidays(canvas, rectF, false, mAttrs.alphaColor, localDate);
        }
    }

    @Override
    public void onDrawDisableDate(Canvas canvas, RectF rectF, LocalDate localDate) {
        drawSolar(canvas, rectF, localDate, mAttrs.disabledAlphaColor, false, false);
        drawLunar(canvas, rectF, localDate, mAttrs.disabledAlphaColor, false, false);
        drawPoint(canvas, rectF, false, mAttrs.disabledAlphaColor, localDate);
        drawHolidays(canvas, rectF, false, mAttrs.disabledAlphaColor, localDate);
    }


    //选中背景
    private void drawSelectBg(Canvas canvas, RectF rectF, int alphaColor) {
        mCirclePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mCirclePaint.setColor(mAttrs.selectCircleColor);
        drawDayBg(canvas, rectF);
    }

    private void drawTodayBg(Canvas canvas, RectF rectF, int alphaColor) {
        mCirclePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mCirclePaint.setColor(mAttrs.hollowCircleColor);
        mCirclePaint.setAlpha(alphaColor);
        drawDayBg(canvas, rectF);
    }

    private void drawDayBg(Canvas canvas, RectF rectF) {
        float widthOffset = (rectF.width() - mSelectBgRectLength) / 2;
        float heightOffset = (rectF.height() - mSelectBgRectLength) / 2;
        RectF realRectF = new RectF(rectF.left + widthOffset, rectF.top + heightOffset, rectF.right - widthOffset, rectF.bottom - heightOffset);
        canvas.drawRoundRect(realRectF, mAttrs.selectCircleRadius, mAttrs.selectCircleRadius, mCirclePaint);
    }


    //绘制公历
    private void drawSolar(Canvas canvas, RectF rectF, LocalDate date, int alphaColor, boolean isSelect, boolean isToday) {
        if (isSelect) {
            mTextPaint.setColor(isToday ? mAttrs.todaySolarSelectTextColor : mAttrs.selectSolarTextColorColor);
        } else {
            mTextPaint.setColor(isToday ? mAttrs.todaySolarTextColor : mAttrs.solarTextColor);
        }
        mTextPaint.setAlpha(alphaColor);

        String dayStr = getMonthFirstDayStr(date);
        float solarTextSizeScale = 1.0f;
        if (dayStr.length() >= 3) {
            if (isLocalChina) {
                solarTextSizeScale = 3f / 5;
            } else {
                solarTextSizeScale = 4f / 5;
            }
        } else {
            if (isLocalChina) {
                solarTextSizeScale = 4f / 5;
            }
        }
        mTextPaint.setTextSize(mAttrs.solarTextSize * solarTextSizeScale);
        canvas.drawText(dayStr, rectF.centerX()
                , isLocalChina && mAttrs.isShowLunar ? rectF.centerY() : getBaseLineY(rectF)
                , mTextPaint);
    }

    //绘制农历
    private void drawLunar(Canvas canvas, RectF rectF, LocalDate localDate, int alphaColor, boolean isSelect, boolean isToday) {
        if (isLocalChina && mAttrs.isShowLunar) {
            boolean isTodaySelect = isSelect && isToday;
            CalendarDate calendarDate = CalendarUtil.getCalendarDate(localDate);
            //优先顺序 替换的文字、农历节日、节气、公历节日、正常农历日期
            String lunarString = mReplaceLunarStrMap.get(calendarDate.localDate);
            if (lunarString == null) {
                if (!TextUtils.isEmpty(calendarDate.lunarHoliday)) {
                    mTextPaint.setColor(isTodaySelect ? mAttrs.todaySelectContrastColor : mAttrs.lunarHolidayTextColor);
                    lunarString = calendarDate.lunarHoliday;
                } else if (!TextUtils.isEmpty(calendarDate.solarTerm)) {
                    mTextPaint.setColor(isTodaySelect ? mAttrs.todaySelectContrastColor : mAttrs.solarTermTextColor);
                    lunarString = calendarDate.solarTerm;
                } else if (!TextUtils.isEmpty(calendarDate.solarHoliday)) {
                    mTextPaint.setColor(isTodaySelect ? mAttrs.todaySelectContrastColor : mAttrs.solarHolidayTextColor);
                    lunarString = calendarDate.solarHoliday;
                } else {
                    mTextPaint.setColor(isTodaySelect ? mAttrs.todaySelectContrastColor : mAttrs.lunarTextColor);
                    lunarString = calendarDate.lunar.lunarOnDrawStr;
                }
            }
            Integer color = mReplaceLunarColorMap.get(calendarDate.localDate);
            if (color == null) {
                if (isSelect) {
                    mTextPaint.setColor(isToday ? mAttrs.todaySelectContrastColor : mAttrs.selectLunarTextColor);
                } else if (isToday) {
                    mTextPaint.setColor(mAttrs.todaySolarTextColor);
                }
            } else {
                mTextPaint.setColor(color);
            }
            mTextPaint.setTextSize(mAttrs.lunarTextSize);
            mTextPaint.setAlpha(alphaColor);
            canvas.drawText(lunarString, rectF.centerX(), rectF.centerY() + mAttrs.lunarDistance, mTextPaint);
        }
    }


    //绘制圆点
    private void drawPoint(Canvas canvas, RectF rectF, boolean isSelect, int alphaColor, LocalDate date) {
        if (mPointList.contains(date)) {
            mCirclePaint.setStyle(Paint.Style.FILL);
            mCirclePaint.setAlpha(alphaColor);

            Integer color;
            if (isSelect) {
                color = mAttrs.selectSolarTextColorColor;
            } else {
                color = mPointColorMap.get(date);
                if (color == null) {
                    color = mAttrs.selectCircleColor;
                }
            }
            mCirclePaint.setColor(color);

            canvas.drawCircle(rectF.centerX(), mAttrs.pointLocation == Attrs.DOWN
                    ? (rectF.centerY() + mAttrs.pointDistance) : (rectF.centerY() - mAttrs.pointDistance), mAttrs.pointSize, mCirclePaint);
        }
    }

    //绘制节假日
    private void drawHolidays(Canvas canvas, RectF rectF, boolean isTodaySelect, int alphaColor, LocalDate localDate) {
        if (mAttrs.isShowHoliday) {
            int[] holidayLocation = getHolidayLocation(rectF.centerX(), rectF.centerY());
            mTextPaint.setTextSize(mAttrs.holidayTextSize);
            if (mHolidayList.contains(localDate)) {
                mTextPaint.setColor(isTodaySelect ? mAttrs.todaySelectContrastColor : mAttrs.holidayColor);
                mTextPaint.setAlpha(alphaColor);
                canvas.drawText("休", holidayLocation[0], holidayLocation[1], mTextPaint);
            } else if (mWorkdayList.contains(localDate)) {
                mTextPaint.setColor(isTodaySelect ? mAttrs.todaySelectContrastColor : mAttrs.workdayColor);
                mTextPaint.setAlpha(alphaColor);
                canvas.drawText("班", holidayLocation[0], holidayLocation[1], mTextPaint);
            }
        }
    }

    //canvas.drawText的基准线
    private float getBaseLineY(RectF rectF) {
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        float top = fontMetrics.top;
        float bottom = fontMetrics.bottom;
        // int baseLineY = (int) (rectF.centerY() - top / 2 - bottom / 2);
        float baseLineY = rectF.centerY() - (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.top;
        return baseLineY;
    }

    //Holiday的位置
    private int[] getHolidayLocation(float centerX, float centerY) {
        int[] location = new int[2];
        int solarTexyCenterY = getSolarTextCenterY(centerY);
        switch (mAttrs.holidayLocation) {
            case Attrs.TOP_LEFT:
                location[0] = (int) (centerX - mAttrs.holidayDistance);
                location[1] = solarTexyCenterY;
                break;
            case Attrs.BOTTOM_RIGHT:
                location[0] = (int) (centerX + mAttrs.holidayDistance);
                location[1] = (int) centerY;
                break;
            case Attrs.BOTTOM_LEFT:
                location[0] = (int) (centerX - mAttrs.holidayDistance);
                location[1] = (int) centerY;
                break;
            case Attrs.TOP_RIGHT:
            default:
                location[0] = (int) (centerX + mAttrs.holidayDistance);
                location[1] = solarTexyCenterY;
                break;
        }
        return location;

    }

    //公历文字的竖直中心y
    private int getSolarTextCenterY(float centerY) {
        mTextPaint.setTextSize(mAttrs.solarTextSize);
        Paint.FontMetricsInt fontMetricsInt = mTextPaint.getFontMetricsInt();
        int ascent = fontMetricsInt.ascent;
        int descent = fontMetricsInt.descent;
        int textCenterY = (int) (descent / 2 + centerY + ascent / 2);//文字的中心y
        return textCenterY;
    }

    //设置标记
    public void setPointList(List<String> list) {
        mPointList.clear();
        for (int i = 0; i < list.size(); i++) {
            LocalDate localDate = null;
            try {
                localDate = new LocalDate(list.get(i));
            } catch (Exception e) {
                throw new RuntimeException("setPointList的参数需要 yyyy-MM-dd 格式的日期");
            }
            mPointList.add(localDate);
        }
        mCalendar.notifyCalendar();
    }

    public void addPointList(List<String> list) {
        for (int i = 0; i < list.size(); i++) {
            LocalDate localDate = null;
            try {
                localDate = new LocalDate(list.get(i));
            } catch (Exception e) {
                throw new RuntimeException("setPointList的参数需要 yyyy-MM-dd 格式的日期");
            }
            if (!mPointList.contains(localDate)) {
                mPointList.add(localDate);
            }
        }
        mCalendar.notifyCalendar();
    }

    public void setPointColorMap(Map<String, Integer> pointColorMap) {
        mPointColorMap.clear();
        for (String key : pointColorMap.keySet()) {
            LocalDate localDate;
            try {
                localDate = new LocalDate(key);
            } catch (Exception e) {
                throw new RuntimeException("setReplaceLunarStrMap的参数需要 yyyy-MM-dd 格式的日期");
            }
            mPointColorMap.put(localDate, pointColorMap.get(key));
        }
        mCalendar.notifyCalendar();
    }

    //设置替换农历的文字
    public void setReplaceLunarStrMap(Map<String, String> replaceLunarStrMap) {
        mReplaceLunarStrMap.clear();
        for (String key : replaceLunarStrMap.keySet()) {
            LocalDate localDate;
            try {
                localDate = new LocalDate(key);
            } catch (Exception e) {
                throw new RuntimeException("setReplaceLunarStrMap的参数需要 yyyy-MM-dd 格式的日期");
            }
            mReplaceLunarStrMap.put(localDate, replaceLunarStrMap.get(key));
        }
        mCalendar.notifyCalendar();
    }

    //设置替换农历的颜色
    public void setReplaceLunarColorMap(Map<String, Integer> replaceLunarColorMap) {
        mReplaceLunarColorMap.clear();
        for (String key : replaceLunarColorMap.keySet()) {
            LocalDate localDate;
            try {
                localDate = new LocalDate(key);

            } catch (Exception e) {
                throw new RuntimeException("setReplaceLunarColorMap的参数需要 yyyy-MM-dd 格式的日期");
            }
            mReplaceLunarColorMap.put(localDate, replaceLunarColorMap.get(key));
        }
        mCalendar.notifyCalendar();
    }


    //设置法定节假日和补班
    public void setLegalHolidayList(List<String> holidayList, List<String> workdayList) {
        mHolidayList.clear();
        mWorkdayList.clear();

        for (int i = 0; i < holidayList.size(); i++) {
            LocalDate holidayLocalDate;
            try {
                holidayLocalDate = new LocalDate(holidayList.get(i));
            } catch (Exception e) {
                throw new RuntimeException("setLegalHolidayList集合中的参数需要 yyyy-MM-dd 格式的日期");
            }
            mHolidayList.add(holidayLocalDate);
        }

        for (int i = 0; i < workdayList.size(); i++) {
            LocalDate workdayLocalDate;
            try {
                workdayLocalDate = new LocalDate(workdayList.get(i));
            } catch (Exception e) {
                throw new RuntimeException("setLegalHolidayList集合中的参数需要 yyyy-MM-dd 格式的日期");
            }
            mWorkdayList.add(workdayLocalDate);
        }
        mCalendar.notifyCalendar();
    }


    private String getMonthFirstDayStr(LocalDate date) {
        String result = "";
        if (date.getDayOfMonth() == 1) {
            String[] lunarMonth = isLocalChina
                    ? new String[]{"一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月"}
                    : new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

            result = lunarMonth[date.getMonthOfYear() - 1];
        } else {
            result = date.getDayOfMonth() + "";
        }
        return result;
    }
}

