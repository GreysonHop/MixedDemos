package com.testdemo.testVerticalScrollView;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.testdemo.util.broken_lib.Utils;

/**
 * Created by Greyson on 2018/7/17.
 */

public class OverlayListLayout2 extends ListView {

    public OverlayListLayout2(@NonNull Context context) {
        super(context);
    }

    public OverlayListLayout2(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public OverlayListLayout2(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
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
                params.leftMargin = Utils.dp2px(20 * (count - 1 - i));
            } else {
                params.leftMargin = Utils.dp2px(20 * i);
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
        setMeasuredDimension(height * count - Utils.dp2px(10) * (count - 1), heightMeasureSpec);
    }
}
