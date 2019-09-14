package com.testdemo.testCanDragScrollView;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ScrollView;

/**
 * ScrollView中，包含三个或三个以上的View，第三个和第二个重叠
 * ，可通过下拉到顶后的拉伸来展示全部。
 * 请保证ScrollView中的ViewGroup的三个子View的正确顺序（位置）
 * Created by Greyson on 2018/4/16.
 */
public class DraggableScrollView_Old extends ScrollView {
    private static String TAG = "DraggableScrollView";

    private View draggableView;
    private int draggableLength;

    private float lastY;//每次滑动的上一次触摸点
    private float draggableY;//下拉到顶时，手指所在的位置
    private boolean isDragging = false;//正在拉伸中

    public DraggableScrollView_Old(Context context) {
        super(context);
    }

    public DraggableScrollView_Old(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        postDelayed(new Runnable() {

            @Override
            public void run() {
                ViewGroup viewGroup = (ViewGroup) getChildAt(0);
                if (viewGroup.getChildCount() > 2) {
                    draggableView = viewGroup.getChildAt(2);
                    View tempView = viewGroup.getChildAt(1);
                    draggableLength = tempView.getBottom() - draggableView.getTop() + 2;
                    Log.i(TAG, "onFinishInflate " + draggableView + "\ndraggableLength = " + draggableLength
                            + "\ntempView = " + tempView.getBottom() + " - " + tempView.getHeight() + " - " + tempView.getTop()
                            + "\nview = " + draggableView.getTop() + " - " + draggableView.getHeight());
                }
            }

        }, 500);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (draggableView == null) {
            return super.onTouchEvent(ev);
        }

        float y = ev.getY();
        int mScrollY = getScrollY();

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastY = y;
                break;

            case MotionEvent.ACTION_MOVE:
                if (isDragging) {
                    if (y - lastY > 0) {
                        float offsetLength = y - draggableY;
                        if (draggableLength != 0 && draggableLength < offsetLength) {
                            offsetLength = draggableLength;
                        }
                        draggableView.setTranslationY(offsetLength);

                    } else {//向上划
                        float offsetLength = y - draggableY;//Length必须大于0，不然可能会比原来的位置还高
                        if (offsetLength < 0) {
                            offsetLength = 0;
                        }
                        draggableView.setTranslationY(offsetLength);

                        if (y < draggableY) {
                            isDragging = false;
                        }
                    }
                    return true;
                } else if (mScrollY == 0) {
                    if (y - lastY > 0) {
                        Log.i(TAG, "----向下划 = " + (y - lastY));
                        if (!isDragging) {
                            draggableY = y;
                            isDragging = true;
                        }
                    }
                }
                lastY = y;
                break;

            case MotionEvent.ACTION_UP:
                ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(draggableView, "translationY", 0f);
                objectAnimator.setDuration(300);
//                objectAnimator.setInterpolator(new BounceInterpolator());
                objectAnimator.setInterpolator(new DecelerateInterpolator());
                objectAnimator.start();
                isDragging = false;
                break;

        }

        return super.onTouchEvent(ev);
    }
}
