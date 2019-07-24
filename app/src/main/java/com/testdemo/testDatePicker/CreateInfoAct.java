package com.testdemo.testDatePicker;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.testdemo.R;
import com.testdemo.testDatePicker.datepicker.bizs.decors.DPDecor;
import com.testdemo.testDatePicker.datepicker.cons.DPMode;
import com.testdemo.testDatePicker.datepicker.views.DatePicker;

public class CreateInfoAct extends Activity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.act_create_info);

        DatePicker picker = (DatePicker) findViewById(R.id.main_dp);
        picker.setDate(2019, 7);
        picker.setFestivalDisplay(false);
        picker.setTodayDisplay(false);
        picker.setHolidayDisplay(false);
        picker.setDeferredDisplay(false);
        picker.setMode(DPMode.NONE);
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

        MyCalendarPicker myDate = (MyCalendarPicker) findViewById(R.id.myDate);
        myDate.setDate(2019, 7);
    }

    public void onClick(View view) {

    }
}
