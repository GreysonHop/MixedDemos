package com.testdemo.testVerticalScrollView;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.testdemo.R;
import com.testdemo.testVerticalScrollView.ThreeScrollView;

/**
 * Created by Administrator on 2018/1/25.
 */
public class TestActivity3 extends Activity implements ThreeScrollView.OnScrollChangeListener{
    private final static String TAG = "Test3-greyson";

    private View blurBgIV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_act3);

        ThreeScrollView threeScrollView = (ThreeScrollView) findViewById(R.id.threeScrollView);

        threeScrollView.addChildView(findViewById(R.id.firstView));
        threeScrollView.addChildView(findViewById(R.id.holdUpView));
        threeScrollView.addChildView(findViewById(R.id.thirdView));
        threeScrollView.addChildView(findViewById(R.id.forthView));

        threeScrollView.setOnScrollChangeListener(this);

        blurBgIV = findViewById(R.id.blurBgIV);

    }

    private boolean isLongTest = false;
    private String[] strings = new String[]{
            "1963年进入东映动画公司，从事动画师工作。\n" +
                    "            1971年加入手冢治虫成立的““虫Production动画部”。197\n" +
                    "            4年加入Zuiyou映像与高田勋、小田部羊一共同创作《阿尔卑斯山的少女》 。",
            "1963年进入东映动画公司，从事动画师工作。\n" +
                    "            1971年加入手冢治虫成立的““虫Production动画部” 。"
    };

    @Override
    public void onScrollChanged(int l, int t, int oldl, int oldt) {
    }

    @Override
    public void onFirstScrollToSecondChange(float nowY, float startY, float endY) {
        float value = (nowY - startY) / (endY - startY);
        blurBgIV.setAlpha(value);

//        Log.i(TAG, "onScrollChanged: " +nowY+ " - " +startY+ " - " +endY);
//        Log.i(TAG, "onFirstScrollToSecondChange: " + value);
    }

    public void onClick(View view) {
        Log.i(TAG, "onClick-" + view.getId());
    }
}
