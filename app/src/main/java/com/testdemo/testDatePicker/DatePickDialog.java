package com.testdemo.testDatePicker;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.core.content.ContextCompat;

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
 * 2019/10/15 新增 {@link #changeMode(int)} 方法，用于Dialog显示后改变视图模式
 */
public class DatePickDialog extends Dialog {
    public static final int MODE_DATE_AND_TIME = 10;
    public static final int MODE_DATE_ONLY = 1;
    public static final int MODE_TIME_ONLY = 2;

    private boolean hasInit;
    private int mMode;
    private int mMinuteGap = 1;//分钟选择器中分钟数之间的间隔，如平时显示的0,1,2...59，间隔为1
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
    private CalendarPicker mCalendarPicker;
    private TimePicker mTimePicker;

    private String selectedDateStr;
    private String selectedTimeStr;

    private OnDatePickListener onDatePickListener;

    /**
     * 该构造方法默认使用 {@link #MODE_DATE_AND_TIME} 模式，即显示日期和时间视图，不会有默认选中的日期和时间的效果
     * ，可通过 {@link #setSelectedDate(Date)} 或 {@link #setSelectedDate(String, String)} 方法去自定义
     * 选中的日期和时间。
     *
     * @param context dialog所属的上下文对象
     */
    public DatePickDialog(Context context) {
        this(context, MODE_DATE_AND_TIME);
    }

    /**
     * 参考 {@link #DatePickDialog(Context)}
     *
     * @param context
     * @param mode    指定dialog的显示模式，有 {@link #MODE_DATE_AND_TIME}显示日期和时间、{@link #MODE_DATE_ONLY}
     *                只显示日期、{@link #MODE_TIME_ONLY}只显示时间
     */
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

        vInflater = new View(getContext());
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(MATCH_PARENT, 0);
        mFlContentPanel.addView(vInflater, params);

        setListener();
        if (mMode != -1) {
            updateView();
        }
        hasInit = true;
    }

    private void initLayoutParams() {
        Window window = getWindow();
        if (window != null) {
            window.setGravity(Gravity.BOTTOM);
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = MATCH_PARENT;
            lp.height = WRAP_CONTENT;
            window.setAttributes(lp);
//            window.setWindowAnimations(R.style.animBottomMenu);//也可以在style中设置
        }
    }

    @Override
    public void show() {
        super.show();
        if (mDPLManager.checkLocale()) {//检查语言是否变动，是则更新UI
            updateViewForLocale();
        }

        mTimePicker.updateDataForHourStyle();
        if (mTimePicker.is12_Hour()) {
            cbTimeBtn.setText(String.format(
                    "%s %s:%s"
                    , mDPLManager.getAmPmStr()[mTimePicker.isAmHour() ? 0 : 1]
                    , mTimePicker.getSelectedHour()
                    , mTimePicker.getSelectedMinute()));
        } else {
            cbTimeBtn.setText(String.format("%s:%s", mTimePicker.getSelectedHour(), mTimePicker.getSelectedMinute()));
        }
    }

    /**
     * 改变Dialog的显示模式，并更新视图。这样做方便在同一个界面里面需要使用不同模式的dialog时，不用重新创建新的dialog对象。
     *
     * @param toBeMode 指定dialog的显示模式，有 {@link #MODE_DATE_AND_TIME}显示日期和时间、{@link #MODE_DATE_ONLY}
     *                 只显示日期、{@link #MODE_TIME_ONLY}只显示时间
     */
    public void changeMode(int toBeMode) {
        if (toBeMode == mMode) {
            return;
        }
        mMode = toBeMode;

        if (!hasInit) {
            return;
        }
        updateView();
    }

    private void updateView() {
        switch (mMode) {
            case MODE_DATE_AND_TIME:
                setTimePanel();
                setCalendarPanel();
                rgSwitchDateTime.setVisibility(View.VISIBLE);
                mTvOnlyOneSwitch.setVisibility(View.GONE);

                if (rgSwitchDateTime.getCheckedRadioButtonId() == cbDateBtn.getId()) {
                    mLlCalendarPicker.setVisibility(View.VISIBLE);
                    mTimePicker.setVisibility(View.GONE);
                } else {
                    cbDateBtn.setChecked(true);
                }
                if (TextUtils.isEmpty(cbTimeBtn.getText().toString())) {
                    cbTimeBtn.setText(String.format(
                            "%s %s:%s"
                            , mDPLManager.getAmPmStr()[mTimePicker.isAmHour() ? 0 : 1]
                            , mTimePicker.getSelectedHour()
                            , mTimePicker.getSelectedMinute()));
                }
                break;

            case MODE_DATE_ONLY:
                setCalendarPanel();
                rgSwitchDateTime.setVisibility(View.GONE);
                mTvOnlyOneSwitch.setVisibility(View.VISIBLE);
                mTvOnlyOneSwitch.setText(cbDateBtn.getText());
                clDatePicker.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.grey_date_picker));

                if (mTimePicker != null) {
                    mTimePicker.setVisibility(View.GONE);
                }
                mLlCalendarPicker.setVisibility(View.VISIBLE);
                break;

            case MODE_TIME_ONLY:
                setTimePanel();
                rgSwitchDateTime.setVisibility(View.GONE);
                mTvOnlyOneSwitch.setVisibility(View.VISIBLE);
                mTvOnlyOneSwitch.setText(cbTimeBtn.getText());
                clDatePicker.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));

                if (mLlCalendarPicker != null) {
                    mLlCalendarPicker.setVisibility(View.GONE);
                }
                mTimePicker.setVisibility(View.VISIBLE);
                if (TextUtils.isEmpty(cbTimeBtn.getText().toString())) {
                    mTvOnlyOneSwitch.setText(mTimePicker.getSelectedHour() + ":" + mTimePicker.getSelectedMinute());
                }
                break;
        }
    }

    private void initCalendarPanel() {
        mLlCalendarPicker = new LinearLayout(getContext());
        mLlCalendarPicker.setOrientation(LinearLayout.VERTICAL);

        mCalendarPicker = new CalendarPicker(getContext());
        mCalendarPicker.setBackgroundColor(Color.WHITE);
        mCalendarPicker.setOnDayClickListener(((clickDay, selectedDays) -> {
            if (TextUtils.isEmpty(clickDay)) {
                return;
            }

            selectedDateStr = clickDay;
            Date date = getDateFromStr(clickDay);
            cbDateBtn.setText(mDPLManager.getDateFormat().format(date));
            mTvOnlyOneSwitch.setText(mDPLManager.getDateFormat().format(date));

        }));

        mLlWeek = new LinearLayout(getContext());
        LinearLayout.LayoutParams lpWeek = new LinearLayout.LayoutParams(0, WRAP_CONTENT);
        lpWeek.weight = 1;
        for (int i = 0; i < mDPLManager.titleWeek().length; i++) {
            TextView tvWeek = new TextView(getContext());
            tvWeek.setText(mDPLManager.titleWeek()[i]);
            tvWeek.setGravity(CENTER);
            tvWeek.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
            tvWeek.setTextColor(Color.parseColor("#283851"));
            tvWeek.setPadding(0, Utils.dp2px(2), 0, Utils.dp2px(2));
            mLlWeek.addView(tvWeek, lpWeek);
        }
        if (!TextUtils.isEmpty(selectedDateStr)) {
            mCalendarPicker.setSelectedDay(selectedDateStr);
            cbDateBtn.setText(mDPLManager.getDateFormat().format(getDateFromStr(selectedDateStr)));
        }
    }

    private void initTimePanel() {
        mTimePicker = new TimePicker(getContext(), mMinuteGap);
        mTimePicker.setGravity(CENTER);
        mTimePicker.setOnWheelListener(new TimePicker.OnWheelListener() {
            @Override
            public void onAmPmWheeled(int index, String amPm) {
                String timeStr = cbTimeBtn.getText().toString();
                if (TextUtils.isEmpty(timeStr)) {
                    timeStr = mDPLManager.getAmPmStr()[index] + " 01:00";
                } else {
                    timeStr = mDPLManager.getAmPmStr()[index] + timeStr.substring(2);
                }
                selectedTimeStr = timeStr;

                cbTimeBtn.setText(selectedTimeStr);
                mTvOnlyOneSwitch.setText(selectedTimeStr);
            }

            @Override
            public void onHourWheeled(int index, String hour) {
                String timeStr = cbTimeBtn.getText().toString();
                if (TextUtils.isEmpty(timeStr)) {
                    timeStr = "00:" + mTimePicker.getSelectedMinute();
                }
                selectedTimeStr = timeStr.replaceFirst("\\w{2}(?=:)", hour);

                cbTimeBtn.setText(selectedTimeStr);
                mTvOnlyOneSwitch.setText(selectedTimeStr);
            }

            @Override
            public void onMinuteWheeled(int index, String minute) {
                String timeStr = cbTimeBtn.getText().toString();
                if (TextUtils.isEmpty(timeStr)) {
                    timeStr = mTimePicker.getSelectedHour() + ":00";
                }
                selectedTimeStr = timeStr.replaceFirst("(?<=:)\\w{2}$", minute);

                cbTimeBtn.setText(selectedTimeStr);
                mTvOnlyOneSwitch.setText(selectedTimeStr);
            }
        });
        //因为TimePicker初始化之前都是默认24小时制来处理时间的，所以这里的selectedTimeStr肯定是24小时制
        if (!TextUtils.isEmpty(selectedTimeStr)) {
            mTimePicker.setSelectedTime(selectedTimeStr);
            this.selectedTimeStr = parseTimeStr(selectedTimeStr);
        }
    }

    private void setCalendarPanel() {
        if (mLlCalendarPicker == null) {
            initCalendarPanel();

            mLlCalendarPicker.addView(mLlWeek);
            mLlCalendarPicker.addView(mCalendarPicker);
            mFlContentPanel.addView(mLlCalendarPicker);
        }
    }

    private void setTimePanel() {
        if (mTimePicker == null) {
            initTimePanel();
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(WRAP_CONTENT, Utils.dp2px(200));
            params.gravity = CENTER;
            mFlContentPanel.addView(mTimePicker, params);
        }
    }

    private void updateViewForLocale() {
        //更新周
        if (mLlWeek != null) {//初始化Dialog时该View还没初始化吧
            String[] weeks = mDPLManager.titleWeek();
            for (int i = 0; i < weeks.length && i < mLlWeek.getChildCount(); i++) {
                TextView tv = (TextView) mLlWeek.getChildAt(i);
                tv.setText(weeks[i]);
            }
        }

        //更新日期的显示
        if (!TextUtils.isEmpty(selectedDateStr)) {
            Date date = getDateFromStr(selectedDateStr);
            if (cbDateBtn != null)
                cbDateBtn.setText(mDPLManager.getDateFormat().format(date));
            if (mTvOnlyOneSwitch != null)
                mTvOnlyOneSwitch.setText(mDPLManager.getDateFormat().format(date));
        }
        if (mTimePicker != null) {
            mTimePicker.updateViewForLocale();
        }
        mTvConfirm.setText(mDPLManager.titleEnsure());
    }

    private void setListener() {
        rgSwitchDateTime.setOnCheckedChangeListener((group, checkedId) -> {
            if (cbDateBtn.getId() == checkedId) {
                checkCbDateBtn(true);

            } else if (cbTimeBtn.getId() == checkedId) {
                checkCbDateBtn(false);

            }
        });

        mTvConfirm.setOnClickListener(clickView -> {
            if (selectedDateStr == null) {
                Toast.makeText(getContext(), "请选择日期", Toast.LENGTH_SHORT).show();
                return;
            }

            if (onDatePickListener != null) {
                String result;
                if (mTimePicker.is12_Hour()) {
                    try {
                        Date date = new SimpleDateFormat("a hh:mm", mDPLManager.getLocale()).parse(selectedTimeStr);
                        result = new SimpleDateFormat("HH:mm", mDPLManager.getLocale()).format(date);
                    } catch (Exception e) {//保险措施
                        String hourStr = mTimePicker.getSelectedHour();
                        if (mTimePicker.isAmHour()) {
                            result = String.format("%s:%s", hourStr, mTimePicker.getSelectedMinute());
                        } else {
                            int hour = Integer.valueOf(hourStr);
                            if (hour != 12) {
                                hour += 12;
                            }
                            result = String.format("%s:%s", TimePicker.fillZero(hour), mTimePicker.getSelectedMinute());
                        }
                    }
                } else {
                    result = selectedTimeStr;
                }
                onDatePickListener.onDatePick(selectedDateStr, result);
            }
        });
    }

    /**
     * 修改时间选择器中分钟数的间隔，如gap=1时，数据为：0,1,2,3...59；gap为15时，数据为：0,15,30,45。
     *
     * @param gap 分钟数的间隔，小于1的整数都会被处理为1。
     */
    public void changeMinuteGap(int gap) {
        mMinuteGap = gap < 1 ? 1 : gap;
        if (!hasInit) {
            return;
        }

        String str = mTimePicker.updateMinuteDateWithGap(mMinuteGap);
        String timeStr = cbTimeBtn.getText().toString();
        if (TextUtils.isEmpty(timeStr)) {
            timeStr = mTimePicker.getSelectedHour() + ":00";
        }
        selectedTimeStr = timeStr.replaceFirst("(?<=:)\\w{2}$", str);

        cbTimeBtn.setText(selectedTimeStr);
        mTvOnlyOneSwitch.setText(selectedTimeStr);
    }

    /**
     * 设置默认选中的日期，并且显示所在月的视图，类似于{@link #setSelectedDate(Date date)}
     *
     * @param selectedDateStr 字符串类型的日期，格式为"****-**-**"；为null时则采用组件内的默认值
     *                        ，月、日为一位数时前面可以不补0，如2019-7-1
     * @param selectedTimeStr 时间格式为"**:**",24小时制；为null时则采用组件内的默认值
     *                        ，数字为一位数时前面必须补0，如01:15
     */
    public void setSelectedDate(String selectedDateStr, String selectedTimeStr) {
        boolean needUpdate = false;
        String formatSelectedDateStr = "";

        if (!TextUtils.isEmpty(selectedDateStr)) {
            try {
                String dateStrToParse;
                SimpleDateFormat dateFormat;
                if (!TextUtils.isEmpty(selectedTimeStr)) {
                    dateStrToParse = String.format("%s,%s", selectedDateStr, selectedTimeStr);
                    dateFormat = new SimpleDateFormat("yyyy-MM-dd,HH:mm", Locale.CHINA);
                } else {
                    dateStrToParse = selectedDateStr;
                    dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
                }
                formatSelectedDateStr = mDPLManager.getDateFormat().format(dateFormat.parse(dateStrToParse));
                this.selectedDateStr = selectedDateStr;
                needUpdate = true;
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(String.format("传入的日期\"%s\"格式有问题! ", selectedDateStr));
            }
        }

        if (!TextUtils.isEmpty(selectedTimeStr)) {
            this.selectedTimeStr = parseTimeStr(selectedTimeStr);
            needUpdate = true;
        }
        if (hasInit && needUpdate) {
            updateValue(formatSelectedDateStr, selectedTimeStr);
        }
    }

    /**
     * 根据当前时间组件的小时制，解析24小时制的时间字符串
     *
     * @param timeStr
     * @return 时、分、12小时制时0为上午，1为下午，-1则说明是24小时制
     */
    private String parseTimeStr(String timeStr) {
        if (timeStr == null || !timeStr.matches("^(([0,1][0-9])|(2[0-3])):([0-5][0-9])$")) {
            return null;//传入的时间不合法
        }

        StringBuilder resultBuilder = new StringBuilder();
        int offsetIndex = timeStr.indexOf(":");
        int hour = Integer.valueOf(timeStr.substring(0, offsetIndex));
        if (mTimePicker == null) {//还没初始化，按24小时制处理
            return timeStr;

        } else {
            if (mTimePicker.is12_Hour()) {//TimePicker已经初始化就要判断小时制
                if (hour >= 12) {
                    resultBuilder.append(mDPLManager.getAmPmStr()[1]).append(" ").append(TimePicker.fillZero(hour - 12));
                } else {
                    resultBuilder.append(mDPLManager.getAmPmStr()[0]).append(" ").append(TimePicker.fillZero(hour));
                }
            } else {
                resultBuilder.append(TimePicker.fillZero(hour));
            }
        }

        resultBuilder.append(":").append(timeStr.substring(offsetIndex + 1));
        return resultBuilder.toString();
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
        this.selectedDateStr = new SimpleDateFormat("yyyy-M-d", Locale.CHINA).format(date);
        String timeFormatStr;
        if (mTimePicker != null && mTimePicker.is12_Hour()) {
            timeFormatStr = "a hh:mm";
        } else {
            timeFormatStr = "HH:mm";
        }
        this.selectedTimeStr = new SimpleDateFormat(timeFormatStr, mDPLManager.getLocale()).format(date);

        if (!hasInit) {
            return;
        }

        updateValue(mDPLManager.getDateFormat().format(date), new SimpleDateFormat("HH:mm", mDPLManager.getLocale()).format(date));
    }

    //该方法里面的selectedTimeStr已经经过12/24小时制的处理
    private void updateValue(String formatSelectedDateStr, String selectedTimeOf24Hour) {
        cbDateBtn.setText(formatSelectedDateStr);
        cbTimeBtn.setText(selectedTimeStr);

        if (mMode == MODE_DATE_AND_TIME) {
            mCalendarPicker.setSelectedDay(selectedDateStr);
            mTimePicker.setSelectedTime(selectedTimeOf24Hour);

        } else if (mMode == MODE_DATE_ONLY) {
            mTvOnlyOneSwitch.setText(formatSelectedDateStr);
            mCalendarPicker.setSelectedDay(selectedDateStr);

        } else if (mMode == MODE_TIME_ONLY) {
            mTvOnlyOneSwitch.setText(selectedTimeStr);
            mTimePicker.setSelectedTime(selectedTimeOf24Hour);
        }
    }

    /**
     * 显示某个月的视图，不选中任一天，也不清除已选中的日期
     *
     * @param year  要显示的月份所在的年份
     * @param month 要显示的月份
     */
    public void showMonth(int year, int month) {
        checkInit();
        mCalendarPicker.setShowMonth(year, month);
    }

    private void checkCbDateBtn(boolean isChecked) {
        if (isChecked) {
            cbTimeBtn.setTextColor(ContextCompat.getColor(getContext(), R.color.blue_date_picker));
            cbDateBtn.setTextColor(Color.WHITE);
            mLlCalendarPicker.setVisibility(View.VISIBLE);
            mTimePicker.setVisibility(View.GONE);

            clDatePicker.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.grey_date_picker));
            vInflater.getLayoutParams().height = 0;

        } else {
            cbTimeBtn.setTextColor(Color.WHITE);
            cbDateBtn.setTextColor(ContextCompat.getColor(getContext(), R.color.blue_date_picker));
            mLlCalendarPicker.setVisibility(View.GONE);
            mTimePicker.setVisibility(View.VISIBLE);

            clDatePicker.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));
            vInflater.getLayoutParams().height = mLlCalendarPicker.getHeight();
        }
    }

    public void setOnDatePickListener(OnDatePickListener onDatePickListener) {
        this.onDatePickListener = onDatePickListener;
    }

    public interface OnDatePickListener {
        /**
         * @param date the date selected is in format of "****-**-**".
         *             年月日格式如"2019-7-12", "2019-11-1"
         * @param time the time selected is in format of "**:**", and it is 24-hour time.
         *             时分格式如"20:15", "09:00"
         */
        void onDatePick(String date, String time);
    }

    private boolean checkInit() {
        if (!hasInit) throw new RuntimeException("you had never invoked the show()!");
        else return true;
    }

    private Date getDateFromStr(String dateStr) {
        String[] dateDatas = dateStr.split("-");

        Calendar calendar = Calendar.getInstance(mDPLManager.getLocale());
        calendar.set(Calendar.YEAR, Integer.valueOf(dateDatas[0]));
        calendar.set(Calendar.MONTH, Integer.valueOf(dateDatas[1]) - 1);
        calendar.set(Calendar.DAY_OF_MONTH, Integer.valueOf(dateDatas[2]));
        return calendar.getTime();
    }
}
