package com.testdemo.testVerticalScrollView;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.ScrollView;

import java.util.ArrayList;

/**
 * Created by Greyson on 2018/1/25.
 * 可以上下拖动并通过距离判断是否切换页面，可以直接添加组件，无限数量
 */
public class ThreeScrollView extends ScrollView {
    private final static String TAG = "ThreeScroll-greyson";

    private ArrayList<View> childViewList = new ArrayList<>();
    private VelocityTracker mVelocityTracker;
    private int mPointId;

    private float firstTouchY;//第一次触摸屏幕的点
    private float lastY;//每次滑动的上一次触摸点
    /**
     * 上一次滚动结束时scroll所在位置
     */
    private float lastScrolledY = 0;

    private int windowHeight;
    private int displayIndex = 0;//表示显示第几屏
    private boolean mIsDragging;
    private float mCanScrollGap = 140;//超过此像素点长度自动滚动到下一个目标屏，在构造方法中换算成dp
    private int mCanScrollVelocity = 880;//超过此速度自动滚动

    public ThreeScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        windowHeight = ((Activity) context).getWindowManager().getDefaultDisplay().getHeight();
        mCanScrollGap = dip2px(context, (int) mCanScrollGap);
        Log.i(TAG, "windowHeight=" + windowHeight + " - mCanScrollGap=" + mCanScrollGap);
    }

    public void addChildView(View childView) {
        childViewList.add(childView);
    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 回收速度追踪器
     */
    private void recycleVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.clear();
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        float y = ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastY = y;
                firstTouchY = y;
                if (mVelocityTracker == null) {
                    mVelocityTracker = VelocityTracker.obtain();
                }
                mVelocityTracker.addMovement(ev);
                mPointId = ev.getPointerId(0);
                break;

            case MotionEvent.ACTION_MOVE:
                if (Math.abs(lastY - y) > 3) {
                    mIsDragging = true;
                    return true;
                }
                break;

            case MotionEvent.ACTION_UP:
                if (!mIsDragging) {
                    recycleVelocityTracker();
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
            if (displayIndex == 0 || displayIndex == 1) {
                mOnScrollChangeListener.onFirstScrollToSecondChange((float) t, 0.0f, childViewList.get(1).getY());
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (mVelocityTracker != null) {
            mVelocityTracker.addMovement(event);
        }

        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                final VelocityTracker velocityTracker = mVelocityTracker;
                velocityTracker.computeCurrentVelocity(1000);
                int initialVelocity = (int) velocityTracker.getYVelocity(mPointId);
                System.out.println("greyson initialVelocity = " + initialVelocity);

                float offsetTotal = y - firstTouchY;//滑动偏移量，负数为向上划

                float scrollStart = 0;
                float scrollEnd = 0;
                if (offsetTotal < 0 && displayIndex + 1 < childViewList.size()) {
                    if (offsetTotal < -mCanScrollGap || initialVelocity < -mCanScrollVelocity) {
                        displayIndex++;
                    }
                } else if (offsetTotal > 0 && displayIndex - 1 >= 0) {
                    if (offsetTotal > mCanScrollGap || initialVelocity > mCanScrollVelocity) {
                        displayIndex--;
                    }
                }

                scrollStart = lastScrolledY;
                scrollEnd = childViewList.get(displayIndex).getY();
                scrollFromTo(scrollStart, scrollEnd);
                lastScrolledY = scrollEnd;

                recycleVelocityTracker();
                mIsDragging = false;
                break;

            case MotionEvent.ACTION_MOVE:
                float moveOffset = lastY - y;//滚动是将view往屏幕上面移动，所以坐标是递减
                lastY = y;
                lastScrolledY += moveOffset / 2;
                if (lastScrolledY > 0) {
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
