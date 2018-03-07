package com.testdemo.testVerticalScrollView;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.testdemo.R;

/**
 * Created by Administrator on 2018/1/25.
 */
public class ThreePageView extends ViewGroup {

    View firstView;
    View secondView;
    TextView contentTV;

    int windowHeight;
    int windowWidth;

    public ThreePageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        windowHeight = ((Activity) context).getWindowManager().getDefaultDisplay().getHeight();
        windowWidth = ((Activity) context).getWindowManager().getDefaultDisplay().getWidth();
        Log.i("greyson", "windowHeight="+windowHeight);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.i("greyson", "onMeasure=" + MeasureSpec.getSize(widthMeasureSpec) + "-" + MeasureSpec.getSize(heightMeasureSpec));
        // 获取菜单并且测量宽高
        View menuView = this.getChildAt(0);
        // menuView.getLayoutParams().width拿到布局参数 heightMeasureSpec屏幕高度
        menuView.measure(widthMeasureSpec, heightMeasureSpec);

        // 获取主界面并且测量宽高
        View mainView = this.getChildAt(1);
        mainView.measure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        firstView = getChildAt(0);
        contentTV = (TextView) firstView.findViewById(R.id.contentTV);
        secondView = getChildAt(1);

        Log.i("greyson", "first="+firstView.getMeasuredWidth() + "-" +firstView.getMeasuredHeight());
        Log.i("greyson", "second="+secondView.getMeasuredWidth() + "-" +secondView.getMeasuredHeight());
        Log.i("greyson", "l="+l + "-" + t + "-" + r+ "-" +b);
        firstView.layout(l, t, r, b);
        secondView.layout(l, t + windowHeight, r, b + windowHeight);
    }


    int lastX, lastY, rawY, contentRawY;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastY = y;
                lastX = x;

                rawY = y;
                contentRawY = (int) contentTV.getY();
                Log.i("greyson", "secondLayout's x=" + secondView.getX() + " - y=" + secondView.getY());
                break;

            case MotionEvent.ACTION_MOVE:
                contentTV.offsetTopAndBottom((y - lastY) * 2 / 3);
//                contentTV.scrollTo(x, y);
                lastY = y;
                lastX = x;
                break;

            case MotionEvent.ACTION_UP:
                Log.i("greyson", "windowHeight=" + windowHeight + "|" + contentTV.getPivotY() + "|" + contentTV.getTranslationY()+"|"
                        + contentTV.getRotationY()+ "|" + contentTV.getScaleY() + "|" + contentTV.getScrollY()+"|"+contentTV.getY());
                int offset = rawY - y;
                if (offset > 0) {
                    if (offset > windowHeight / 3) {
                        contentTV.offsetTopAndBottom(50 - Float.valueOf(contentTV.getY()).intValue());
                        translateUpAnim();
                    } else {
                        contentTV.setY(contentRawY);
                    }
                } else {
                    if (-offset > windowHeight / 3) {
                        contentTV.offsetTopAndBottom(270 - Float.valueOf(contentTV.getY()).intValue());
//                        contentTV.setY(500);
//                        translateDownAnim();
                    } else {
                        contentTV.setY(contentRawY);
                    }
                }
                break;

        }
        return true;
    }
    private void translateUpAnim() {
        float offsest = contentTV.getY() + contentTV.getHeight() - secondView.getY();
        ObjectAnimator animator = ObjectAnimator.ofFloat(secondView, "translationY", 0f, offsest);
        animator.setDuration(500);
        animator.start();
    }
    private void translateDownAnim() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(secondView, "translationY", 0f, windowHeight - secondView.getHeight());
        animator.setDuration(500);
        animator.start();
    }
}
