package com.testdemo.testVerticalScrollView;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.testdemo.R;

/**
 * Created by Administrator on 2018/1/25.
 */
public class ThreePageLinearLayout extends LinearLayout {
    private final static String TAG = "ThreePage-greyson";

    private int mWindowHeight;
    private int mStatusBarHeight;
    private int mMarginBottom;//每个子View的marginBottom值
    private int mMarginTop;//每个子View的marginTop值

    //自定义组件真正显示的内容高度，即去掉状态栏、marginTop、marginBottom
    int actualHeight;

    public ThreePageLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ThreePageLinearLayout);
        mMarginBottom = (int) array.getDimension(R.styleable.ThreePageLinearLayout_marginBottom, 0);
        mMarginTop = (int) array.getDimension(R.styleable.ThreePageLinearLayout_marginTop, 0);

        mWindowHeight = ((Activity) context).getWindowManager().getDefaultDisplay().getHeight();
        mStatusBarHeight = (int) getStatusBarHeight(getContext());

        actualHeight = mWindowHeight - mMarginBottom - mStatusBarHeight - mMarginTop;

        Log.i(TAG, "mWindowHeight=" + mWindowHeight + " - " + mMarginBottom + " - " + mMarginTop + " - " + mStatusBarHeight);
        array.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);//自定义组件的宽
        int currentHeight = 0;//自定义组件的总高度
        int firstViewActualHeight = 0;//第一个子View真正显示的内容高度

        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View childView = getChildAt(i);
            childView.measure(widthMeasureSpec, heightMeasureSpec);

            int childHeight = actualHeight;
            if (i == 0) {
                firstViewActualHeight = childView.getMeasuredHeight();
                Log.i(TAG, "onMeasure's first = " + firstViewActualHeight);
            } else if (i == 1) {
                childHeight = actualHeight - firstViewActualHeight;
            }
            currentHeight += childHeight;
            Log.i(TAG, "onMeasure's i = " + i + ": " + childHeight + " - total: " + currentHeight);
        }
        setMeasuredDimension(width, currentHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int layoutY = t;//子组件左上点的Y坐标，不包含margin
        int firstViewActualHeight = 0;//第一个子View真正显示的内容高度

        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View childView = getChildAt(i);

            if (i == 0) {
                firstViewActualHeight = childView.getMeasuredHeight();
                int viewBottomY = layoutY + actualHeight;

                childView.layout(l, layoutY, r, viewBottomY);
                layoutY = viewBottomY;

                Log.i(TAG, "onLayout's firstView's height=" + firstViewActualHeight);
                continue;
            }

            if (i == 1) {
                MarginLayoutParams marginLayoutParams = (MarginLayoutParams) childView.getLayoutParams();
                int topMargin = marginLayoutParams.topMargin;
                Log.i(TAG, "onLayout topMargin=" + topMargin);

                int viewHeight = actualHeight - firstViewActualHeight;

                childView.layout(l, layoutY + topMargin, r, layoutY + topMargin + viewHeight);
                layoutY = layoutY + topMargin + viewHeight;
                continue;
            }

            childView.layout(l, layoutY, r, layoutY + actualHeight);
            layoutY = layoutY + actualHeight;
        }
    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    private double getStatusBarHeight(Context context) {
        double statusBarHeight = Math.ceil(25 * context.getResources().getDisplayMetrics().density);
        return statusBarHeight;
    }

}
