package com.testdemo.testVerticalScrollView;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

/**
 * Created by Administrator on 2018/1/25.
 * 可以上下拖动并通过距离判断是否切换页面，已完成
 */
public class ThreeScrollView2 extends ScrollView {
    private final static String TAG = "ThreeScroll-greyson";
    private final static int INDEX_FIRST_VIEW = 0;//显示第几屏的对应下标
    private final static int INDEX_SECOND_VIEW = 1;
    private final static int INDEX_THIRD_VIEW = 2;

    private View firstView;
    private View secondView;
    private View thirdView;
    private View holdUpView;

    private float firstViewY;//firstView在scrollView中的位置
    private float holdUpViewY;//holdUpView在scrollView中的位置
    private float secondViewY;//secondView在scrollView中的位置
    private float thirdViewY;//thirdView在scrollView中的位置

    private float secondMarginTop;

    private float firstTouchY;//第一次触摸屏幕的点
    private float lastY;//每次滑动的上一次触摸点
    /**
     * 上一次滚动结束时scroll所在位置
     */
    private float lastScrolledY = 0;
    private float canScrollGap = 120;//像素点，在构造方法中换算成dp
    private int windowHeight;

    private int displayIndex = INDEX_FIRST_VIEW;//表示显示第几屏

    public ThreeScrollView2(Context context, AttributeSet attrs) {
        super(context, attrs);
        windowHeight = ((Activity) context).getWindowManager().getDefaultDisplay().getHeight();
        canScrollGap = dip2px(context, (int)canScrollGap);
        Log.i(TAG, "windowHeight=" + windowHeight + " - canScrollGap=" + canScrollGap);
    }


    public void setFirstView(View firstView) {
        this.firstView = firstView;
    }

    public void setSecondView(View secondView) {
        this.secondView = secondView;
//        MarginLayoutParams marginLayoutParams = (MarginLayoutParams) secondView.getLayoutParams();
//        secondMarginTop = marginLayoutParams.topMargin;
        secondMarginTop = dip2px(secondView.getContext(), 250);
    }
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }


    public void setThirdView(View thirdView) {
        this.thirdView = thirdView;
    }

    public void setHoldUpView(View holdUpView) {
        this.holdUpView = holdUpView;
        holdUpView.addOnLayoutChangeListener(new OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                holdUpViewY = top;
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            onTouchEvent(ev);
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (mOnScrollChangeListener != null) {
            mOnScrollChangeListener.onScrollChanged(l, t, oldl, oldt);
            if (displayIndex == INDEX_FIRST_VIEW || displayIndex == INDEX_SECOND_VIEW) {
                mOnScrollChangeListener.onFirstScrollToSecondChange((float) t, 0.0f, holdUpViewY);
            }
        }
        Log.i(TAG, "onScrollChanged || t=" +t+ " - oldt=" + oldt);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (firstView == null || secondView == null || holdUpView == null || thirdView == null) {
            return super.onTouchEvent(event);
        }

        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.i(TAG, "onTouchEvent Down");

                lastY = firstTouchY = y;
                holdUpViewY = holdUpView.getY();//相对scrollView的位置
                secondViewY = secondView.getY();
                thirdViewY = thirdView.getY();
                break;

            case MotionEvent.ACTION_UP:
                Log.i(TAG, "onTouchEvent Up");

                float offsetTotal = y - firstTouchY;//负数为向上划

                switch (displayIndex) {
                    case INDEX_FIRST_VIEW:
                        if (offsetTotal < -canScrollGap) {
                            scrollFromTo(lastScrolledY, holdUpViewY);
                            lastScrolledY = holdUpViewY;
                            displayIndex = INDEX_SECOND_VIEW;
                        } else if (offsetTotal < 0) {
                            scrollFromTo(lastScrolledY, 0);
                            lastScrolledY = 0;
                        }
                        break;

                    case INDEX_SECOND_VIEW:
                        if (offsetTotal < -canScrollGap) {
                            scrollFromTo(lastScrolledY, thirdViewY);
                            lastScrolledY = thirdViewY;
                            displayIndex = INDEX_THIRD_VIEW;
                        } else if (offsetTotal > canScrollGap) {
                            scrollFromTo(lastScrolledY, 0);
                            lastScrolledY = 0;
                            displayIndex = INDEX_FIRST_VIEW;
                        } else {
                            scrollFromTo(lastScrolledY, holdUpViewY);
                            lastScrolledY = holdUpViewY;
                        }
                        break;

                    case INDEX_THIRD_VIEW:
                        if (offsetTotal > canScrollGap) {
                            scrollFromTo(lastScrolledY, holdUpViewY);
                            lastScrolledY = holdUpViewY;
                            displayIndex = INDEX_SECOND_VIEW;
                        } else {
                            scrollFromTo(lastScrolledY, thirdViewY);
                            lastScrolledY = thirdViewY;
                        }
                        break;
                }
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

    private void switchBetweenFirstAndSecond(boolean firstToSecond) {
        final float y = holdUpView.getY();

        ValueAnimator animator;
        if (firstToSecond) {
            animator = ValueAnimator.ofFloat(0.0f, 1.0f);//scrollView要滚到的位置，从原始到目的地（0->1）
        } else {
            animator = ValueAnimator.ofFloat(1.0f, 0.0f);
        }
        animator.setDuration(300);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (Float) animation.getAnimatedValue();
                scrollTo(0, (int) (y * value));

                MarginLayoutParams marginLayoutParams = (MarginLayoutParams) secondView.getLayoutParams();
                marginLayoutParams.topMargin = (int) (secondMarginTop * (1 - value));
                secondView.setLayoutParams(marginLayoutParams);
            }
        });
        animator.start();
    }

    private void switchBetweenSecondAndThird(boolean secondToThird) {
        holdUpViewY = holdUpView.getY();
        final float offset = thirdView.getY() - holdUpViewY;

        ValueAnimator animator;
        if (secondToThird) {
            animator = ValueAnimator.ofFloat(0.0f, 1.0f);
        } else {
            animator = ValueAnimator.ofFloat(1.0f, 0.0f);
        }
        animator.setDuration(300);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (Float) animation.getAnimatedValue();
                scrollTo(0, (int) (holdUpViewY + offset * value));
            }
        });
        animator.start();
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
        Log.i(TAG, "onNestedScroll");
    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
        Log.i(TAG, "onOverScrolled");
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
