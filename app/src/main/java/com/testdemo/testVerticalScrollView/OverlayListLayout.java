package com.testdemo.testVerticalScrollView;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;

import com.testdemo.R;
import com.testdemo.broken_lib.Utils;

import java.util.ArrayList;

/**
 * Created by Greyson on 2018/7/17.
 */

public class OverlayListLayout extends FrameLayout {

    //    ArrayList<View> cacheViewList = new ArrayList<>();
    BaseAdapter adapter;
    int itemWidth;

    public OverlayListLayout(@NonNull Context context) {
        super(context);
    }

    public OverlayListLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.OverlayListLayout);
        itemWidth = (int) array.getDimension(R.styleable.OverlayListLayout_item_width, 0);
        array.recycle();
    }

    public OverlayListLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setAdapter(BaseAdapter adapter) {
        removeAllViews();
        this.adapter = adapter;
        int count = adapter.getCount();
        for (int i = 0; i < count; i++) {
            View view = adapter.getView(i, null, this);
//            cacheViewList.add(view);
            addView(view);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int count = getChildCount();
        boolean antitone = true;//是否反序，即第一个子View离左边最远
        for (int i = 0; i < count; i++) {
            View view = getChildAt(i);
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
            if (antitone) {
                params.leftMargin = height * 2 / 3 * (count - 1 - i);
            } else {
                params.leftMargin = height * 2 / 3 * i;
            }
            view.setLayoutParams(params);
            view.measure(heightMeasureSpec, heightMeasureSpec);
        }

        /*switch (MeasureSpec.getMode(widthMeasureSpec)) {
            case MeasureSpec.UNSPECIFIED:

                break;

            case MeasureSpec.AT_MOST:

                break;

            case MeasureSpec.EXACTLY:
                setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), height);
                break;
        }*/
        setMeasuredDimension(height * count - height / 3 * (count - 1), heightMeasureSpec);
    }
}
