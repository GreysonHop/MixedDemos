package com.testdemo.testSpecialEditLayout;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.testdemo.R;

import java.util.ArrayList;
import java.util.List;

public class MenuLinearLayout extends LinearLayout {

    private TextView mNextPage;
    private TextView mPreviousPage;

    private List<TextView> mTvMenuList = new ArrayList<>();
    private List<String> mMenuList;

    public MenuLinearLayout(Context context) {
        super(context);
        mNextPage = new TextView(getContext());
        mNextPage.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.list_icon_next);
        mNextPage.setPadding(dp2px(7f), dp2px(5f), dp2px(5f), dp2px(7f));
        mPreviousPage = new TextView(getContext());
        mPreviousPage.setCompoundDrawablesWithIntrinsicBounds(R.drawable.refresh, 0, 0, 0);
        mPreviousPage.setPadding(dp2px(7f), dp2px(5f), dp2px(5f), dp2px(7f));

        setBackgroundResource(R.drawable.shape_corner4_black);
        setGravity(Gravity.CENTER_VERTICAL);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    public void setMenuList(List<String> menuList) {
        mMenuList = menuList;
        if (menuList == null) return;

        int size = menuList.size();
        for (int i = 0; i < size; i++) {
            String s = menuList.get(i);
            TextView tv = new TextView(getContext());
            tv.setTextColor(Color.WHITE);
//            tv.setBackgroundResource();
            tv.setPadding(dp2px(7f), dp2px(5f), dp2px(5f), dp2px(7f));
            tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
            tv.setText(s);
            tv.setOnClickListener((view) -> {
                if (onMenuClickListener != null) {
                    onMenuClickListener.onMenuClick(view);
                }
            });

            mTvMenuList.add(tv);
            addView(tv);
            if (i != size - 1) {
                View line = new View(getContext());
                LayoutParams lp = new LayoutParams(dp2px(1f), dp2px(25f));
                line.setLayoutParams(lp);
                line.setBackgroundColor(Color.WHITE);
                addView(line);
            }
        }
    }


    private OnMenuClickListener onMenuClickListener;

    public OnMenuClickListener getOnMenuClickListener() {
        return onMenuClickListener;
    }

    public void setOnMenuClickListener(OnMenuClickListener onMenuClickListener) {
        this.onMenuClickListener = onMenuClickListener;
    }

    interface OnMenuClickListener {
        void onMenuClick(View view);
    }

    public int dp2px(float dp) {
        float scale = getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}
