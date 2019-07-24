package com.testdemo.testDatePicker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Region;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;

import com.testdemo.broken_lib.Utils;
import com.testdemo.testDatePicker.datepicker.bizs.calendars.DPCManager;
import com.testdemo.testDatePicker.datepicker.bizs.themes.DPTManager;
import com.testdemo.testDatePicker.datepicker.entities.DPInfo;

import java.util.Arrays;

public class MyCalendarPicker extends View {

    private final Region[][] MONTH_WEEKS_4 = new Region[4][7];
    private final Region[][] MONTH_WEEKS_5 = new Region[5][7];
    private final Region[][] MONTH_WEEKS_6 = new Region[6][7];

    private final DPInfo[][] INFO_4 = new DPInfo[4][7];
    private final DPInfo[][] INFO_5 = new DPInfo[5][7];
    private final DPInfo[][] INFO_6 = new DPInfo[6][7];

    private DPCManager mCManager = DPCManager.getInstance();
    private DPTManager mTManager = DPTManager.getInstance();
    protected Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG |
            Paint.LINEAR_TEXT_FLAG);
    private Scroller mScroller;

    private Region[][] currentMonthWeeks = MONTH_WEEKS_4;

    private int mCurrentYear, mCurrentMonth;
    private int mNextYear, mNextMonth;
    private int mPreviousYear, mPreviousMonth;

    private float mCanScrollGapY = 100;

   /* private String[][] dateOfMonth = new String[][]{
            new String[]{null, null, "1", "2", "3", "4", "5"},
            new String[]{"6", "7", "8", "9", "10", "11", "12"},
            new String[]{"13", "14", "15", "16", "17", "18", "19"},
            new String[]{"20", "21", "22", "23", "24", "25", "26"},
            new String[]{"27", "28", "29", "30", "31", null, null},
            new String[]{null, null, null, null, null, null, null}
    };*/


    public MyCalendarPicker(Context context) {
        this(context, null);
    }

    public MyCalendarPicker(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyCalendarPicker(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mScroller = new Scroller(getContext());
        mPaint.setTextAlign(Paint.Align.CENTER);
        mCanScrollGapY = Utils.dp2px((int) mCanScrollGapY);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        System.out.println("greyson MyCalendarPicker onMeasure()");
        if (mCurrentMonth <= 0 || mCurrentYear <= 0) {
            return;
        }

        DPInfo[][] info = mCManager.obtainDPInfo(mCurrentYear, mCurrentMonth);
        int column = 4;
        if (info[4][0] == null) {
            currentMonthWeeks = MONTH_WEEKS_4;
            column = 4;
        } else if (info[5][0] == null) {
            currentMonthWeeks = MONTH_WEEKS_5;
            column = 5;
        } else {
            currentMonthWeeks = MONTH_WEEKS_6;
            column = 6;
        }
        int measuredWidth = MeasureSpec.getSize(widthMeasureSpec);
        setMeasuredDimension(measuredWidth, (int) (measuredWidth * column / 7f));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        System.out.println("greyson MyCalendarPicker onSizeChanged()");
        if (mCurrentMonth <= 0 || mCurrentYear <= 0) {
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

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            invalidate();
        } else {
            requestLayout();
        }
    }

    float mFirstTouchY;
    float mLastTouchY;
    int mTotalScrollY;
    int mLastTotalScrollY;
    int verticalIndex;//标志纵向滑动几次

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mFirstTouchY = y;
                break;

            case MotionEvent.ACTION_MOVE:
//                mTotalScrollY += y - mLastTouchY;
                int scrollGoalY = mLastTotalScrollY + (int) (mFirstTouchY - y);
                smoothScrollTo(0, scrollGoalY);
                break;

            case MotionEvent.ACTION_UP:
                if (y - mFirstTouchY > mCanScrollGapY) {//slide down
                    if (mCurrentMonth == 1) {
                        mCurrentYear--;
                        mCurrentMonth = 12;
                    } else {
                        mCurrentMonth--;
                    }
                    computeDate();
//                    invalidate();
                    mLastTotalScrollY -= getHeight();

                } else if (mFirstTouchY - y > mCanScrollGapY) {//slide up
                    if (mCurrentMonth == 12) {
                        mCurrentYear++;
                        mCurrentMonth = 1;
                    } else {
                        mCurrentMonth++;
                    }
                    computeDate();
//                    invalidate();
                    mLastTotalScrollY += getHeight();
//                    smoothScrollTo(0, mLastTotalScrollY);
                }
                smoothScrollTo(0, mLastTotalScrollY);
                break;
        }
        mLastTouchY = y;
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        System.out.println("greyson MyCalendarPicker onDraw()");
        canvas.drawColor(Color.WHITE);
        if (mCurrentMonth <= 0 || mCurrentYear <= 0) {
            return;
        }

        drawMonthData(canvas, 0, verticalIndex - getMeasuredHeight(), mPreviousYear, mPreviousMonth);
        drawMonthData(canvas, 0, verticalIndex, mCurrentYear, mCurrentMonth);
        drawMonthData(canvas, 0, verticalIndex + getMeasuredHeight(), mNextYear, mNextMonth);
    }

    /**
     * 画某年某月的日历视图
     *
     * @param canvas
     * @param x
     * @param y
     * @param year
     * @param month
     */
    private void drawMonthData(Canvas canvas, int x, int y, int year, int month) {
        canvas.save();
        canvas.translate(x, y);
        DPInfo[][] info = mCManager.obtainDPInfo(year, month);
        DPInfo[][] result;
        Region[][] tmp;
        if (TextUtils.isEmpty(info[4][0].strG)) {
            tmp = MONTH_WEEKS_4;
            arrayClear(INFO_4);
            result = arrayCopy(info, INFO_4);
        } else if (TextUtils.isEmpty(info[5][0].strG)) {
            tmp = MONTH_WEEKS_5;
            arrayClear(INFO_5);
            result = arrayCopy(info, INFO_5);
        } else {
            tmp = MONTH_WEEKS_6;
            arrayClear(INFO_6);
            result = arrayCopy(info, INFO_6);
        }
        for (int i = 0; i < result.length; i++) {
            for (int j = 0; j < result[i].length; j++) {
                drawMonthData(canvas, tmp[i][j].getBounds(), info[i][j]);
            }
        }
        drawFrame(canvas, tmp[0][0].getBounds(), tmp.length);
        canvas.restore();
    }

    private void drawMonthData(Canvas canvas, Rect rect, DPInfo info) {
//        drawBG(canvas, rect, info);
        drawDayText(canvas, rect, info.strG, info.isWeekend);
//        if (isFestivalDisplay) drawFestival(canvas, rect, info.strF, info.isFestival);
//        drawDecor(canvas, rect, info);
    }

    private void drawDayText(Canvas canvas, Rect rect, String str, boolean isWeekend) {
        mPaint.setColor(Color.GREEN);
        canvas.drawRect(rect, mPaint);

        mPaint.setTextSize(Utils.dp2px(14));
        if (isWeekend) {
            mPaint.setColor(mTManager.colorWeekend());
        } else {
            mPaint.setColor(mTManager.colorG());
        }
        float y /*= rect.centerY()*/;
        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
        y = rect.centerY() - fontMetrics.top / 2 - fontMetrics.bottom / 2;
//        if (!isFestivalDisplay)
//            y = rect.centerY() + Math.abs(mPaint.ascent()) - (mPaint.descent() - mPaint.ascent()) / 2F;
        canvas.drawText(str, rect.centerX(), y, mPaint);
    }

    private void drawFrame(Canvas canvas, Rect cellRect, int columnNumber) {
        mPaint.setColor(Color.parseColor("#f5f9fc"));
        int cellWith = cellRect.width();
        int lineLength;
        canvas.drawLine(0, cellWith, cellWith * 7, cellWith, mPaint);
        canvas.drawLine(0, cellWith * 2, cellWith * 7, cellWith * 2, mPaint);
        canvas.drawLine(0, cellWith * 3, cellWith * 7, cellWith * 3, mPaint);
        lineLength = cellWith * 4;
        if (columnNumber > 4) {
            canvas.drawLine(0, cellWith * 4, cellWith * 7, cellWith * 4, mPaint);
            lineLength = cellWith * 5;
        }
        if (columnNumber > 5) {
            canvas.drawLine(0, cellWith * 5, cellWith * 7, cellWith * 5, mPaint);
            lineLength = cellWith * 6;
        }
        canvas.drawLine(cellWith, 0, cellWith, lineLength, mPaint);
        canvas.drawLine(cellWith * 2, 0, cellWith * 2, lineLength, mPaint);
        canvas.drawLine(cellWith * 3, 0, cellWith * 3, lineLength, mPaint);
        canvas.drawLine(cellWith * 4, 0, cellWith * 4, lineLength, mPaint);
        canvas.drawLine(cellWith * 5, 0, cellWith * 5, lineLength, mPaint);
        canvas.drawLine(cellWith * 6, 0, cellWith * 6, lineLength, mPaint);
    }

    private void arrayClear(DPInfo[][] info) {
        for (DPInfo[] anInfo : info) {
            Arrays.fill(anInfo, null);
        }
    }

    private DPInfo[][] arrayCopy(DPInfo[][] src, DPInfo[][] dst) {
        for (int i = 0; i < dst.length; i++) {
            System.arraycopy(src[i], 0, dst[i], 0, dst[i].length);
        }
        return dst;
    }

    private void smoothScrollTo(int fx, int fy) {
        int dx = fx - mScroller.getFinalX();
        int dy = fy - mScroller.getFinalY();
        smoothScrollBy(dx, dy);
    }

    private void smoothScrollBy(int dx, int dy) {
        mScroller.startScroll(mScroller.getFinalX(), mScroller.getFinalY(), dx, dy, 500);
        invalidate();
    }

    void setDate(int year, int month) {
        mCurrentYear = year;
        mCurrentMonth = month;
        verticalIndex = 0;
//        indexYear = 0;
//        indexMonth = 0;
//        buildRegion();
        computeDate();
        requestLayout();
        invalidate();
    }

    /**
     * 设置新的当前年月后，计算上一个月或下一个月的所在年月值
     */
    private void computeDate() {
        if (mCurrentMonth == 12) {
            mNextYear = mCurrentYear + 1;
            mNextMonth = 1;
        } else {
            mNextYear = mCurrentYear;
            mNextMonth = mCurrentMonth + 1;
        }

        if (mCurrentMonth == 1) {
            mPreviousYear = mCurrentYear - 1;
            mPreviousMonth = 12;
        } else {
            mPreviousYear = mCurrentYear;
            mPreviousMonth = mCurrentMonth - 1;
        }

        /*if (null != onDateChangeListener) {
            onDateChangeListener.onYearChange(centerYear);
            onDateChangeListener.onMonthChange(centerMonth);
        }*/
    }

}
