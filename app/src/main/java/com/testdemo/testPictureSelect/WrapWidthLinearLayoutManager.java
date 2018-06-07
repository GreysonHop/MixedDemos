package com.testdemo.testPictureSelect;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Greyson
 */
public class WrapWidthLinearLayoutManager extends LinearLayoutManager {

    private int spanCount;
    private int spacing;
    private boolean includeEdge;

    public WrapWidthLinearLayoutManager(Context context) {
        super(context);
    }

    public WrapWidthLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public WrapWidthLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state, int widthSpec, int heightSpec) {
        final int widthMode = View.MeasureSpec.getMode(widthSpec);
        final int heightMode = View.MeasureSpec.getMode(heightSpec);
        final int widthSize = View.MeasureSpec.getSize(widthSpec);
        final int heightSize = View.MeasureSpec.getSize(heightSpec);

        for(int i = 0; i < getItemCount(); i++) {
            View view = getChildAt(i);
            if (view instanceof ImageView) {
                Log.w("greyson", "child's " + i + " image: " + view);
            }
        }
        super.onMeasure(recycler, state, widthSpec, heightSpec);
    }
}