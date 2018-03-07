package com.testdemo.testVerticalScrollView;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018/1/25.
 * 可以上下拖动并通过距离判断是否切换页面，可以直接添加组件，无限数量
 */
public class ThreeScrollView extends ScrollView {
    private final static String TAG = "ThreeScroll-greyson";
    private final static int INDEX_FIRST_VIEW = 0;//显示第几屏的对应下标
    private final static int INDEX_SECOND_VIEW = 1;
    private final static int INDEX_THIRD_VIEW = 2;

    private ArrayList<View> childViewList = new ArrayList<>();

    private float firstTouchY;//第一次触摸屏幕的点
    private float lastY;//每次滑动的上一次触摸点
    /**
     * 上一次滚动结束时scroll所在位置
     */
    private float lastScrolledY = 0;
    private float canScrollGap = 120;//像素点，在构造方法中换算成dp
    private int windowHeight;

    private int displayIndex = INDEX_FIRST_VIEW;//表示显示第几屏

    public ThreeScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        windowHeight = ((Activity) context).getWindowManager().getDefaultDisplay().getHeight();
        canScrollGap = dip2px(context, (int)canScrollGap);
        Log.i(TAG, "windowHeight=" + windowHeight + " - canScrollGap=" + canScrollGap);
    }

    public void addChildView(View childView) {
        childViewList.add(childView);
    }
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
//            onTouchEvent(ev);
//        }
        float y = ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.i(TAG, "event down y = " + getY());
                lastY = y;
                firstTouchY = y;
                break;

            case MotionEvent.ACTION_MOVE:
                if (Math.abs(lastY - y) > 3) {
                    return true;
                }
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (mOnScrollChangeListener != null) {
            mOnScrollChangeListener.onScrollChanged(l, t, oldl, oldt);
            if (displayIndex == INDEX_FIRST_VIEW || displayIndex == INDEX_SECOND_VIEW) {
                mOnScrollChangeListener.onFirstScrollToSecondChange((float) t, 0.0f, childViewList.get(1).getY());
            }
        }
        Log.i(TAG, "onScrollChanged || t=" +t+ " - oldt=" + oldt);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.i(TAG, "onTouchEvent Down");

//                lastY = firstTouchY = y;
                break;

            case MotionEvent.ACTION_UP:
                Log.i(TAG, "onTouchEvent Up");

                float offsetTotal = y - firstTouchY;//负数为向上划

                float scrollStart = 0;
                float scrollEnd = 0;
                if (offsetTotal < -canScrollGap) {
                    if (displayIndex + 1 < childViewList.size()) {
                        displayIndex++;
                    }
                } else if (offsetTotal > canScrollGap) {
                    if (displayIndex - 1 >= 0) {
                        displayIndex--;
                    }
                }
                scrollStart = lastScrolledY;
                scrollEnd = childViewList.get(displayIndex).getY();
                scrollFromTo(scrollStart, scrollEnd);
                lastScrolledY = scrollEnd;

                break;

            case MotionEvent.ACTION_MOVE:
                float moveOffset = lastY - y;//滚动是将view往屏幕上面移动，所以坐标是递减
                lastY = y;
                lastScrolledY += moveOffset;
                if (lastScrolledY > 0 ) {
                    scrollTo(0, (int) lastScrolledY);
                } else {
                    lastScrolledY = 0;
                }
                break;
        }
        return true;
    }

    private void scrollFromTo(final float fromY, float toY) {
        final float offset = toY - fromY;

        ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 1.0f);
        animator.setDuration(300);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (Float) animation.getAnimatedValue();
                scrollTo(0, (int) (fromY + offset * value));
            }
        });
        animator.start();
    }

    private OnScrollChangeListener mOnScrollChangeListener;

    public void setOnScrollChangeListener(OnScrollChangeListener mOnScrollChangeListener) {
        this.mOnScrollChangeListener = mOnScrollChangeListener;
    }

    public interface OnScrollChangeListener {
        void onScrollChanged(int l, int t, int oldl, int oldt);
        void onFirstScrollToSecondChange(float nowY, float startY, float endY);
    }
}
