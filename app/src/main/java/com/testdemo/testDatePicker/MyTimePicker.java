package com.testdemo.testDatePicker;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.testdemo.R;
import com.testdemo.broken_lib.Utils;
import com.testdemo.testDatePicker.wheelView.ArrayWheelAdapter;
import com.testdemo.testDatePicker.wheelView.LineConfig;
import com.testdemo.testDatePicker.wheelView.OnItemPickListener;
import com.testdemo.testDatePicker.wheelView.WheelView;

import java.util.ArrayList;


public class MyTimePicker extends LinearLayout {

    final WheelView mHourView = new WheelView(getContext());
    final WheelView mMinuteView = new WheelView(getContext());
    private ArrayList<String> mHourList = new ArrayList<>();
    private ArrayList<String> mMinuteList = new ArrayList<>();

    private OnWheelListener onWheelListener;

    private int mSelectedHourIndex, mSelectedMinuteIndex;
    private String mSelectedHour, mSelectedMinute;

    public MyTimePicker(Context context) {
        this(context, null);
    }

    public MyTimePicker(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyTimePicker(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setGravity(Gravity.CENTER);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;


        for (int i = 1; i <= 24; i++) {
            mHourList.add(fillZero(i));
        }

        for (int i = 0; i < 60; i = i + 15) {
            mMinuteList.add(fillZero(i));
        }

        mHourView.setCanLoop(true);
//        mHourView.setSelectedTextColor(textColorFocus);
//        mHourView.setUnSelectedTextColor(textColorNormal);
        mHourView.setDividerType(LineConfig.DividerType.FILL);
        mHourView.setAdapter(new ArrayWheelAdapter<>(mHourList));
//        mHourView.setCurrentItem(mSelectedHourIndex);
//        mHourView.setLineConfig(lineConfig);
        mHourView.setLayoutParams(layoutParams);
        mHourView.setOnItemPickListener(new OnItemPickListener<String>() {
            @Override
            public void onItemPicked(int index, String item) {
                mSelectedHourIndex = index;
                mSelectedHour = item;
                if (onWheelListener != null) {
                    onWheelListener.onHourWheeled(index, item);
                }
               /* if (!canLinkage) {
                    return;
                }
//                changeMinuteData(trimZero(item));
                mMinuteView.setAdapter(new ArrayWheelAdapter<>(minutes));
                mMinuteView.setCurrentItem(mSelectedMinuteIndex);*/
            }
        });
        addView(mHourView);

        TextView labelView = new TextView(getContext());
        LayoutParams lableLP = new LayoutParams(layoutParams.width, layoutParams.height);
        lableLP.leftMargin = Utils.dp2px(29);
        lableLP.rightMargin = Utils.dp2px(27);
        labelView.setLayoutParams(lableLP);
//        labelView.setTextColor(textColorFocus);
        labelView.setTextSize(getResources().getDimensionPixelSize(R.dimen.text_wheel_time_colon));
        labelView.setText(":");
        addView(labelView);

        //分钟
        mMinuteView.setCanLoop(true);
//        mMinuteView.setTextSize(textSize);//must be called before setDateList
//        mMinuteView.setSelectedTextColor(textColorFocus);
//        mMinuteView.setUnSelectedTextColor(textColorNormal);
        mMinuteView.setAdapter(new ArrayWheelAdapter<>(mMinuteList));
//        mMinuteView.setCurrentItem(mSelectedMinuteIndex);
        mMinuteView.setDividerType(LineConfig.DividerType.FILL);
//        mMinuteView.setLineConfig(lineConfig);
        mMinuteView.setLayoutParams(layoutParams);
        addView(mMinuteView);
        mMinuteView.setOnItemPickListener(new OnItemPickListener<String>() {
            @Override
            public void onItemPicked(int index, String item) {
                mSelectedMinuteIndex = index;
                mSelectedMinute = item;
                if (onWheelListener != null) {
                    onWheelListener.onMinuteWheeled(index, item);
                }
            }
        });
    }


    /*private void changeMinuteData(int selectedHour) {
        if (startHour == endHour) {
            if (startMinute > endMinute) {
                int temp = startMinute;
                startMinute = endMinute;
                endMinute = temp;
            }
            for (int i = startMinute; i <= endMinute; i+= stepMinute) {
                minutes.add(DateUtils.fillZero(i));
            }
        } else if (selectedHour == startHour) {
            for (int i = startMinute; i <= 59; i+= stepMinute) {
                minutes.add(DateUtils.fillZero(i));
            }
        } else if (selectedHour == endHour) {
            for (int i = 0; i <= endMinute; i+= stepMinute) {
                minutes.add(DateUtils.fillZero(i));
            }
        } else {
            for (int i = 0; i <= 59; i+= stepMinute) {
                minutes.add(DateUtils.fillZero(i));
            }
        }
        if (minutes.indexOf(selectedMinute) == -1) {
            //当前设置的分钟不在指定范围，则默认选中范围开始的分钟
            selectedMinute = minutes.get(0);
        }
    }*/

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

    public void setSelectedTime(String timeStr) {
        if (timeStr == null) {
            return;
        }

        if (timeStr.matches("^((0[1-9])|(1[0-9])|(2[0-4])):((00)|(15)|(30)|(45))$")) {
            String[] times = timeStr.split(":");
            mSelectedHour = times[0];
            mSelectedMinute = times[1];
            mHourView.setCurrentItem(mHourList.indexOf(mSelectedHour));
            mMinuteView.setCurrentItem(mMinuteList.indexOf(mSelectedMinute));
        }
    }

    public void setOnWheelListener(OnWheelListener onWheelListener) {
        this.onWheelListener = onWheelListener;
    }

    public interface OnWheelListener {

        void onYearWheeled(int index, String year);

        void onMonthWheeled(int index, String month);

        void onDayWheeled(int index, String day);

        void onHourWheeled(int index, String hour);

        void onMinuteWheeled(int index, String minute);

    }
}
