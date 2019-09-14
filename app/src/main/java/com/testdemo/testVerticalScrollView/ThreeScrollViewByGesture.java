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
 * Created by Greyson on 2018/1/25.
 * 根据上下划的手势切换页面
 */
public class ThreeScrollViewByGesture extends ScrollView {

    private View firstView;
    private View secondView;
    private View thirdView;
    private View holdUpView;

    private float firstViewY;//firstView在scrollView中的位置
    private float holdUpViewY;//holdUpView在scrollView中的位置
    private float secondViewY;//secondView在scrollView中的位置
    private float thirdViewY;//thirdView在scrollView中的位置

    private float secondMarginTop;

    private float firstTouchY;
    private float canScrollGap = 80;
    private int windowHeight;

    private int displayMode = 0;

    public ThreeScrollViewByGesture(Context context, AttributeSet attrs) {
        super(context, attrs);
        windowHeight = ((Activity) context).getWindowManager().getDefaultDisplay().getHeight();

        Log.i("greyson", "windowHeight=" + windowHeight);
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
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        /*if (getChildCount() > 0) {
            ViewGroup viewGroup = (ViewGroup) getChildAt(0);
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                if (i == 2) {
                    View view = viewGroup.getChildAt(2);
                    Log.i("greyson", "child-2=" + view);
                    int temp = thirdView.getMeasuredHeight();
                    int thirdHeightSpec = MeasureSpec.makeMeasureSpec(windowHeight, MeasureSpec.AT_MOST);
                    view.measure(widthMeasureSpec, thirdHeightSpec);
//                setMeasuredDimension(getMeasuredWidth(), getMeasuredHeight() + windowHeight - temp);
                    break;
                }
                Log.i("greyson", "child-" + i + "=" + getChildAt(i));
//                viewGroup.getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
            }
//            setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), windowHeight * 3);
        }*/
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        /*if (getChildCount() >= 3) {
            Log.i("greyson", "onLayout  " + l + "-" + t + "-" + r + "-" + b + "-");
            View view = getChildAt(2);
            view.layout(l, t, r, l + windowHeight);
        }*/
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (firstView == null || secondView == null || holdUpView == null || thirdView == null) {
            return super.onTouchEvent(event);
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                firstTouchY = event.getY();
                break;

            case MotionEvent.ACTION_UP:
                float offset = event.getY() - firstTouchY;
                switch (displayMode) {
                    case 0:
                        if (offset < -canScrollGap) {
                            switchBetweenFirstAndSecond(true);
                            displayMode = 1;
                        }
                        break;

                    case 1:
                        if (offset < -canScrollGap) {
                            switchBetweenSecondAndThird(true);
                            displayMode = 2;
                        } else if (offset > canScrollGap) {
                            switchBetweenFirstAndSecond(false);
                            displayMode = 0;
                        }
                        break;

                    case 2:
                        if (offset > canScrollGap) {
                            switchBetweenSecondAndThird(false);
                            displayMode = 1;
                        }
                        break;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                break;
        }
        return true;
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
}
