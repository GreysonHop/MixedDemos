package com.testdemo.testNestedScroll.raw;

import android.content.Context;
import android.util.AttributeSet;

import androidx.recyclerview.widget.LinearLayoutManager;

public class CustomLayoutManager extends LinearLayoutManager {

    public static int SCROLL_NO_LIMIT = 0;
    public static int SCROLL_BAN_HORIZON = 1;
    public static int SCROLL_BAN_VERTICAL = 2;
    public static int SCROLL_LIMIT_ALL = 3;
    public static int SCROLL_ENABLE_HORIZON = 4;
    public static int SCROLL_ENABLE_VERTICAL = 4;

    private int scrollMode = SCROLL_NO_LIMIT;

    public CustomLayoutManager(Context context) {
        super(context);
    }

    public CustomLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /*public CustomLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
    }*/

    public void setScrollMode(int mode) {
        this.scrollMode = mode;
    }

    @Override
    public boolean canScrollVertically() {
        if (scrollMode == SCROLL_ENABLE_VERTICAL) return true;
        if (scrollMode == SCROLL_LIMIT_ALL || scrollMode == SCROLL_BAN_VERTICAL) return false;
        return super.canScrollVertically();
    }

    @Override
    public boolean canScrollHorizontally() {
        if (scrollMode == SCROLL_ENABLE_HORIZON) return true;
        if (scrollMode == SCROLL_LIMIT_ALL || scrollMode == SCROLL_BAN_HORIZON) return false;
        return super.canScrollHorizontally();
    }
}
