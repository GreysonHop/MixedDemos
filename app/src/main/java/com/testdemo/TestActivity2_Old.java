package com.testdemo;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.testdemo.broken_lib.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 通过动画做一个从屏幕下外面慢慢向上移动的组件，和可上下拉的组件
 * Created by Greyson on 2018/1/25.
 */
public class TestActivity2_Old extends Activity implements View.OnClickListener {
    private final static String TAG = "greyson_Test2";

    LinearLayout shareLayout;
    ImageView bgIV;
    View blackBgIV;
    TextView clickBtn;
    TextView popupTV;
    boolean isSee = false;

    private View dragLayout;

    private PopupWindow popupWindow;
    private ListView popupMenuView;

    private Chronometer chronometer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_act2);

        bgIV = (ImageView) findViewById(R.id.bgIV);
        blackBgIV = findViewById(R.id.blackBgIV);
        shareLayout = (LinearLayout) findViewById(R.id.shareLayout);
        clickBtn = (TextView) findViewById(R.id.anim_btn);
        popupTV = (TextView) findViewById(R.id.popupTV);

        chronometer = (Chronometer) findViewById(R.id.chronometer);
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                Log.i("greyson", "base = " + chronometer.getBase() + " - "
                        + chronometer.getText().toString() + " - " + timeTick2Second(chronometer.getText().toString()));
//                popupTV.setText(second2Minute(timeTick2Second(chronometer.getText().toString())));
            }
        });
        chronometer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chronometer.setBase(SystemClock.elapsedRealtime() - (9 * 3600 + 59 * 60 + 55) * 1000);
                chronometer.start();
            }
        });

        //倒计时
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINESE);
        try {
            Date date = sdf.parse("2019-10-09 08:29:00");//pay attention on the expire time
            long expireMilliseconds = date.getTime();
            long currentMilliseconds = System.currentTimeMillis();
            if (expireMilliseconds > currentMilliseconds) {
                CountDownTimer countDownTimer = new CountDownTimer(expireMilliseconds - currentMilliseconds, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        popupTV.setText(second2Minute(millisUntilFinished / 1000));
                    }

                    @Override
                    public void onFinish() {
                        Toast.makeText(TestActivity2_Old.this, "倒计时完成！！！", Toast.LENGTH_SHORT).show();
                        popupTV.setText("已过期");
                    }
                };
                countDownTimer.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        dragLayout = findViewById(R.id.dragLayout);
        findViewById(R.id.dragTV).setOnClickListener(this);

//        bgIV.setOnClickListener(this);
        blackBgIV.setOnClickListener(this);
        clickBtn.setOnClickListener(this);
        popupTV.setOnClickListener(this);
        shareLayout.setOnClickListener(this);

        ObjectAnimator scaleAnim = ObjectAnimator.ofFloat(dragLayout, "scaleX", 0f, 1f);
        ObjectAnimator scaleAnim2 = ObjectAnimator.ofFloat(dragLayout, "scaleY", 0f, 1f);
        AnimatorSet set = new AnimatorSet();
        set.setDuration(800);
        set.setTarget(dragLayout);
        set.playTogether(scaleAnim, scaleAnim2);
        set.start();
    }

    public static int timeTick2Second(String time) {
        String h, m, s;
        int index1 = time.indexOf(":");
        int index2 = time.lastIndexOf(":");

        if (index2 == -1) {
            return 0;
        }
        if (index1 == index2) {//时间不到1小时,00:00
            h = "0";
            m = time.substring(0, index2);
        } else {//格式 1:00:00 或 111:00:00
            h = time.substring(0, index1);
            m = time.substring(index1 + 1, index2);
        }
        s = time.substring(index2 + 1);

        return Integer.valueOf(h) * 3600 + Integer.valueOf(m) * 60 + Integer.valueOf(s);
    }

    public static String second2Minute(long second) {
        int hour = (int) (second / 3600);
        second -= hour * 3600;

        int minute = (int) (second / 60);
        second -= minute * 60;

        String minuteStr = minute < 10 ? "0" + minute : String.valueOf(minute);
        String secondStr = second < 10 ? "0" + second : String.valueOf(second);

        if (hour == 0) {
            return minuteStr + ":" + secondStr;
        } else {
            return hour + ":" + minuteStr + ":" + secondStr;
        }
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.dragTV) {
            Toast.makeText(this, "drag TV said Hello !!!!!", Toast.LENGTH_SHORT).show();
            ((TestApplication) getApplication()).setActivity(this);
            startActivity(new Intent(this, TestActivity2BlurBg.class));
            return;
        }

        if (v == blackBgIV) {
            isSee = !isSee;
            showShareLayout(isSee);
            return;
        }

        if (v == bgIV) {
            Toast.makeText(this, "you click picture bg bg!-------", Toast.LENGTH_SHORT).show();
            return;
        }
        if (v == shareLayout) {
            Toast.makeText(this, "you click shareLayout!!!!!", Toast.LENGTH_SHORT).show();
            getInviteCode();
            return;
        }

        if (v == clickBtn) {
            Log.i(TAG, "click button dragLayout's y=" + dragLayout.getY() + " - top=" + dragLayout.getTop());
            clickBtn.setSelected(!clickBtn.isSelected());

            isSee = !isSee;
            showShareLayout(isSee);
            return;
        }

        if (v == popupTV) {
            if (popupMenuView == null) {
                popupMenuView = new ListView(this);
//                popupMenuView.setBackgroundResource(R.drawable.menu);
                ArrayList<String> menuList = new ArrayList<>();
                menuList.add("日榜单");
                menuList.add("周榜单");
                menuList.add("月榜单");
                popupMenuView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_activated_1, menuList));
                popupMenuView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Toast.makeText(TestActivity2_Old.this, "you click " + position, Toast.LENGTH_SHORT).show();
                    }
                });

                popupWindow = new PopupWindow(this);
                popupWindow.setContentView(popupMenuView);
                popupWindow.setWidth(Utils.dp2px(86));
                popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
                popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_popup));
                popupWindow.setOutsideTouchable(true);
                popupWindow.setFocusable(true);
