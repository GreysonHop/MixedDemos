package com.testdemo.testDatePicker;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.testdemo.R;
import com.testdemo.broken_lib.Utils;
import com.testdemo.testDatePicker.wheelView.ArrayWheelAdapter;
import com.testdemo.testDatePicker.wheelView.LineConfig;
import com.testdemo.testDatePicker.wheelView.OnItemPickListener;
import com.testdemo.testDatePicker.wheelView.WheelView;

import java.util.ArrayList;

/**
 * Created by Greyson
 */
public class TimePicker extends LinearLayout {

    private final WheelView mHourView = new WheelView(getContext());
    private final WheelView mMinuteView = new WheelView(getContext());
    private ArrayList<String> mHourList = new ArrayList<>();
    private ArrayList<String> mMinuteList = new ArrayList<>();

    private OnWheelListener onWheelListener;

    private String mSelectedHour, mSelectedMinute;

    public TimePicker(Context context) {
        this(context, null);
    }

    public TimePicker(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TimePicker(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setGravity(Gravity.CENTER);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER_VERTICAL;

        for (int i = 0; i <= 23; i++) {
            mHourList.add(fillZero(i));
        }

        mHourView.setCanLoop(false);
        mHourView.setTypeface(Typeface.SERIF);
        mHourView.setDividerType(LineConfig.DividerType.FILL);
        mHourView.setAdapter(new ArrayWheelAdapter<>(mHourList));
        mSelectedHour = mHourView.getCurrentItem();
//        mHourView.setLineConfig(lineConfig);
        mHourView.setLayoutParams(layoutParams);
        mHourView.setOnItemPickListener(new OnItemPickListener<String>() {
            @Override
            public void onItemPicked(int index, String item) {
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
        lableLP.gravity = layoutParams.gravity;
        lableLP.bottomMargin = Utils.dp2px(3);
        lableLP.leftMargin = Utils.dp2px(29);
        lableLP.rightMargin = Utils.dp2px(27);
        labelView.setLayoutParams(lableLP);
        labelView.setTextColor(Color.parseColor("#283851"));
        labelView.setTextSize(getResources().getDimensionPixelSize(R.dimen.text_wheel_time_colon));
        labelView.setText(":");
        addView(labelView);

        //分钟
        mMinuteView.setCanLoop(false);
        mMinuteView.setTypeface(Typeface.DEFAULT);
        updateMinuteDateWithGap(15);
        mSelectedMinute = mMinuteView.getCurrentItem();
        mMinuteView.setDividerType(LineConfig.DividerType.FILL);
//        mMinuteView.setLineConfig(lineConfig);
        mMinuteView.setLayoutParams(layoutParams);
        addView(mMinuteView);
        mMinuteView.setOnItemPickListener(new OnItemPickListener<String>() {
            @Override
            public void onItemPicked(int index, String item) {
                mSelectedMinute = item;
                if (onWheelListener != null) {
                    onWheelListener.onMinuteWheeled(index, item);
                }
            }
        });
    }

    public void updateMinuteDateWithGap(int gap) {
        mMinuteList.clear();
        for (int i = 0; i < 60; i = i + gap) {
            mMinuteList.add(fillZero(i));
        }
        mMinuteView.setAdapter(new ArrayWheelAdapter<>(mMinuteList));
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

        if (timeStr.matches("^((0[1-9])|(1[0-9])|(2[0-4])):((00)|(15)|(30)|(45))$")) {
            String[] times = timeStr.split(":");
            mSelectedHour = times[0];
            mSelectedMinute = times[1];
            mHourView.setCurrentItem(mHourList.indexOf(mSelectedHour));
            mMinuteView.setCurrentItem(mMinuteList.indexOf(mSelectedMinute));

        } else if (timeStr.matches("^((0[1-9])|(1[0-9])|(2[0-4])):([0-5][0-9])$")) {
            String[] times = timeStr.split(":");
            mSelectedHour = times[0];
            mHourView.setCurrentItem(mHourList.indexOf(mSelectedHour));

            int minuteIndex = 0;
            try {
                int timeInt = Integer.valueOf(times[1]);
                minuteIndex = timeInt / 15 + 1;
                if (minuteIndex == 4) {
                    minuteIndex--;
                }
            } catch (Exception e) {
            }

            mSelectedMinute = mMinuteList.get(minuteIndex);
            mMinuteView.setCurrentItem(minuteIndex);

            if (onWheelListener != null) {
                onWheelListener.onMinuteWheeled(minuteIndex, mSelectedMinute);
            }
        }
    }

    public void setOnWheelListener(OnWheelListener onWheelListener) {
        this.onWheelListener = onWheelListener;
    }

    public interface OnWheelListener {

        void onHourWheeled(int index, String hour);

        void onMinuteWheeled(int index, String minute);

    }
}
