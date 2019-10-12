package com.testdemo.testDatePicker;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.testdemo.R;
import com.testdemo.broken_lib.Utils;
import com.testdemo.testDatePicker.datepicker.bizs.languages.DPLManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.view.Gravity.CENTER;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created by Greyson
 */
public class DatePickDialog extends Dialog {
    public static final int MODE_DATE_AND_TIME = 10;
    public static final int MODE_DATE_ONLY = 1;
    public static final int MODE_TIME_ONLY = 2;

    private int mMode;
    private DPLManager mDPLManager = DPLManager.getInstance();

    private ViewGroup clDatePicker;
    private TextView mTvOnlyOneSwitch;
    private RadioGroup rgSwitchDateTime;
    private RadioButton cbDateBtn;
    private RadioButton cbTimeBtn;
    private TextView mTvConfirm;

    private FrameLayout mFlContentPanel;//包含日期、时间选择组件的面板
    private View vInflater;
    private LinearLayout mLlCalendarPicker;//包含星期组件的日期选择面板
    private LinearLayout mLlWeek;
    private MyCalendarPicker myCalendarPicker;
    private MyTimePicker myTimePicker;

    private String selectedDateStr;
    private String selectedTimeStr;

    private OnDatePickListener onDatePickListener;

    public DatePickDialog(Context context) {
        this(context, -1);
    }

    public DatePickDialog(Context context, int mode) {
        this(context, R.style.ActionSheetDialogStyle, mode);
    }

    public DatePickDialog(Context context, int themeResId, int mode) {
        super(context, themeResId);
        mMode = mode;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_date_pick);
        initLayoutParams();

        clDatePicker = findViewById(R.id.cl_select_date);
        mTvOnlyOneSwitch = findViewById(R.id.tv_only_one_switch);
        rgSwitchDateTime = findViewById(R.id.rg_switch_date_time);
        cbDateBtn = findViewById(R.id.cb_date_btn);
        cbTimeBtn = findViewById(R.id.cb_time_btn);
        mTvConfirm = findViewById(R.id.tv_confirm);
        mTvConfirm.setText(mDPLManager.titleEnsure());
        mFlContentPanel = findViewById(R.id.fl_content_panel);

//        vInflater = findViewById(R.id.v_inflater);
//        mLlCalendarPicker = findViewById(R.id.ll_calendar_picker);
//        mLlWeek = findViewById(R.id.ll_week);
//        myCalendarPicker = findViewById(R.id.myCalendarPicker);
//        myTimePicker = findViewById(R.id.myTimePicker);
        vInflater = new View(getContext());
        ViewGroup.LayoutParams params = vInflater.getLayoutParams();
        mFlContentPanel.addView(vInflater, params);

