package com.testdemo.testDatePicker;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.provider.Settings;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.testdemo.R;
import com.testdemo.broken_lib.Utils;
import com.testdemo.testDatePicker.datepicker.bizs.languages.DPLManager;
import com.testdemo.testDatePicker.wheelView.ArrayWheelAdapter;
import com.testdemo.testDatePicker.wheelView.LineConfig;
import com.testdemo.testDatePicker.wheelView.OnItemPickListener;
import com.testdemo.testDatePicker.wheelView.WheelView;

import java.util.ArrayList;

/**
 * Created by Greyson
 */
public class TimePicker extends LinearLayout {

    private final WheelView mAmPmView = new WheelView(getContext());
    private final WheelView mHourView = new WheelView(getContext());
    private final WheelView mMinuteView = new WheelView(getContext());
    private ArrayList<String> mAmPmList = new ArrayList<>();
    private ArrayList<String> mHourList = new ArrayList<>();
    private ArrayList<String> mMinuteList = new ArrayList<>();

    private OnWheelListener onWheelListener;

    private short mSelectedAmPm = -1;
    private String mSelectedHour, mSelectedMinute;
    private int mHourStyle;//小时制，默认为24小时制，除非值等于12
    private int mMinuteGap;//分钟选择器中分钟数之间的间隔，如平时显示的0,1,2...59，间隔为1

    public TimePicker(Context context) {
        this(context, 1);
    }

    /**
     * @param context
     * @param minuteGap 修改时间选择器中分钟数的间隔（必须是大于等于1的整数），默认为1
     */
    public TimePicker(Context context, int minuteGap) {
        this(context, null, 0, minuteGap);
    }

