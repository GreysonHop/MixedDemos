package com.testdemo.testSpecialEditLayout;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.luck.picture.lib.tools.ScreenUtils;

/**
 * Created by Greyson on 2018/10/15.
 */

public class ToolLayout extends LinearLayout {

    private int windowWidth;
    private int minWidth;
    private int middleWidth;
    private int maxWidth;
    private float mCanScrollGap = 60;//超过此像素长度自动滚动，在构造方法中换算成dp
    private int mCanScrollVelocity = 500;//超过此速度自动滚动

    private VelocityTracker mVelocityTracker;
    private int mPointId;
    private float firstTouchX;//第一次触摸屏幕的点
    private float lastX;//每次滑动的上一次触摸点
    private boolean mIsDragging;
    //    private boolean isZoomMode;
    public final static int MODE_EDIT_MIN = 1;
    public final static int MODE_EDIT_MIDDLE = 2;
    public final static int MODE_EDIT_MAX = 3;
    private int editTextMode = MODE_EDIT_MIDDLE;

    private ViewGroup mScaleView;
    private EditText mScaleEdit;

    public ToolLayout(Context context) {
        super(context);
        init();
    }

    public ToolLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ToolLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        windowWidth = ScreenUtils.getScreenWidth(getContext());
        minWidth = ScreenUtils.dip2px(getContext(), 65f);
        middleWidth = ScreenUtils.dip2px(getContext(), 200f);
        maxWidth = windowWidth - ScreenUtils.dip2px(getContext(), 30f);
        mCanScrollGap = ScreenUtils.dip2px(getContext(), (int) mCanScrollGap);
        Log.d("greyson", "ToolLayout's childCount in init() = " + getChildCount());
    }

    public int getEditTextMode() {
        return this.editTextMode;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.d("greyson", "ToolLayout's onMeasure() " + getChildCount());
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
        if (mScaleView == null && getChildCount() > 0 && getChildAt(0) instanceof ViewGroup) {
            mScaleView = (ViewGroup) getChildAt(0);
        }
        if (mScaleView == null) {
            return super.onInterceptTouchEvent(ev);
        }

        if (mScaleEdit == null && mScaleView.getChildCount() > 0 && mScaleView.getChildAt(0) instanceof EditText) {
            mScaleEdit = (EditText) mScaleView.getChildAt(0);
        }

        if (mScaleEdit == null) {
            return super.onInterceptTouchEvent(ev);
        }

        float x = ev.getX();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = x;
                firstTouchX = x;
                if (mVelocityTracker == null) {
                    mVelocityTracker = VelocityTracker.obtain();
                }
                mVelocityTracker.addMovement(ev);
                mPointId = ev.getPointerId(0);
                break;

            case MotionEvent.ACTION_MOVE:
                if (Math.abs(lastX - x) > 3) {
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
    public boolean onTouchEvent(MotionEvent event) {
        if (mVelocityTracker == null) {
            return super.onTouchEvent(event);
        }
        mVelocityTracker.addMovement(event);
        float x = event.getX();

        switch (event.getAction()) {

            case MotionEvent.ACTION_UP:
                final VelocityTracker velocityTracker = mVelocityTracker;
                velocityTracker.computeCurrentVelocity(1000);
                int initialVelocity = (int) velocityTracker.getXVelocity(mPointId);
                float offsetTotal = x - firstTouchX;

                LayoutParams params1 = (LayoutParams) mScaleView.getLayoutParams();
                if (offsetTotal < 0 && editTextMode == MODE_EDIT_MIDDLE) {
                    if (offsetTotal < -mCanScrollGap || initialVelocity < -mCanScrollVelocity) {
//                        isZoomMode = true;
                        editTextMode = MODE_EDIT_MIN;
                        startAnim(mScaleView, params1.width, minWidth);
                        mScaleEdit.setMaxLines(1);
                    }
                } else if (offsetTotal > 0 && editTextMode == MODE_EDIT_MIN) {
                    if (offsetTotal > mCanScrollGap || initialVelocity > mCanScrollVelocity) {
//                        isZoomMode = false;
                        editTextMode = MODE_EDIT_MIDDLE;
                        startAnim(mScaleView, params1.width, middleWidth);
                        mScaleEdit.setMaxLines(4);
                    }
                }

                recycleVelocityTracker();
                mIsDragging = false;
                break;
/*
            case MotionEvent.ACTION_MOVE:
                float offset = x - lastX;
                lastX = x;
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
                int width = params.width;
                width += offset;
                if (width < minWidth || width > middleWidth) {
                    break;
                }
                params.width = width;
                view.setLayoutParams(params);
                break;*/
        }

        return true;
    }

    private void startAnim(View view, final int fromWidth, int toWidth) {
        final int offset = toWidth - fromWidth;
        final View object = view;

        ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 1.0f);
        animator.setDuration(300);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (Float) animation.getAnimatedValue();
                LayoutParams params = (LayoutParams) object.getLayoutParams();
                params.width = (int) (fromWidth + offset * value);
                object.setLayoutParams(params);
            }
        });
        animator.start();
    }

    public void fullEdit(boolean isFull) {
        if (mScaleView == null) {
            return;
        }
        LayoutParams params = (LayoutParams) mScaleView.getLayoutParams();
        int toWidth = 0;
        if (isFull) {
            if (editTextMode == MODE_EDIT_MAX) return;
            toWidth = maxWidth;
            editTextMode = MODE_EDIT_MAX;
        } else {
            if (editTextMode == MODE_EDIT_MIDDLE) return;
            toWidth = middleWidth;
            editTextMode = MODE_EDIT_MIDDLE;
        }
        mScaleEdit.setMaxLines(4);

        startAnim(mScaleView, params.width, toWidth);
    }

}
