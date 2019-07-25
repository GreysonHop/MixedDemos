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

        final WheelView hourView = new WheelView(getContext());
        final WheelView minuteView = new WheelView(getContext());

        ArrayList<String> hours = new ArrayList<>();
        for (int i = 1; i <= 24; i++) {
            hours.add(fillZero(i));
        }
        ArrayList<String> minutes = new ArrayList<>();
        for (int i = 0; i < 60; i = i + 15) {
            minutes.add(fillZero(i));
        }

        hourView.setCanLoop(true);
//        hourView.setSelectedTextColor(textColorFocus);
//        hourView.setUnSelectedTextColor(textColorNormal);
        hourView.setDividerType(LineConfig.DividerType.FILL);
        hourView.setAdapter(new ArrayWheelAdapter<>(hours));
//        hourView.setCurrentItem(selectedHourIndex);
//        hourView.setLineConfig(lineConfig);
        hourView.setLayoutParams(layoutParams);
        /*hourView.setOnItemPickListener(new OnItemPickListener<String>() {
            @Override
            public void onItemPicked(int index, String item) {
                selectedHourIndex = index;
                selectedMinuteIndex = 0;
                selectedHour = item;
                if (onWheelListener != null) {
                    onWheelListener.onHourWheeled(index, item);
                }
                if (!canLinkage) {
                    return;
                }
//                changeMinuteData(trimZero(item));
                minuteView.setAdapter(new ArrayWheelAdapter<>(minutes));
                minuteView.setCurrentItem(selectedMinuteIndex);
            }
        });*/
        addView(hourView);

        TextView labelView = new TextView(getContext());
        LayoutParams lableLP = new LayoutParams(layoutParams.width, layoutParams.height);
        lableLP.leftMargin = Utils.dp2px(29);
        lableLP.rightMargin = Utils.dp2px(27);
        labelView.setLayoutParams(lableLP);
//        labelView.setTextColor(textColorFocus);
        labelView.setTextSize(getResources().getDimensionPixelSize(R.dimen.text_wheel_time_colon));
        labelView.setText(" : ");
        addView(labelView);

        //分钟
        minuteView.setCanLoop(true);
//        minuteView.setTextSize(textSize);//must be called before setDateList
//        minuteView.setSelectedTextColor(textColorFocus);
//        minuteView.setUnSelectedTextColor(textColorNormal);
        minuteView.setAdapter(new ArrayWheelAdapter<>(minutes));
//        minuteView.setCurrentItem(selectedMinuteIndex);
        minuteView.setDividerType(LineConfig.DividerType.FILL);
//        minuteView.setLineConfig(lineConfig);
        minuteView.setLayoutParams(layoutParams);
        addView(minuteView);
       /* minuteView.setOnItemPickListener(new OnItemPickListener<String>() {
            @Override
            public void onItemPicked(int index, String item) {
                selectedMinuteIndex = index;
                selectedMinute = item;
                if (onWheelListener != null) {
                    onWheelListener.onMinuteWheeled(index, item);
                }
            }
        });*/
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
}
