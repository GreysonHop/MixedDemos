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

    public ThreePageLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ThreePageLinearLayout);
        mMarginBottom = (int) array.getDimension(R.styleable.ThreePageLinearLayout_marginBottom, 0);
        mMarginTop = (int) array.getDimension(R.styleable.ThreePageLinearLayout_marginTop, 0);

        mWindowHeight = ((Activity) context).getWindowManager().getDefaultDisplay().getHeight();
        mStatusBarHeight = (int) getStatusBarHeight(getContext());

        Log.i(TAG, "mWindowHeight="+mWindowHeight + " - " +mMarginBottom + mMarginTop);
        array.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);//自定义组件的宽
        int currentHeight = 0;//自定义组件的高度

        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View childView = getChildAt(i);
            if (i == 0) {
                int displayHeight = mWindowHeight - mMarginBottom - mStatusBarHeight - mMarginTop;
                Log.i(TAG, "onMeasure's display = " + displayHeight);
                currentHeight += displayHeight;
                childView.measure(widthMeasureSpec, heightMeasureSpec);
                continue;
            }
            //后面两个子View高度默认为屏幕高度，所以在此不计算
            childView.measure(widthMeasureSpec, heightMeasureSpec);
        }
        setMeasuredDimension(width, mWindowHeight * (count - 1) + currentHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
//        super.onLayout(changed, l, t, r, b);
        int layoutY = t;//子组件左上点的Y坐标，不包含margin

        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View childView = getChildAt(i);
            if (i == 0) {
                int viewHeight = childView.getMeasuredHeight();
//                int viewTopY = mWindowHeight - mMarginBottom - viewHeight - mStatusBarHeight;
                int viewBottomY = layoutY + mWindowHeight - mMarginBottom - mMarginTop - mStatusBarHeight;

                childView.layout(l, layoutY, r, viewBottomY);
                layoutY = viewBottomY;

                Log.i(TAG, "onLayout's firstView's height=" + viewHeight);
                continue;
            }

            if (i == 1) {
                MarginLayoutParams marginLayoutParams = (MarginLayoutParams) childView.getLayoutParams();
                int topMargin = marginLayoutParams.topMargin;
                Log.i(TAG, "onLayout topMargin=" +topMargin);
                childView.layout(l, layoutY+topMargin, r, layoutY + topMargin + (mWindowHeight - childView.getMeasuredHeight() - topMargin));
                layoutY = layoutY + topMargin + (mWindowHeight - childView.getMeasuredHeight() - topMargin);
                continue;
            }

            childView.layout(l, layoutY, r, layoutY + mWindowHeight);
            layoutY = layoutY + mWindowHeight;
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

    private double getStatusBarHeight(Context context){
        double statusBarHeight = Math.ceil(25 * context.getResources().getDisplayMetrics().density);
        return statusBarHeight;
    }

}