//                popupWindow.setAnimationStyle(R.style.PopupWindowAnimation);
            }
            popupWindow.showAsDropDown(popupTV);
        }
    }

    private void showShareLayout(boolean show) {
        if (show) {
            AlphaAnimation alphaInAnimation = new AlphaAnimation(0.0f, 0.9f);
            alphaInAnimation.setDuration(300);
            TranslateAnimation translateInAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                    1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
            translateInAnimation.setDuration(300);

            blackBgIV.setAnimation(alphaInAnimation);
            shareLayout.setAnimation(translateInAnimation);

            shareLayout.setVisibility(View.VISIBLE);
            blackBgIV.setVisibility(View.VISIBLE);

        } else {
            AlphaAnimation alphaOutAnimation = new AlphaAnimation(0.9f, 0.0f);
            alphaOutAnimation.setDuration(300);

            TranslateAnimation translateOutAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                    0.0f, Animation.RELATIVE_TO_SELF, 1.0f);
            translateOutAnimation.setDuration(300);

            blackBgIV.setAnimation(alphaOutAnimation);
            shareLayout.setAnimation(translateOutAnimation);

            shareLayout.setVisibility(View.GONE);
            blackBgIV.setVisibility(View.GONE);

        }
    }

    private void getInviteCode() {
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboardManager.hasText()) {
            String text = clipboardManager.getText().toString();
            Pattern pattern = Pattern.compile("(?<=station_invite_code:).{6}");
            Matcher matcher = pattern.matcher(text);

            Log.i("greyson", "clip content = " + text + " - groupCount=" + matcher.groupCount());
            if (matcher.find()) {
                String mInviteCode = matcher.group();
                Log.i("greyson", " group=" + mInviteCode);
            }
        }
    }
}
