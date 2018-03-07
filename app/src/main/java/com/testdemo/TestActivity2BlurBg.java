package com.testdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.testdemo.blurbehind.BlurBehind;
import com.testdemo.blurbehind.OnBlurCompleteListener;
import com.wonderkiln.blurkit.BlurLayout;

/**
 * Created by Administrator on 2018/3/6.
 */
public class TestActivity2BlurBg extends Activity {

    private FrameLayout container;
    private BlurLayout blurLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_act2_blur_bg);

        container = (FrameLayout) findViewById(R.id.container);
        blurLayout = (BlurLayout) findViewById(R.id.blurLayout);

        Log.i("greyson", "" + getCallingActivity() + "\n" + getParent());


        blurLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BlurBehind.getInstance().execute(TestActivity2BlurBg.this, new OnBlurCompleteListener() {
                    @Override
                    public void onBlurComplete() {
                        BlurBehind.getInstance()
//                .withAlpha(100)
//                .withFilterColor(Color.parseColor("#FF171619"))
                                .setBackground(TestActivity2BlurBg.this);
                    }
                });
            }
        });
    }
}
