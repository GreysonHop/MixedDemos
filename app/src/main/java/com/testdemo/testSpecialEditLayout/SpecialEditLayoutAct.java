package com.testdemo.testSpecialEditLayout;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.luck.picture.lib.tools.ScreenUtils;
import com.testdemo.R;

/**
 * Created by Greyson on 2018/10/15.
 */

public class SpecialEditLayoutAct extends Activity {

    private ToolLayout toolLayout;
    private EditText editText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_test_special_edit_layout);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//沉浸式状态栏
            Window window = getWindow();
            ViewGroup decorView = (ViewGroup) getWindow().getDecorView();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
                decorView.setSystemUiVisibility(option);
                window.setStatusBarColor(Color.TRANSPARENT);

            } else {
                WindowManager.LayoutParams attributes = window.getAttributes();
                int flagTranslucentStatus = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
                attributes.flags |= flagTranslucentStatus;
                window.setAttributes(attributes);

            }
            //根布局添加占位状态栏
            ImageView statusBarView = new ImageView(this);
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ScreenUtils.getStatusBarHeight(getBaseContext()));
            decorView.addView(statusBarView, lp);

            ViewGroup rootView = (ViewGroup) decorView.findViewById(Window.ID_ANDROID_CONTENT);
            if (rootView.getChildCount() > 0) {
                rootView.getChildAt(0).setFitsSystemWindows(true);
            }/* else {
                rootView.setPadding(0, ScreenUtils.getStatusBarHeight(getBaseContext()), 0, 0);
            }*/
            statusBarView.setBackgroundColor(Color.WHITE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            } else {
                statusBarView.setImageResource(R.drawable.bg_grad_shadow);
            }
        }

//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED);

        toolLayout = (ToolLayout) findViewById(R.id.layout_tool);
        editText = (EditText) findViewById(R.id.et_msg);

        /*editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (v == editText) {
                    toolLayout.fullEdit(hasFocus);
                }
            }
        });*/

        toolLayout.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                System.out.println("greyson onLayoutChange: " + left + " - " + right + " - " + top + " - " + bottom
                        + " \n" + oldLeft + " - " + oldRight + " - " + oldTop + " - " + oldBottom);

                Rect rect = new Rect();
                // 获取当前页面窗口的显示范围
                toolLayout.getWindowVisibleDisplayFrame(rect);
                int screenHeight = getResources().getDisplayMetrics().heightPixels;

                int keyboardHeight = screenHeight - rect.bottom; // 拟定输入法的高度
//                if (Math.abs(keyboardHeight) > screenHeight / 4) {// 超过屏幕四分之一则表示弹出了输入法
                if (Math.abs(keyboardHeight) > screenHeight / 4) {
                    toolLayout.fullEdit(true);
                } else {
                    if (toolLayout.getEditTextMode() == ToolLayout.MODE_EDIT_MIDDLE
                            || toolLayout.getEditTextMode() == ToolLayout.MODE_EDIT_MIN) {
                        return;
                    }
                    toolLayout.fullEdit(false);
                }

            }
        });

        /*final View main = toolLayout.getRootView();
        main.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        System.out.println("greyson -------");
                        Rect rect = new Rect();
                        main.getWindowVisibleDisplayFrame(rect);
                        int mainInvisibleHeight = main.getRootView().getHeight() - rect.bottom;
                        int screenHeight = main.getRootView().getHeight();//屏幕高度
                        if (mainInvisibleHeight > screenHeight / 4) {
                            toolLayout.fullEdit(true);
                        } else {
                            if (toolLayout.getEditTextMode() == ToolLayout.MODE_EDIT_MIDDLE
                                    || toolLayout.getEditTextMode() == ToolLayout.MODE_EDIT_MIN) {
                                return;
                            }
                            toolLayout.fullEdit(false);
                        }
                    }
                }
        );*/
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_emoji:
                testSomeMethod();
                break;
        }
    }

    private void testSomeMethod() {
        int h1 = toolLayout.getContext().getResources().getDisplayMetrics().heightPixels;

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int h2 = metrics.heightPixels;

        Point point = new Point();
        getWindowManager().getDefaultDisplay().getSize(point);
        int h3 = point.y;

        System.out.println("屏幕高：" + h1 + " - " + h2 + " - " + h3);

        Rect rect = new Rect();
        toolLayout.getRootView().getWindowVisibleDisplayFrame(rect);
        Rect rect2 = new Rect();
        toolLayout.getWindowVisibleDisplayFrame(rect2);
        System.out.println("rect：" + rect.height() + " - " + rect2.height()
                + "\n" + rect.hashCode() + " - " + rect2.hashCode() + "\n" + rect.bottom + " - " + rect2.bottom
                + "\n" + rect.top + " - " + rect2.top);

        System.out.println("rootView : " + toolLayout.getRootView() + " - " + editText.getRootView());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (editText.hasFocus()) {
                editText.clearFocus();
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
