package com.testdemo.testDatePicker;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.testdemo.R;
import com.testdemo.testDatePicker.datepicker.bizs.decors.DPDecor;
import com.testdemo.testDatePicker.datepicker.cons.DPMode;
import com.testdemo.testDatePicker.datepicker.views.DatePicker;

public class CreateInfoAct extends Activity {

    private RadioGroup rgSwitchDateTime;
    private RadioButton cbDateBtn;
    private RadioButton cbTimeBtn;

    private View vInflater;
    private MyCalendarPicker myCalendarPicker;
    private MyTimePicker myTimePicker;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.act_create_info);

        ConstraintLayout clDatePicker = findViewById(R.id.cl_select_date);
        rgSwitchDateTime = findViewById(R.id.rg_switch_date_time);
        cbDateBtn = findViewById(R.id.cb_date_btn);
        cbTimeBtn = findViewById(R.id.cb_time_btn);

        vInflater = findViewById(R.id.v_inflater);
        myCalendarPicker = findViewById(R.id.myCalendarPicker);
        myTimePicker = findViewById(R.id.myTimePicker);

        rgSwitchDateTime.setOnCheckedChangeListener((group, checkedId) -> {
            if (cbDateBtn.getId() == checkedId) {
                clDatePicker.setBackgroundColor(getResources().getColor(R.color.grey_date_picker));
                checkCbDateBtn(true);

                vInflater.getLayoutParams().height = 0;

                myCalendarPicker.setVisibility(View.VISIBLE);
                myTimePicker.setVisibility(View.GONE);

            } else if (cbTimeBtn.getId() == checkedId) {
                clDatePicker.setBackgroundColor(getResources().getColor(R.color.white));
                checkCbDateBtn(false);

                vInflater.getLayoutParams().height = myCalendarPicker.getHeight();

                myCalendarPicker.setVisibility(View.GONE);
                myTimePicker.setVisibility(View.VISIBLE);
            }
        });

        myCalendarPicker.setOnDayClickListener(((clickDay, selectedDays) -> {
            if (TextUtils.isEmpty(clickDay)) {
                return;
            }
            String[] dateDatas = clickDay.split("-");
            cbDateBtn.setText(getString(R.string.text_calendar_cn, dateDatas[0], dateDatas[1], dateDatas[2]));
        }));


        DatePicker picker = (DatePicker) findViewById(R.id.main_dp);
        picker.setDate(2019, 7);
        picker.setFestivalDisplay(false);
        picker.setTodayDisplay(false);
        picker.setHolidayDisplay(false);
        picker.setDeferredDisplay(false);
        picker.setMode(DPMode.MULTIPLE);
        picker.setDPDecor(new DPDecor() {
            @Override
            public void drawDecorTL(Canvas canvas, Rect rect, Paint paint, String data) {
                super.drawDecorTL(canvas, rect, paint, data);
                switch (data) {
                    case "2019-07-5":
                    case "2019-07-7":
                    case "2019-07-9":
                    case "2019-07-11":
                        paint.setColor(Color.GREEN);
                        canvas.drawRect(rect, paint);
                        break;
                    default:
                        paint.setColor(Color.RED);
                        canvas.drawCircle(rect.centerX(), rect.centerY(), rect.width() / 2, paint);
                        break;
                }
            }

            @Override
            public void drawDecorTR(Canvas canvas, Rect rect, Paint paint, String data) {
                super.drawDecorTR(canvas, rect, paint, data);
                switch (data) {
                    case "2019-07-10":
                    case "2019-07-11":
                    case "2019-07-12":
                        paint.setColor(Color.BLUE);
                        canvas.drawCircle(rect.centerX(), rect.centerY(), rect.width() / 2, paint);
                        break;
                    default:
                        paint.setColor(Color.YELLOW);
                        canvas.drawRect(rect, paint);
                        break;
                }
            }
        });

        myCalendarPicker.setDate(2019, 7);
    }

    private void checkCbDateBtn(boolean isChecked) {
        if (isChecked) {
            cbTimeBtn.setTextColor(getResources().getColor(R.color.blue_date_picker));
            cbDateBtn.setTextColor(Color.WHITE);
        } else {
            cbTimeBtn.setTextColor(Color.WHITE);
            cbDateBtn.setTextColor(getResources().getColor(R.color.blue_date_picker));
        }
    }

    public void onClick(View view) {

    }
}