        setListener();
        if (mMode != -1) {
            updateView();
            setSelectedDate(new Date());//default to select today
        }
    }

    private void initLayoutParams() {
        Window window = getWindow();
        if (window != null) {
            window.setGravity(Gravity.BOTTOM);
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = MATCH_PARENT;
            lp.height = WRAP_CONTENT;
            window.setAttributes(lp);
            window.setWindowAnimations(R.style.animBottomMenu);
        }
    }

    public void changeMode(int toBeMode) {
        if (toBeMode == mMode) {
            return;
        }
        mMode = toBeMode;
        updateView();
    }

    private void updateView() {
        switch (mMode) {
            case MODE_DATE_AND_TIME:
                setTimePanel();
                setCalendarPanel();
                myTimePicker.setVisibility(View.GONE);
                break;

            case MODE_DATE_ONLY:
                setCalendarPanel();
                if (myTimePicker != null) {
                    myTimePicker.setVisibility(View.GONE);
                }
                break;

            case MODE_TIME_ONLY:
                setTimePanel();
                if (mLlCalendarPicker != null) {
                    mLlCalendarPicker.setVisibility(View.GONE);
                }

                /*LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) myTimePicker.getLayoutParams();
                params.gravity = Gravity.CENTER;
                params.height = Utils.dp2px(200);
                params.width = WRAP_CONTENT;*/
                break;
        }
    }

    private void initCalendarPanel() {
        mLlCalendarPicker = new LinearLayout(getContext());
        mLlCalendarPicker.setOrientation(LinearLayout.VERTICAL);

        myCalendarPicker = new MyCalendarPicker(getContext());
        myCalendarPicker.setOnDayClickListener(((clickDay, selectedDays) -> {
            if (TextUtils.isEmpty(clickDay)) {
                return;
            }

            selectedDateStr = clickDay;
            String[] dateDatas = clickDay.split("-");

            Calendar calendar = Calendar.getInstance(Locale.getDefault());
            calendar.set(Calendar.YEAR, Integer.valueOf(dateDatas[0]));
            calendar.set(Calendar.MONTH, Integer.valueOf(dateDatas[1]) - 1);
            calendar.set(Calendar.DAY_OF_MONTH, Integer.valueOf(dateDatas[2]));
            if (mMode == MODE_DATE_AND_TIME) {
                cbDateBtn.setText(DPLManager.getInstance().getDateFormat().format(calendar.getTime()));
            } else {
                mTvOnlyOneSwitch.setText(DPLManager.getInstance().getDateFormat().format(calendar.getTime()));
            }

        }));

        mLlWeek = new LinearLayout(getContext());
        LinearLayout.LayoutParams lpWeek = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        lpWeek.weight = 1;
        for (int i = 0; i < mDPLManager.titleWeek().length; i++) {
            TextView tvWeek = new TextView(getContext());
            tvWeek.setText(mDPLManager.titleWeek()[i]);
            tvWeek.setGravity(CENTER);
            tvWeek.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
            tvWeek.setTextColor(Color.parseColor("#283851"));
            mLlWeek.addView(tvWeek, lpWeek);
        }
    }

    private void setCalendarPanel() {
        if (mLlCalendarPicker == null) {
            initCalendarPanel();

            mLlCalendarPicker.addView(mLlWeek);
            mLlCalendarPicker.addView(myCalendarPicker);
            mFlContentPanel.addView(mLlCalendarPicker);
        }
    }

    private void initTimePanel() {
        myTimePicker = new MyTimePicker(getContext());
        myTimePicker.setGravity(CENTER);
        myTimePicker.setOnWheelListener(new MyTimePicker.OnWheelListener() {
            @Override
            public void onHourWheeled(int index, String hour) {
                String timeStr = cbTimeBtn.getText().toString();
                selectedTimeStr = timeStr.replaceFirst("^\\w{2}(?=:)", hour);
                if (mMode == MODE_DATE_AND_TIME) {
                    cbTimeBtn.setText(selectedTimeStr);
                } else {
                    mTvOnlyOneSwitch.setText(selectedTimeStr);
                }
            }

            @Override
            public void onMinuteWheeled(int index, String minute) {
                String timeStr = cbTimeBtn.getText().toString();
                selectedTimeStr = timeStr.replaceFirst("(?<=:)\\w{2}$", minute);
                if (mMode == MODE_DATE_AND_TIME) {
                    cbTimeBtn.setText(selectedTimeStr);
                } else {
                    mTvOnlyOneSwitch.setText(selectedTimeStr);
                }
            }
        });
    }

    private void setTimePanel() {
        if (myTimePicker == null) {
            initTimePanel();
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(WRAP_CONTENT, Utils.dp2px(200));
            params.gravity = CENTER;
            mFlContentPanel.addView(myTimePicker, params);
        }
    }

    private void setListener() {
        rgSwitchDateTime.setOnCheckedChangeListener((group, checkedId) -> {
            if (cbDateBtn.getId() == checkedId) {
                mLlWeek.setVisibility(View.VISIBLE);
                clDatePicker.setBackgroundColor(getContext().getResources().getColor(R.color.grey_date_picker));
                checkCbDateBtn(true);

                vInflater.getLayoutParams().height = 0;

            } else if (cbTimeBtn.getId() == checkedId) {
                mLlWeek.setVisibility(View.GONE);
                clDatePicker.setBackgroundColor(getContext().getResources().getColor(R.color.white));
                checkCbDateBtn(false);

                vInflater.getLayoutParams().height = mLlCalendarPicker.getHeight();
            }
        });

        mTvConfirm.setOnClickListener(clickView -> {
            if (selectedDateStr == null) {
                Toast.makeText(getContext(), "请选择日期", Toast.LENGTH_SHORT).show();
                return;
            }
            if (onDatePickListener != null) {
                onDatePickListener.onDatePick(selectedDateStr, selectedTimeStr);
            }
        });
    }

    /**
     * 设置默认选中的日期，并且显示所在月的视图，类似于{@link #setSelectedDate(Date date)}
     *
     * @param selectedDateStr 字符串类型的日期，格式为"****-**-**"；为null时则采用组件内的默认值
     *                        ，月、日为一位数时前面可以不补0，如2019-7-1
     * @param selectedTimeStr 时间格式为"**:**"；为null时则采用组件内的默认值
     *                        ，数字为一位数时前面必须补0，如01:15
     */
    public void setSelectedDate(String selectedDateStr, String selectedTimeStr) {//todo need to fix like {@link #setSelectedDate(Date date)}
        if (selectedDateStr != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
            Date selectDate;
            try {
                selectDate = dateFormat.parse(selectedDateStr);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(String.format("传入的日期\"%s\"格式有问题! ", selectedDateStr));
                return;
            }

//            cbDateBtn.setText(String.format(Locale.CHINA, "%tY年%tm月%td日", selectDate, selectDate, selectDate));
            cbDateBtn.setText(DPLManager.getInstance().getDateFormat().format(selectDate));
            this.selectedDateStr = selectedDateStr;
            myCalendarPicker.setSelectedDay(selectedDateStr);
        }

        if (selectedTimeStr != null) {
            cbTimeBtn.setText(selectedTimeStr);
            this.selectedTimeStr = selectedTimeStr;
            myTimePicker.setSelectedTime(selectedTimeStr);
        }
    }

    /**
     * 设置默认选中的日期，并且显示所在月的视图
     *
     * @param date
     */
    public void setSelectedDate(Date date) {
        if (date == null) {
            return;
        }

        String selectedDateStr = new SimpleDateFormat("yyyy-M-d", Locale.CHINA).format(date);
        String formatSelectedDateStr = DPLManager.getInstance().getDateFormat().format(date);
        this.selectedDateStr = selectedDateStr;

        String selectedTimeStr = new SimpleDateFormat("HH:mm", Locale.CHINA).format(date);
        this.selectedTimeStr = selectedTimeStr;

        if (mMode == MODE_DATE_AND_TIME) {
            cbDateBtn.setText(formatSelectedDateStr);
            cbTimeBtn.setText(selectedTimeStr);
            myCalendarPicker.setSelectedDay(selectedDateStr);
            myTimePicker.setSelectedTime(selectedTimeStr);

        } else if (mMode == MODE_DATE_ONLY) {
            mTvOnlyOneSwitch.setText(formatSelectedDateStr);
            myCalendarPicker.setSelectedDay(selectedDateStr);

        } else if (mMode == MODE_TIME_ONLY) {
            mTvOnlyOneSwitch.setText(selectedTimeStr);
            myTimePicker.setSelectedTime(selectedTimeStr);
        }
    }

    /**
     * 显示某个月的视图，不选中任一天，也不清除已选中的日期
     *
     * @param year  要显示的月份所在的年份
     * @param month 要显示的月份
     */
    public void showMonth(int year, int month) {
        myCalendarPicker.setShowMonth(year, month);
    }

    private void checkCbDateBtn(boolean isChecked) {
        if (isChecked) {
            cbTimeBtn.setTextColor(getContext().getResources().getColor(R.color.blue_date_picker));
            cbDateBtn.setTextColor(Color.WHITE);
            myCalendarPicker.setVisibility(View.VISIBLE);
            myTimePicker.setVisibility(View.GONE);

        } else {
            cbTimeBtn.setTextColor(Color.WHITE);
            cbDateBtn.setTextColor(getContext().getResources().getColor(R.color.blue_date_picker));
            myCalendarPicker.setVisibility(View.GONE);
            myTimePicker.setVisibility(View.VISIBLE);
        }
    }

    public void setOnDatePickListener(OnDatePickListener onDatePickListener) {
        this.onDatePickListener = onDatePickListener;
    }

    public interface OnDatePickListener {
        /**
         * @param date the date selected is in format of "****-**-**".
         *             年月日格式如"2019-7-12", "2019-11-1"
         * @param time the time selected is in format of "**:**".
         *             时分格式如"20:15", "09:00"
         */
        void onDatePick(String date, String time);
    }

}
