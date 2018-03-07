package com.testdemo;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 通过动画做一个从屏幕下外面慢慢向上移动的组件，和可上下拉的组件
 * Created by Administrator on 2018/1/25.
 */
public class TestActivity2 extends Activity implements View.OnClickListener {
    private final static String TAG = "greyson_Test2";

    LinearLayout shareLayout;
    ImageView bgIV;
    View blackBgIV;
    Button clickBtn;
    boolean isSee = false;

    private View dragLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_act2);

        bgIV = (ImageView) findViewById(R.id.bgIV);
        blackBgIV = findViewById(R.id.blackBgIV);
        shareLayout = (LinearLayout) findViewById(R.id.shareLayout);
        clickBtn = (Button) findViewById(R.id.clickBtn);

        dragLayout = findViewById(R.id.dragLayout);
        findViewById(R.id.dragTV).setOnClickListener(this);

//        bgIV.setOnClickListener(this);
        blackBgIV.setOnClickListener(this);
        clickBtn.setOnClickListener(this);
        shareLayout.setOnClickListener(this);

        ObjectAnimator scaleAnim = ObjectAnimator.ofFloat(dragLayout, "center", 0f, 1f);
        scaleAnim.setDuration(1000);
        scaleAnim.start();
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
//            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(dragLayout, "translationY", 110f);
//            objectAnimator.setDuration(500);
//            objectAnimator.start();
            /*TranslateAnimation translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0
                    , Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1);
            translateAnimation.setDuration(500);
            translateAnimation.setFillAfter(true);
            dragLayout.startAnimation(translateAnimation);*/


            isSee = !isSee;
            showShareLayout(isSee);
            return;
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
