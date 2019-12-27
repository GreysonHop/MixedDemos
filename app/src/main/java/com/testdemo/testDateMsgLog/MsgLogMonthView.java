package com.testdemo.testDateMsgLog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.testdemo.broken_lib.Utils;
import com.testdemo.testDatePicker.datepicker.bizs.languages.DPLManager;
import com.testdemo.testDatePicker.datepicker.bizs.themes.DPTManager;

import java.util.Arrays;
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

    private DPTManager mTManager = DPTManager.getInstance();

    protected Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG |
            Paint.LINEAR_TEXT_FLAG);

    //greyson
    private MsgLogDate[][] mMonthData = null;
    private String mPressedDay = "-1";//被按中的日期，显示按住的效果

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


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        System.out.println("greyson CalendarPicker onTouchEvent() action = " + event.getAction());
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                dealClickEvent((int) event.getX(), (int) event.getY(), event);
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
        MsgLogDate[][] info = mMonthData;
        MsgLogDate[][] result;
        Region[][] tmp;
        if (TextUtils.isEmpty(info[4][0].dayStr)) {
            tmp = MONTH_WEEKS_4;
            arrayClear(INFO_4);
            result = arrayCopy(info, INFO_4);
        } else if (TextUtils.isEmpty(info[5][0].dayStr)) {
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
                drawDayText(canvas, tmp[i][j].getBounds(), info[i][j]);
            }
        }
        canvas.restore();
    }

    private void drawDayText(Canvas canvas, Rect rect, MsgLogDate dpInfo) {
        String strDay = dpInfo.dayStr;
        boolean isToday = dpInfo.isToday;
        boolean isPressedDay = false;
        if (!TextUtils.equals("-1", mPressedDay)) {
            isPressedDay = TextUtils.equals(strDay, mPressedDay);
        }
        if (isPressedDay) {
            mPressedDay = "-1";
        }

        int bgHalfWidth = Utils.dp2px(13);
        RectF bgRect = new RectF();
        bgRect.left = rect.centerX() - bgHalfWidth;
        bgRect.top = rect.centerY() - bgHalfWidth;
        bgRect.right = rect.centerX() + bgHalfWidth;
        bgRect.bottom = rect.centerY() + bgHalfWidth;
        //画背景
        if (isPressedDay) {
            mPaint.setColor(Color.parseColor("#AAC8FF"));
            canvas.drawRoundRect(bgRect, Utils.dp2px(2), Utils.dp2px(2), mPaint);
        } else if (isToday) {
            mPaint.setColor(Color.parseColor("#3E82FB"));
            canvas.drawRoundRect(bgRect, Utils.dp2px(1), Utils.dp2px(1), mPaint);
        }

        mPaint.setTextSize(Utils.dp2px(16));
        mPaint.setColor(isPressedDay || isToday ? Color.WHITE : mTManager.colorG());
        mPaint.setFakeBoldText(true);
        Paint.FontMetrics dayFontMetrics = mPaint.getFontMetrics();
        float y = rect.centerY() - dayFontMetrics.top / 2 - dayFontMetrics.bottom / 2;

        canvas.drawText(strDay, rect.centerX(), y, mPaint);//画日期的数字


        if (isToday) {
            mPaint.setColor(Color.parseColor("#3E82FB"));
            mPaint.setTextSize(Utils.dp2px(10));
            Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
            y = rect.centerY() + bgHalfWidth + fontMetrics.bottom - fontMetrics.top;

            if (DPLManager.getInstance().isSameLanguage(Locale.CHINA)) {//temporary deal
                canvas.drawText("今天", rect.centerX(), y, mPaint);
            } else {
                canvas.drawText("Today", rect.centerX(), y, mPaint);
            }

        }
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

    private void dealClickEvent(int x, int y, MotionEvent event) {
        if (mMonthData == null) {
            return;
        }

        MsgLogDate[][] info = mMonthData;
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

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mPressedDay = info[i][j].dayStr;
                    invalidate();

                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    try {
                        int year = Integer.valueOf(info[i][j].yearStr);
                        int month = Integer.valueOf(info[i][j].monthStr);
                        int day = Integer.valueOf(currentDay);
//                    final String date = info[i][j].yearStr + "-" + info[i][j].monthStr + "-" + currentDay;
                        if (mOnDayClickListener != null) {
                            mOnDayClickListener.onDayClickListener(this, year, month, day);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        }

        if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {//为了消除按下的背景
            invalidate();
        }
    }

    public interface OnDayClickListener {
        /**
         *
         */
        void onDayClickListener(MsgLogMonthView msgLogMonthView, int year, int month, int day);
    }

    private OnDayClickListener mOnDayClickListener;

    public void setOnDayClickListener(OnDayClickListener onDayClickListener) {
        this.mOnDayClickListener = onDayClickListener;
    }

    public OnDayClickListener getOnDayClickListener() {
        return this.mOnDayClickListener;
    }
}