    public TimePicker(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0, 1);
    }

    public TimePicker(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 1);
    }

    public TimePicker(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int minuteGap) {
        super(context, attrs, defStyleAttr);
        mMinuteGap = minuteGap <= 0 ? 1 : minuteGap;
        init();
    }

    private void init() {
        setGravity(Gravity.CENTER);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER_VERTICAL;

        //上\下午滚轮
        mAmPmView.setCanLoop(false);
        mAmPmView.setTypeface(Typeface.SERIF);
        mAmPmView.setDividerType(LineConfig.DividerType.FILL);
        mAmPmView.setLayoutParams(layoutParams);
        mAmPmView.setOnItemPickListener((OnItemPickListener<String>) (index, item) -> {
            mSelectedAmPm = (short) index;
            if (onWheelListener != null) {
                onWheelListener.onAmPmWheeled(index, item);
            }
        });
        addView(mAmPmView);

        //小时滚轮
        mHourView.setCanLoop(false);
        mHourView.setTypeface(Typeface.SERIF);
        mHourView.setDividerType(LineConfig.DividerType.FILL);

        mHourView.setLayoutParams(layoutParams);
        mHourView.setOnItemPickListener((OnItemPickListener<String>) (index, item) -> {
            mSelectedHour = item;
            if (onWheelListener != null) {
                onWheelListener.onHourWheeled(index, item);
            }
        });
        addView(mHourView);

        //时间冒号
        TextView labelView = new TextView(getContext());
        LayoutParams lableLP = new LayoutParams(layoutParams.width, layoutParams.height);
        lableLP.gravity = layoutParams.gravity;
        lableLP.bottomMargin = Utils.dp2px(3);
        lableLP.leftMargin = Utils.dp2px(29);
        lableLP.rightMargin = Utils.dp2px(27);
        labelView.setLayoutParams(lableLP);
        labelView.setTextColor(Color.parseColor("#283851"));
        labelView.setTextSize(getResources().getDimensionPixelSize(R.dimen.text_wheel_time_colon));
        labelView.setText(":");
        addView(labelView);

        //分钟滚轮
        mMinuteView.setCanLoop(false);
        mMinuteView.setTypeface(Typeface.DEFAULT);
        /*mSelectedMinute = */
        updateMinuteDateWithGap(mMinuteGap);

        mMinuteView.setDividerType(LineConfig.DividerType.FILL);
//        mMinuteView.setLineConfig(lineConfig);
        mMinuteView.setLayoutParams(layoutParams);
        mMinuteView.setOnItemPickListener((OnItemPickListener<String>) (index, item) -> {
            mSelectedMinute = item;
            if (onWheelListener != null) {
                onWheelListener.onMinuteWheeled(index, item);
            }
        });
        addView(mMinuteView);
        updateDataForHourStyle();
    }

    public boolean updateDataForHourStyle() {
        int timeFormat = Settings.System.getInt(getContext().getContentResolver(), Settings.System.TIME_12_24, 24);
        if (timeFormat == mHourStyle) {
            return false;
        }

        mAmPmList.clear();
        String[] amPm = DPLManager.getInstance().getAmPmStr();
        mAmPmList.add(amPm[0]);
        mAmPmList.add(amPm[1]);
        mAmPmView.setAdapter(new ArrayWheelAdapter<>(mAmPmList));

        mHourList.clear();
        final int start = timeFormat == 12 ? 1 : 0;
        final int end = timeFormat == 12 ? 12 : 23;
        for (int i = start; i <= end; i++) {
            mHourList.add(fillZero(i));
        }
        mHourView.setAdapter(new ArrayWheelAdapter<>(mHourList));

        if (timeFormat == 12) {
            if (!TextUtils.isEmpty(mSelectedHour)) {//原来是24小时制，判断小时是否超过12点
                String newHourStr;
                int originalHour = Integer.valueOf(mSelectedHour);
                if (originalHour >= 12) {//下午的时间处理，24小时制的12:00将显示为：下午12:00
                    newHourStr = fillZero(originalHour == 12 ? 12 : originalHour - 12);
                    mAmPmView.setCurrentItem(mSelectedAmPm = 1);

                } else {//早上的时间处理，24小时制的00:00将显示为：上午12:00
                    newHourStr = originalHour == 0 ? fillZero(12) : mSelectedHour;
                    mAmPmView.setCurrentItem(mSelectedAmPm = 0);
                }
                mSelectedHour = newHourStr;
                mHourView.setCurrentItem(mHourList.indexOf(newHourStr));
            }
            mAmPmView.setVisibility(VISIBLE);

        } else {
            //如果原来12小时制时是下午的时间，则小时数增加12
            if (!TextUtils.isEmpty(mSelectedHour)) {
                String newHourStr;
                int hour = Integer.valueOf(mSelectedHour);
                if (mSelectedAmPm == 1) {
                    if (hour != 12) {
                        hour += 12;
                    }
                    newHourStr = String.valueOf(hour);
                } else {//上午12点转成24小时制
                    if (hour == 12) {
                        hour = 0;
                    }
                    newHourStr = fillZero(hour);
                }
                mSelectedHour = newHourStr;
                mHourView.setCurrentItem(mHourList.indexOf(newHourStr));
            }

            mAmPmView.setVisibility(GONE);
        }
//        mSelectedHour = mHourView.getCurrentItem();
        mHourStyle = timeFormat;
        return true;
    }

    /**
     * 以新的间隔，更新分钟滚轮的数据，如gap=1时，数据为：0,1,2,3...59；gap为15时，数据为：0,15,30,45
     *
     * @param gap 间隔数大小
     * @return 更新数据后当前选中的分钟数
     */
    public String updateMinuteDateWithGap(int gap) {
        mMinuteGap = gap;
        mMinuteList.clear();
        for (int i = 0; i < 60; i = i + gap) {
            mMinuteList.add(fillZero(i));
        }
        mMinuteView.setAdapter(new ArrayWheelAdapter<>(mMinuteList));

        if (gap > 1) {
            mSelectedMinute = fillZero(getNearMinuteInCurrGap(mSelectedMinute));
        }
        int selectedIndex = mMinuteList.indexOf(mSelectedMinute);
        mMinuteView.setCurrentItem(selectedIndex);
        return mSelectedMinute;
    }

    public static int trimZero(@NonNull String text) {
        try {
            if (text.startsWith("0")) {
                text = text.substring(1);
            }
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @NonNull
    public static String fillZero(int number) {
        return number < 10 ? "0" + number : String.valueOf(number);
    }

    public boolean is12_Hour() {
        return mHourStyle == 12;
    }

    public boolean isAmHour() {
        return mSelectedAmPm == 0;
    }

    public String getSelectedHour() {
        return mSelectedHour;
    }

    public String getSelectedMinute() {
        return mSelectedMinute;
    }

    /**
     * 设置选中的时间
     *
     * @param timeStr 时间格式：01:30 / 11:45
     */
    public void setSelectedTime(String timeStr) {
        if (timeStr == null) {
            return;
        }

        if (!timeStr.matches("^(([0,1][0-9])|(2[0-3])):([0-5][0-9])$")) {//检查传入的时间是否合法
            return;
        }


        if (mMinuteGap == 15) {
            if (timeStr.matches("^(([0,1][0-9])|(2[0-3])):((00)|(15)|(30)|(45))$")) {//传入的时间的分钟数刚好是15分为间隔
                String[] times = timeStr.split(":");
                parseTimeForHourStyle(times);
                mSelectedHour = times[0];
                mSelectedMinute = times[1];

                mHourView.setCurrentItem(mHourList.indexOf(mSelectedHour));
                mMinuteView.setCurrentItem(mMinuteList.indexOf(mSelectedMinute));

            } else {
                String[] times = timeStr.split(":");
                parseTimeForHourStyle(times);
                mSelectedHour = times[0];
                mHourView.setCurrentItem(mHourList.indexOf(mSelectedHour));

//              minuteIndex = timeInt / 15 + 1;
                mSelectedMinute = fillZero(getNearMinuteInCurrGap(times[1]));
                int minuteIndex = mMinuteList.indexOf(mSelectedMinute);
                mMinuteView.setCurrentItem(minuteIndex);

                if (onWheelListener != null) {//因为传入的分钟数不符合15分为间隔而进行了"取整"，所以要通过外面更新目前选中的分钟数
                    onWheelListener.onMinuteWheeled(minuteIndex, mSelectedMinute);
                }
            }
        } else {
            String[] times = timeStr.split(":");
            parseTimeForHourStyle(times);
            mSelectedHour = times[0];
            mSelectedMinute = times[1];
            mHourView.setCurrentItem(mHourList.indexOf(mSelectedHour));
            mMinuteView.setCurrentItem(mMinuteList.indexOf(mSelectedMinute));
        }
    }

    private void parseTimeForHourStyle(String[] times) {
        if (is12_Hour()) {
            int hour = Integer.valueOf(times[0]);
            if (hour >= 12) {
                mAmPmView.setCurrentItem(mSelectedAmPm = 1);
                times[0] = fillZero(hour == 12 ? 12 : hour - 12);
            } else {
                times[0] = hour == 0 ? fillZero(12) : times[0];
                mAmPmView.setCurrentItem(mSelectedAmPm = 0);
            }
        }
    }

    /**
     * 参考{@link #getNearMinuteInCurrGap(int)}
     *
     * @param minute
     * @return
     */
    private int getNearMinuteInCurrGap(String minute) {
        int minuteInt = 0;
        try {
            minuteInt = Integer.valueOf(minute);
        } catch (Exception e) {
        }
        return getNearMinuteInCurrGap(minuteInt);
    }

    /**
     * 通过已给的分钟数，转换成当前分钟间隔数下最接近的某个数值，如当{@link #mMinuteGap}=15时，17转为15，23转为30。
     *
     * @param minute 想要转为的分钟数
     * @return 当前分钟间隔下的分钟数据列表中的一项
     */
    private int getNearMinuteInCurrGap(int minute) {
        int nearMinute;
        int scale = minute / mMinuteGap;
        int remainder = minute % mMinuteGap;
        if (remainder == 0) {
            nearMinute = minute;
        } else if (remainder <= mMinuteGap / 2) {
            nearMinute = scale * mMinuteGap;
        } else {
            nearMinute = (scale + 1) * mMinuteGap;
        }
        return nearMinute;
    }

    public void updateViewForLocale() {
        mAmPmList.clear();
        String[] amPm = DPLManager.getInstance().getAmPmStr();
        mAmPmList.add(amPm[0]);
        mAmPmList.add(amPm[1]);
        mAmPmView.setAdapter(new ArrayWheelAdapter<>(mAmPmList));
    }

    public void setOnWheelListener(OnWheelListener onWheelListener) {
        this.onWheelListener = onWheelListener;
    }

    public interface OnWheelListener {

        void onAmPmWheeled(int index, String amPm);

        void onHourWheeled(int index, String hour);

        void onMinuteWheeled(int index, String minute);

    }
}
