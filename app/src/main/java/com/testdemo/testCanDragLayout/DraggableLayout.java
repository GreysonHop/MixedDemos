package com.testdemo.testCanDragLayout;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;

/**
 * Created by Administrator on 2018/2/9.
 */
public class DraggableLayout extends LinearLayout {
    private final static String TAG = "greyson_dragLayout";

    private float lastTouchY;
    private float firstTouchY;


    public DraggableLayout(Context context) {
        super(context);
    }

    public DraggableLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DraggableLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        float y = ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.i(TAG, "event down y = " + getY());
                lastTouchY = y;
                firstTouchY = y;
                break;

            case MotionEvent.ACTION_MOVE:
                if (lastTouchY - y != 0) {
                    return true;
                }
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float y = event.getY();
        switch (event.getAction()) {

            case MotionEvent.ACTION_MOVE:
                float translationOffset = y - firstTouchY;//滑动的总偏移量
                float currentY = (float) Math.sqrt(Math.abs(translationOffset)) * 2;
                if (y - firstTouchY < 0) {
                    setTranslationY(-currentY);
                } else {
                    setTranslationY(currentY);
                }
//                Log.i(TAG, "offsetSmall=" + offsetSmall + "  offset = " + offset + "  top=" + getTop());
                lastTouchY = y;
                break;

            case MotionEvent.ACTION_UP:
                Log.i(TAG, "event up y = " + getY() + "  translationY=" + getTranslationY());
                ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(this, "translationY", 0f);
                objectAnimator.setDuration(300);
//                objectAnimator.setInterpolator(new BounceInterpolator());
                objectAnimator.setInterpolator(new DecelerateInterpolator());
                objectAnimator.start();
                break;
        }
        return true;
    }
}
