package com.testdemo.testDateMsgLog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Region;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.testdemo.broken_lib.Utils;
import com.testdemo.testDatePicker.datepicker.bizs.languages.DPLManager;
import com.testdemo.testDatePicker.datepicker.bizs.themes.DPTManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * MonthView
 *
 * @author Greyson 2019-12-26
 */
@SuppressLint("ClickableViewAccessibility")
public class MsgLogMonthView extends View {

    private final Region[][] MONTH_WEEKS_4 = new Region[4][7];
    private final Region[][] MONTH_WEEKS_5 = new Region[5][7];
    private final Region[][] MONTH_WEEKS_6 = new Region[6][7];

    private final MsgLogDate[][] INFO_4 = new MsgLogDate[4][7];
    private final MsgLogDate[][] INFO_5 = new MsgLogDate[5][7];
    private final MsgLogDate[][] INFO_6 = new MsgLogDate[6][7];

    private MsgLogManager mCManager = MsgLogManager.getInstance();
    private DPTManager mTManager = DPTManager.getInstance();
    private DPLManager mDPLManager = DPLManager.getInstance();

    protected Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG |
            Paint.LINEAR_TEXT_FLAG);

    private String mSelectDayStr = "1";

    //greyson
    private MsgLogDate[][] mMonthData = null;

    public void setMonthData(MsgLogDate[][] monthData) {
        mMonthData = monthData;
        invalidate();
    }


    public MsgLogMonthView(Context context) {
        this(context, null);
    }

    public MsgLogMonthView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MsgLogMonthView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint.setTextAlign(Paint.Align.CENTER);

        Calendar calendar = Calendar.getInstance();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int row;
        if (mMonthData == null) {
            row = 1;
        } else {
            if (TextUtils.isEmpty(mMonthData[4][0].dayStr)) {
                row = 4;
            } else if (TextUtils.isEmpty(mMonthData[5][0].dayStr)) {
                row = 5;
            } else {
                row = 6;
            }
        }

        int measuredWidth = MeasureSpec.getSize(widthMeasureSpec);
        int measuredHeight = (int) (measuredWidth * row / 7f);
        setMeasuredDimension(measuredWidth, measuredHeight);
        System.out.println("greyson CalendarPicker onMeasure() measuredHeight=" + measuredHeight
                + ", column = " + row);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        System.out.println("greyson CalendarPicker onSizeChanged()");
        if (mMonthData == null) {
            return;
        }

        int cellWidth = w / 7;


        for (int i = 0; i < MONTH_WEEKS_4.length; i++) {
            for (int j = 0; j < MONTH_WEEKS_4[i].length; j++) {
                Region region = new Region();
                region.set((j * cellWidth), (i * cellWidth), cellWidth + (j * cellWidth),
                        cellWidth + (i * cellWidth));
                MONTH_WEEKS_4[i][j] = region;
            }
        }
        for (int i = 0; i < MONTH_WEEKS_5.length; i++) {
            for (int j = 0; j < MONTH_WEEKS_5[i].length; j++) {
                Region region = new Region();
                region.set((j * cellWidth), (i * cellWidth), cellWidth + (j * cellWidth),
                        cellWidth + (i * cellWidth));
                MONTH_WEEKS_5[i][j] = region;
            }
        }
        for (int i = 0; i < MONTH_WEEKS_6.length; i++) {
            for (int j = 0; j < MONTH_WEEKS_6[i].length; j++) {
                Region region = new Region();
                region.set((j * cellWidth), (i * cellWidth), cellWidth + (j * cellWidth),
                        cellWidth + (i * cellWidth));
                MONTH_WEEKS_6[i][j] = region;
            }
        }
    }

    private float mFirstTouchY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        System.out.println("greyson CalendarPicker onTouchEvent() action = " + event.getAction());
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mFirstTouchY = y;
                break;

            case MotionEvent.ACTION_MOVE:
                break;

            case MotionEvent.ACTION_UP:
                dealClickEvent((int) event.getX(), (int) event.getY());
                break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        System.out.println("greyson CalendarPicker onDraw()");
        if (mMonthData == null) {
            return;
        }

        drawMonthData(canvas);
    }

    /**
     * 画某年某月的日历视图
     *
     * @param canvas
     */
    private void drawMonthData(Canvas canvas) {
        canvas.save();
        int myHeight;
//        MsgLogDate[][] info = mCManager.obtainMsgLogDate(year, month);
        MsgLogDate[][] info = mMonthData;
        MsgLogDate[][] result;
        Region[][] tmp;
        if (TextUtils.isEmpty(info[4][0].dayStr)) {
            tmp = MONTH_WEEKS_4;
            arrayClear(INFO_4);
            result = arrayCopy(info, INFO_4);
            myHeight = MONTH_WEEKS_4[0][0].getBounds().height() * 4;
        } else if (TextUtils.isEmpty(info[5][0].dayStr)) {
            tmp = MONTH_WEEKS_5;
            arrayClear(INFO_5);
            result = arrayCopy(info, INFO_5);
            myHeight = MONTH_WEEKS_5[0][0].getBounds().height() * 5;
        } else {
            tmp = MONTH_WEEKS_6;
            arrayClear(INFO_6);
            result = arrayCopy(info, INFO_6);
            myHeight = MONTH_WEEKS_6[0][0].getBounds().height() * 6;
        }

        for (int i = 0; i < result.length; i++) {
            for (int j = 0; j < result[i].length; j++) {
                drawMonthData(canvas, tmp[i][j].getBounds(), info[i][j]);
            }
        }
        canvas.restore();
    }

    private void drawMonthData(Canvas canvas, Rect rect, MsgLogDate info) {
//        drawBG(canvas, rect, info);
        drawDayText(canvas, rect, info);
//        if (isFestivalDisplay) drawFestival(canvas, rect, info.strF, info.isFestival);
//        drawDecor(canvas, rect, info);
    }

    private void drawDayText(Canvas canvas, Rect rect, MsgLogDate dpInfo) {
        String strDay = dpInfo.dayStr;
        boolean isToday = dpInfo.isToday;
        boolean isWeekend = dpInfo.isWeekend;
        boolean isSelectedDay = strDay.equals(mSelectDayStr);

        //画背景
        if (isSelectedDay) {
            mPaint.setColor(Color.parseColor("#3E82FB"));
            canvas.drawRect(rect, mPaint);
        } else if (isToday) {
            mPaint.setColor(Color.parseColor("#AAC8FF"));
            canvas.drawRect(rect, mPaint);
        }

        float y;
        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
        if (isToday) {
            mPaint.setColor(isSelectedDay ? Color.WHITE : isWeekend ? mTManager.colorWeekend() : mTManager.colorG());
            mPaint.setTextSize(Utils.dp2px(14));
            if (DPLManager.getInstance().isSameLanguage(Locale.CHINA)) {//temporary deal
                canvas.drawText("今天", rect.centerX(), rect.centerY() - fontMetrics.bottom, mPaint);
            } else {
                canvas.drawText("Today", rect.centerX(), rect.centerY() - fontMetrics.bottom, mPaint);
            }

            y = rect.centerY() - fontMetrics.ascent;
        } else {
            y = rect.centerY() - fontMetrics.top / 2 - fontMetrics.bottom / 2;
        }

//        if (!isFestivalDisplay)
//            y = rect.centerY() + Math.abs(mPaint.ascent()) - (mPaint.descent() - mPaint.ascent()) / 2F;

        mPaint.setTextSize(Utils.dp2px(14));
        mPaint.setColor(isSelectedDay ? Color.WHITE : isWeekend ? mTManager.colorWeekend() : mTManager.colorG());
        canvas.drawText(strDay, rect.centerX(), y, mPaint);
    }

    /**
     * 设置选中的年月日，并且显示所在月的视图
     */
    public void setSelectedDay(String dayStr) {
        if (dayStr == null) {
            return;
        }

        mSelectDayStr = dayStr;

        setShowMonth(dayStr);
    }

    private void arrayClear(MsgLogDate[][] info) {
        for (MsgLogDate[] anInfo : info) {
            Arrays.fill(anInfo, null);
        }
    }

    private MsgLogDate[][] arrayCopy(MsgLogDate[][] src, MsgLogDate[][] dst) {
        for (int i = 0; i < dst.length; i++) {
            System.arraycopy(src[i], 0, dst[i], 0, dst[i].length);
        }
        return dst;
    }

    private void dealClickEvent(int x, int y) {
        MsgLogDate[][] info = mMonthData;//todo is null??
//        MsgLogDate[][] info = mCManager.obtainMsgLogDate(mCurrentYear, mCurrentMonth);
        Region[][] tmp;
        if (TextUtils.isEmpty(info[4][0].dayStr)) {
            tmp = MONTH_WEEKS_4;
        } else if (TextUtils.isEmpty(info[5][0].dayStr)) {
            tmp = MONTH_WEEKS_5;
        } else {
            tmp = MONTH_WEEKS_6;
        }
        for (int i = 0; i < tmp.length; i++) {
            for (int j = 0; j < tmp[i].length; j++) {
                Region region = tmp[i][j];
                String currentDay;
                if (TextUtils.isEmpty(currentDay = info[i][j].dayStr)) {
                    continue;
                }

                if (!region.contains(x, y)) {
                    continue;
                }

                mSelectDayStr = currentDay;
                final String date = info[i][j].yearStr + "-" + info[i][j].monthStr + "-" + currentDay;
                if (mOnDayClickListener != null) {
                    mOnDayClickListener.onDayClickListener(date);
                }
                invalidate();
//                Toast.makeText(getContext(), "you click: " + mCurrentYear + "年" + mCurrentMonth + "月" + currentDay + "日", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 显示某年某月的视图
     */
    void setShowMonth(String day) {

//        buildRegion();
        requestLayout();
        invalidate();
    }

    public interface OnDayClickListener {
        /**
         * 格式都为“****-**-**”
         *
         * @param clickDay 点击的日期，月和日前面不补0，如2019-7-1
         */
        void onDayClickListener(String clickDay);
    }

    private OnDayClickListener mOnDayClickListener;

    public void setOnDayClickListener(OnDayClickListener onDayClickListener) {
        this.mOnDayClickListener = onDayClickListener;
    }

    public OnDayClickListener getOnDayClickListener() {
        return this.mOnDayClickListener;
    }
}
