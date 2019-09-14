package com.testdemo.testVerticalScrollView;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.testdemo.R;

/**
 * Created by Greyson on 2018/7/11.
 */

public class IndexPointLayout extends LinearLayout {


    public IndexPointLayout(Context context) {
        super(context);
    }

    public IndexPointLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public IndexPointLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * @param count
     * @param selectIndex 最小值为1
     */
    public void setPointCount(int count, int selectIndex) {
        for (int i = 0; i < count; i++) {
            TextView view = new TextView(getContext());
            if (selectIndex - 1 == i) {
                view.setBackgroundResource(R.drawable.bg_corner18_grad_fe820f);
            } else {
                view.setBackgroundResource(R.drawable.background);
            }

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dip2px(getContext(), 2.5f), dip2px(getContext(), 2.5f));
            params.leftMargin = dip2px(getContext(), 2.5f);
            params.rightMargin = dip2px(getContext(), 2.5f);

            addView(view, params);
        }
    }

    public void setSelection(int index) {
        if (index > getChildCount()) {
            return;
        }
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            if (index - 1 == i) {
                view.setBackgroundResource(R.drawable.bg_corner18_grad_fe820f);
            } else {
                view.setBackgroundResource(R.drawable.background);
            }
        }
    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}
