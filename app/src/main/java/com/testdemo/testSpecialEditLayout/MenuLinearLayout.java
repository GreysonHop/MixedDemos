package com.testdemo.testSpecialEditLayout;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.Shape;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
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
        initPageSwitchBtn();
    }

    private void initPageSwitchBtn() {
        mNextPage = new TextView(getContext());
        mNextPage.setGravity(Gravity.CENTER);
//        mNextPage.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.list_icon_next);
        mNextPage.setPadding(dp2px(8f), dp2px(5f), dp2px(10f), dp2px(5f));
        mNextPage.setOnClickListener((v) -> showNextPage());

        mPreviousPage = new TextView(getContext());
        mPreviousPage.setGravity(Gravity.CENTER);
//        mPreviousPage.setCompoundDrawablesWithIntrinsicBounds(R.drawable.refresh, 0, 0, 0);
        mPreviousPage.setPadding(dp2px(10f), dp2px(5f), dp2px(8f), dp2px(5f));
        mPreviousPage.setOnClickListener((v) -> showPreviousPage());

        setBackgroundResource(R.drawable.shape_corner4_black);
        setGravity(Gravity.CENTER_VERTICAL);

        float triangleWidth = dp2pxFloat(10f);
        float triangleHeight = dp2pxFloat(6f);
        /*Path rightTrianglePath = new Path();
        rightTrianglePath.moveTo(0f, 0f);
        rightTrianglePath.lineTo(0f, triangleWidth);
        rightTrianglePath.lineTo(triangleHeight, triangleWidth / 2);
        rightTrianglePath.close();
        PathShape rightTriangleShape = new PathShape(rightTrianglePath, triangleHeight*3, triangleWidth*3);
        ShapeDrawable rightTriangleDrawable = new ShapeDrawable(rightTriangleShape);
        mNextPage.setCompoundDrawablesWithIntrinsicBounds(null, null, rightTriangleDrawable, null);//这种方式不行吗？
        */

        Drawable rightTriangleDrawable = new Drawable() {
            @Override
            public void draw(@NonNull Canvas canvas) {
                Paint paint = new Paint();
                paint.setColor(Color.WHITE);
                paint.setStyle(Paint.Style.FILL);
                Path path = new Path();
                path.moveTo(0f, 0f);
                path.lineTo(0f, triangleWidth);
                path.lineTo(triangleHeight, triangleWidth / 2);
                path.close();
                canvas.drawPath(path, paint);
            }

            @Override
            public void setAlpha(int alpha) {
            }

            @Override
            public void setColorFilter(@Nullable ColorFilter colorFilter) {
            }

            @Override
            public int getOpacity() {
                return PixelFormat.TRANSLUCENT;
            }
            @Override
            public int getIntrinsicWidth() {
                return (int) triangleHeight;
            }

            @Override
            public int getIntrinsicHeight() {
                return (int) triangleWidth;
            }
        };
        mNextPage.setCompoundDrawablesWithIntrinsicBounds(null, null, rightTriangleDrawable, null);

        Drawable leftTriangleDrawable = new Drawable() {
            @Override
            public void draw(@NonNull Canvas canvas) {
                Paint paint = new Paint();
                paint.setColor(Color.WHITE);
                paint.setStyle(Paint.Style.FILL);
                Path path = new Path();
                path.moveTo(triangleHeight, 0f);
                path.lineTo(triangleHeight, triangleWidth);
                path.lineTo(0f, triangleWidth / 2);
                path.close();
                canvas.drawPath(path, paint);
            }

            @Override
            public void setAlpha(int alpha) {
            }

            @Override
            public void setColorFilter(@Nullable ColorFilter colorFilter) {
            }

            @Override
            public int getOpacity() {
                return PixelFormat.TRANSLUCENT;
            }
            @Override
            public int getIntrinsicWidth() {
                return (int) triangleHeight;
            }

            @Override
            public int getIntrinsicHeight() {
                return (int) triangleWidth;
            }
        };
        mPreviousPage.setCompoundDrawablesWithIntrinsicBounds(leftTriangleDrawable, null, null, null);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private SparseArray<TextView> mAllMenu = new SparseArray<>();
    /**
     * indicate the last index of per page. the size equal to {@link #mShowingPage}.
     * And the page data include the index indicated by this array's value.
     */
    private int[] mLastIndexPerPage;
    /**
     * the page that indicate current menu list in show. NOTE: it begin from 0
     */
    private int mShowingPage;

    public void setMenuList(List<String> menuList) {
        mMenuList = menuList;
        if (menuList == null) return;

        int[] ints;
        int size = menuList.size();
        ints = new int[size];
        int currentWidth = 0;
        List<Integer> pagePoint = new ArrayList<>();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int widthPerPage = screenWidth / 4 * 3;

        for (int i = 0; i < size; i++) {
            String s = menuList.get(i);
            TextView tv = new TextView(getContext());
            tv.setTextColor(Color.WHITE);
//            tv.setBackgroundResource();
            tv.setPadding(dp2px(7f), dp2px(5f), dp2px(7f), dp2px(5f));
            tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
            tv.setText(s);
            tv.setGravity(Gravity.CENTER);
            tv.setOnClickListener((view) -> {
                if (onMenuClickListener != null) {
                    onMenuClickListener.onMenuClick(view);
                }
            });

            ints[i] = getViewWidth(tv);
            System.out.println("greyson tv's width = " + ints[i] + ", currentWidth = " + currentWidth + ", screen = " + widthPerPage);
            currentWidth += ints[i];
            if (currentWidth > widthPerPage) {
                pagePoint.add(i - 1);
                currentWidth = ints[i];
            }

            if (i == size - 1) {
                pagePoint.add(i);
            }

            mTvMenuList.add(tv);
            mAllMenu.put(i, tv);
        }
        mLastIndexPerPage = new int[pagePoint.size()];
        for (int i = 0; i < pagePoint.size(); i++) {
            mLastIndexPerPage[i] = pagePoint.get(i);
        }

        mShowingPage = 0;
        showMenuInPage(mShowingPage);
    }

    private void showNextPage() {
        if (mShowingPage + 1 > mLastIndexPerPage.length) {
            return;
        }
        showMenuInPage(++mShowingPage);
    }

    private void showPreviousPage() {
        if (mShowingPage - 1 < 0) {
            return;
        }
        showMenuInPage(--mShowingPage);
    }

    private void showMenuInPage(int page) {
        removeAllViews();

        int beginIndex = 0;
        if (page > 0) {
            addView(mPreviousPage);
            addView(getLineView());

            beginIndex = mLastIndexPerPage[page - 1] + 1;
        }

        int showUntilIndex = mLastIndexPerPage[page];
        for (int i = beginIndex; i <= showUntilIndex; i++) {
            addView(mTvMenuList.get(i));

            if (i == showUntilIndex) {
                if (page < mLastIndexPerPage.length - 1) {
                    addView(getLineView());
                    addView(mNextPage);
                }
            } else {
                addView(getLineView());
            }
        }
    }

    private View getLineView() {
        View line = new View(getContext());
        LayoutParams lp = new LayoutParams(dp2px(1f), LayoutParams.MATCH_PARENT);
        line.setLayoutParams(lp);
        line.setBackgroundColor(Color.WHITE);
        return line;
    }

    private int getViewWidth(View view) {
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        return view.getMeasuredWidth();
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
        return (int) dp2pxFloat(dp);
    }

    public float dp2pxFloat(float dp) {
        float scale = getResources().getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }
}
